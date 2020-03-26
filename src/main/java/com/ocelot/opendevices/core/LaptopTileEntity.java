package com.ocelot.opendevices.core;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.api.DeviceSerializers;
import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.api.computer.settings.LaptopSetting;
import com.ocelot.opendevices.api.device.Device;
import com.ocelot.opendevices.api.device.DeviceSerializer;
import com.ocelot.opendevices.api.device.DeviceTileEntity;
import com.ocelot.opendevices.api.device.process.DeviceProcess;
import com.ocelot.opendevices.api.device.process.ProcessSerializer;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.registry.DeviceProcessRegistryEntry;
import com.ocelot.opendevices.core.render.LaptopRenderer;
import com.ocelot.opendevices.core.task.ExecuteProcessTask;
import com.ocelot.opendevices.core.task.SyncProcessTask;
import com.ocelot.opendevices.core.task.SyncSettingsTask;
import com.ocelot.opendevices.core.task.TerminateProcessTask;
import com.ocelot.opendevices.init.DeviceBlocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LaptopTileEntity extends DeviceTileEntity implements Computer, ITickableTileEntity
{
    private static final AxisAlignedBB RENDER_AABB = VoxelShapes.fullCube().getBoundingBox();

    private UUID user;
    private boolean open;
    private HashMap<UUID, DeviceProcess<Computer>> processes;
    private HashSet<UUID> startingProcesses;
    private Queue<Runnable> executionQueue;

    private UUID address;
    private CompoundNBT settings;
    private LaptopDesktop desktop;
    private LaptopWindowManager windowManager;
    private LaptopTaskBar taskBar;

    //TODO make this a better system
    @OnlyIn(Dist.CLIENT)
    private float rotation;
    @OnlyIn(Dist.CLIENT)
    private float lastRotation;

    public LaptopTileEntity()
    {
        super(DeviceBlocks.TE_LAPTOP);
        this.user = null;
        this.open = false;
        this.processes = new HashMap<>();
        this.startingProcesses = new HashSet<>();
        this.executionQueue = new ConcurrentLinkedQueue<>();

        this.settings = new CompoundNBT();
        this.desktop = new LaptopDesktop();
        this.windowManager = new LaptopWindowManager(this);
        this.taskBar = new LaptopTaskBar(this);
    }

    private void startProcess(UUID processId, DeviceProcess<Computer> process)
    {
        this.processes.put(processId, process);
        if (!this.isClient())
        {
            OpenDevices.LOGGER.debug("Starting process '" + DeviceRegistries.getProcessRegistryName(process) + "' for Laptop with id '" + processId + "'");
            this.startingProcesses.add(processId);
        }
    }

    @Override
    protected void randomizeAddress()
    {
        this.address = UUID.randomUUID();
    }

    @Override
    public void tick()
    {
        super.tick();
        if (this.hasWorld())
        {
            if (this.isClient())
            {
                LaptopRenderer.update(this);

                this.lastRotation = this.rotation;
                if (!this.open)
                {
                    if (this.rotation > 0)
                    {
                        this.rotation -= 0.1f;
                    }
                    if (this.rotation < 0)
                    {
                        this.rotation = 0;
                    }
                }
                else
                {
                    if (this.rotation < 1)
                    {
                        this.rotation += 0.1f;
                    }
                    if (this.rotation > 1)
                    {
                        this.rotation = 1;
                    }
                }
            }

            if (!this.executionQueue.isEmpty())
            {
                OpenDevices.LOGGER.debug("Executing {} task(s)", this.executionQueue.size());
            }

            Runnable runnable;
            while ((runnable = this.executionQueue.poll()) != null)
            {
                runnable.run();
            }

            if (!this.processes.isEmpty())
            {
                this.processes.values().stream().filter(process -> !this.startingProcesses.contains(process.getProcessId())).forEach(DeviceProcess::update);
                if (!this.isClient())
                {
                    this.processes.values().stream().filter(DeviceProcess::isTerminated).forEach(process -> this.execute(() -> this.terminateProcess(process.getProcessId())));
                }
            }

            this.desktop.update();
            this.windowManager.update();
        }
    }

    public void initProcess(UUID processId)
    {
        this.startingProcesses.remove(processId);
        if (!this.processes.containsKey(processId))
        {
            OpenDevices.LOGGER.warn("Could not initialize process with id '" + processId + "' as it does not exist!");
            return;
        }

        this.execute(() -> this.processes.get(processId).init());
    }

    public boolean syncExecuteProcess(ResourceLocation processName, UUID processId)
    {
        if (!this.canExecuteProcess(processName))
        {
            OpenDevices.LOGGER.warn("Could not execute process with name '" + processName + "' for Laptop as there are more than the maximum processes running. Skipping!");
            return false;
        }

        DeviceProcessRegistryEntry entry = DeviceRegistries.PROCESSES.getValue(processName);

        if (entry == null)
        {
            OpenDevices.LOGGER.warn("Could not execute process with name '" + processName + "' for Laptop as it does not exist. Skipping!");
            return false;
        }

        DeviceProcess<Computer> process = entry.createProcess(Computer.class, this, processId);
        if (process != null)
        {
            this.startProcess(processId, process);
            return true;
        }
        return false;
    }

    public boolean syncTerminateProcess(UUID processId)
    {
        DeviceProcess<Computer> process = this.processes.get(processId);

        if (process == null)
        {
            OpenDevices.LOGGER.warn("Could not terminate process with id '" + processId + "' for Laptop as it does not exist. Skipping!");
            return false;
        }

        this.windowManager.closeProcessWindows(processId);
        this.processes.remove(processId);

        return true;
    }

    public boolean syncProcess(UUID processId, CompoundNBT data)
    {
        DeviceProcess<Computer> process = this.getProcess(processId);

        if (process == null)
        {
            OpenDevices.LOGGER.warn("Could not sync process with id '" + processId + "' for Laptop as it does not exist. Skipping!");
            return false;
        }

        process.readSyncNBT(data);
        return true;
    }

    @Override
    public boolean canExecuteProcess(ResourceLocation processId)
    {
        return this.processes.size() < DeviceConstants.MAX_COMPUTER_PROCESSES;
    }

    @Override
    public UUID executeProcess(ResourceLocation processName)
    {
        if (!this.canExecuteProcess(processName))
        {
            OpenDevices.LOGGER.warn("Could not execute process with name '" + processName + "' for Laptop as there are more than the maximum processes running. Skipping!");
            return null;
        }

        UUID processId = UUID.randomUUID();
        if (this.isClient())
        {
            TaskManager.sendToServer(new ExecuteProcessTask(this.getPos(), processName, processId), TaskManager.TaskReceiver.SENDER_AND_NEARBY);
        }
        else
        {
            if (this.syncExecuteProcess(processName, processId))
            {
                this.initProcess(processId);
                TaskManager.sendToTracking(new ExecuteProcessTask(this.getPos(), processName, processId), this.getWorld(), this.getPos());
            }
            else
            {
                return null;
            }
        }

        return processId;
    }

    // TODO has not been tested, needs validation!
    @Override
    public void terminateProcess(UUID processId)
    {
        if (this.isClient())
        {
            TaskManager.sendToServer(new TerminateProcessTask(this.getAddress(), processId), TaskManager.TaskReceiver.SENDER_AND_NEARBY);
        }
        else
        {
            if (this.syncTerminateProcess(processId))
            {
                TaskManager.sendToTracking(new TerminateProcessTask(this.getAddress(), processId), this.getWorld(), this.getPos());
            }
        }
    }

    @Override
    public void syncProcess(UUID processId)
    {
        DeviceProcess<Computer> process = this.getProcess(processId);

        if (process == null)
        {
            OpenDevices.LOGGER.warn("Could not sync process with id '" + processId + "' for Laptop as it does not exist. Skipping!");
            return;
        }

        if (this.isClient())
        {
            TaskManager.sendToServer(new SyncProcessTask(this.getAddress(), processId, process.writeSyncNBT()), TaskManager.TaskReceiver.NEARBY);
        }
        else
        {
            TaskManager.sendToTracking(new SyncProcessTask(this.getAddress(), processId, process.writeSyncNBT()), this.getWorld(), this.getPos());
        }
    }

    @Override
    public void execute(@Nonnull Runnable command)
    {
        this.executionQueue.add(command);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        super.write(nbt);
        nbt.putBoolean("open", this.open);
        return nbt;
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        this.open = nbt.getBoolean("open");
    }

    @Override
    public void save(CompoundNBT nbt)
    {
        if (this.address != null)
            nbt.putUniqueId("address", this.address);
        nbt.put("settings", this.settings);
        nbt.put("desktop", this.desktop.serializeNBT());
        nbt.put("windowManager", this.windowManager.serializeNBT());
        nbt.put("taskBar", this.taskBar.serializeNBT());

        ListNBT processesNbt = new ListNBT();
        this.processes.forEach((processId, process) ->
        {
            CompoundNBT processNbt = ProcessSerializer.write(process);
            if (processNbt != null)
            {
                processesNbt.add(processNbt);
            }
        });
        nbt.put("processes", processesNbt);
    }

    @Override
    public void load(CompoundNBT nbt)
    {
        this.address = nbt.hasUniqueId("address") ? nbt.getUniqueId("address") : null;
        this.settings = nbt.getCompound("settings");
        this.desktop.deserializeNBT(nbt.getCompound("desktop"));
        this.windowManager.deserializeNBT(nbt.getCompound("windowManager"));
        this.taskBar.deserializeNBT(nbt.getCompound("taskBar"));

        ListNBT processesNbt = nbt.getList("processes", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < Math.min(processesNbt.size(), DeviceConstants.MAX_COMPUTER_PROCESSES); i++)
        {
            DeviceProcess<Computer> process = ProcessSerializer.read(Computer.class, this, processesNbt.getCompound(i));
            if (process != null)
            {
                UUID processId = process.getProcessId();
                this.startProcess(processId, process);
                if (!this.isClient())
                {
                    this.initProcess(processId);
                }
            }
        }
    }

    public void syncSettings(CompoundNBT nbt)
    {
        this.settings.merge(nbt);
        if (!this.isClient())
        {
            this.markDirty();
        }
    }

    @Override
    public <T> T readSetting(LaptopSetting<T> setting)
    {
        if (!DeviceRegistries.SETTINGS.containsKey(setting.getRegistryName()))
            throw new RuntimeException("Setting " + setting.getRegistryName() + " is not registered! In order to read from a setting it needs to be registered!");
        return setting.contains(this.settings) ? setting.read(this.settings) : setting.getDefaultValue();
    }

    @Override
    public <T> void writeSetting(LaptopSetting<T> setting, T value)
    {
        if (!DeviceRegistries.SETTINGS.containsKey(setting.getRegistryName()))
        {
            OpenDevices.LOGGER.warn("Setting " + setting.getRegistryName() + " is not registered! In order to write to a setting it needs to be registered!");
            return;
        }

        if (this.world != null)
        {
            CompoundNBT nbt = new CompoundNBT();
            setting.write(value, nbt);

            if (this.world.isRemote())
            {
                TaskManager.sendToServer(new SyncSettingsTask(this.getAddress(), nbt), TaskManager.TaskReceiver.NEARBY);
                this.syncSettings(nbt);
            }
            else
            {
                TaskManager.sendToTracking(new SyncSettingsTask(this.getAddress(), nbt), this.world, this.getPos());
            }
        }
    }

    public void toggleOpen(PlayerEntity player)
    {
        if (this.inUse() || !this.canInteract(player))
            return;
        this.open = !this.open;
        this.notifyUpdate();
    }

    public boolean view(PlayerEntity player)
    {
        if (this.inUse() || !this.open)
            return false;
        if (this.canInteract(player))
        {
            this.user = player.getUniqueID();
            return true;
        }
        return false;
    }

    public void stopView(PlayerEntity player)
    {
        if (this.user == null)
            return;
        if (this.user.equals(player.getUniqueID()))
            this.user = null;
    }

    private boolean canInteract(@Nullable PlayerEntity player)
    {
        if (player == null)
            return false;
        return player.getDistanceSq(this.pos.getX(), this.pos.getY(), this.pos.getZ()) <= 64D;
    }

    private boolean inUse()
    {
        return this.canInteract(this.getUser());
    }

    public boolean isOpen()
    {
        return open;
    }

    @Nullable
    public PlayerEntity getUser()
    {
        if (this.user == null || this.world == null)
            return null;
        return this.world.getPlayerByUuid(this.user);
    }

    @Override
    public UUID getAddress()
    {
        return address;
    }

    @Nullable
    @Override
    public DeviceSerializer<? extends Device> getSerializer()
    {
        return DeviceSerializers.TILE_ENTITY_DEVICE_SERIALIZER;
    }

    @Override
    public Collection<UUID> getProcessIds()
    {
        return new HashSet<>(processes.keySet());
    }

    @Override
    public DeviceProcess<Computer> getProcess(UUID id)
    {
        return processes.get(id);
    }

    @Override
    public boolean supportsExecution()
    {
        return true;
    }

    @Override
    public boolean supportsProcesses()
    {
        return true;
    }

    @Override
    public LaptopDesktop getDesktop()
    {
        return desktop;
    }

    @Override
    public LaptopWindowManager getWindowManager()
    {
        return windowManager;
    }

    @Override
    public LaptopTaskBar getTaskBar()
    {
        return taskBar;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return RENDER_AABB.offset(this.pos);
    }

    @OnlyIn(Dist.CLIENT)
    public float getScreenAngle(float partialTicks)
    {
        return DeviceConstants.LAPTOP_OPENED_ANGLE * (this.lastRotation + (this.rotation - this.lastRotation) * partialTicks);
    }
}
