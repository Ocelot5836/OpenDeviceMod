package com.ocelot.opendevices.core;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.api.device.DeviceProcess;
import com.ocelot.opendevices.api.device.DeviceTileEntity;
import com.ocelot.opendevices.api.device.ProcessSerializer;
import com.ocelot.opendevices.api.laptop.Laptop;
import com.ocelot.opendevices.api.laptop.settings.LaptopSetting;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.registry.DeviceProcessRegistryEntry;
import com.ocelot.opendevices.core.task.ExecuteProcessTask;
import com.ocelot.opendevices.core.task.SyncProcessTask;
import com.ocelot.opendevices.core.task.SyncSettingsTask;
import com.ocelot.opendevices.init.DeviceBlocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LaptopTileEntity extends DeviceTileEntity implements Laptop, ITickableTileEntity
{
    private static final AxisAlignedBB RENDER_AABB = VoxelShapes.fullCube().getBoundingBox();

    private UUID user;
    private boolean open;
    private HashMap<UUID, DeviceProcess<Laptop>> processes;
    private Queue<Runnable> executionQueue;

    private UUID address;
    private CompoundNBT settings;
    private LaptopDesktop desktop;
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
        this.executionQueue = new ConcurrentLinkedQueue<>();

        this.address = UUID.randomUUID();
        this.settings = new CompoundNBT();
        this.desktop = new LaptopDesktop(this);
        this.taskBar = new LaptopTaskBar(this);
    }

    @Override
    public void tick()
    {
        if (this.hasWorld())
        {
            if (this.isClient())
            {
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

            this.processes.values().forEach(process -> process.update(this));

            this.desktop.update();
        }
    }

    public boolean syncProcess(UUID processId, CompoundNBT data)
    {
        DeviceProcess<Laptop> process = this.getProcess(processId);

        if (process == null)
        {
            OpenDevices.LOGGER.warn("Could not sync process with id '" + processId + "' for Laptop as it does not exist. Skipping!");
            return false;
        }

        process.readSyncNBT(data);
        return true;
    }

    public boolean syncExecuteProcess(ResourceLocation processName, UUID processId)
    {
        DeviceProcessRegistryEntry entry = DeviceRegistries.PROCESSES.getValue(processName);

        if (entry == null)
        {
            OpenDevices.LOGGER.warn("Could not execute process with name '" + processName + "' for Laptop as it does not exist. Skipping!");
            return false;
        }

        this.processes.put(processId, entry.createProcess(processId));
        return true;
    }

    @Override
    public void syncProcess(UUID processId)
    {
        DeviceProcess<Laptop> process = this.getProcess(processId);

        if (process == null)
        {
            OpenDevices.LOGGER.warn("Could not sync process with id '" + processId + "' for Laptop as it does not exist. Skipping!");
            return;
        }

        CompoundNBT data = process.writeSyncNBT();

        if (this.isClient())
        {
            if (this.syncProcess(processId, data))
            {
                TaskManager.sendToServer(new SyncProcessTask(this.getPos(), processId, data), TaskManager.TaskReceiver.NEARBY);
            }
        }
        else
        {
            TaskManager.sendToTracking(new SyncProcessTask(this.getPos(), processId, data), this.getWorld(), this.getPos());
        }
    }

    @Override
    public UUID executeProcess(ResourceLocation processName)
    {
        UUID processId = UUID.randomUUID();

        if (this.isClient())
        {
            TaskManager.sendToServer(new ExecuteProcessTask(this.getPos(), processName, processId), TaskManager.TaskReceiver.SENDER_AND_NEARBY);
        }
        else
        {
            TaskManager.sendToTracking(new ExecuteProcessTask(this.getPos(), processName, processId), this.getWorld(), this.getPos());
        }

        return processId;
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
        //TODO add process saving/loading
        nbt.putUniqueId("address", this.address);
        nbt.put("settings", this.settings);
        nbt.put("desktop", this.desktop.serializeNBT());
        nbt.put("taskBar", this.taskBar.serializeNBT());

        ListNBT processesNbt = new ListNBT();
        this.processes.forEach((processId, process) -> processesNbt.add(ProcessSerializer.write(process)));
        nbt.put("processes", processesNbt);
    }

    @Override
    public void load(CompoundNBT nbt)
    {
        this.address = nbt.getUniqueId("address");
        this.settings = nbt.getCompound("settings");
        this.desktop.deserializeNBT(nbt.getCompound("desktop"));
        this.taskBar.deserializeNBT(nbt.getCompound("taskBar"));

        ListNBT processesNbt = nbt.getList("processes", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < processesNbt.size(); i++)
        {
            DeviceProcess<Laptop> process = ProcessSerializer.read(processesNbt.getCompound(i));
            if (process != null)
            {
                this.processes.put(process.getProcessId(), process);
            }
        }
    }

    public void syncSettings(CompoundNBT nbt)
    {
        this.settings.merge(nbt);
    }

    @Override
    public <T> T readSetting(LaptopSetting<T> setting)
    {
        if (!DeviceRegistries.SETTINGS.containsKey(setting.getRegistryName()))
            throw new RuntimeException("Setting " + setting.getRegistryName() + " is not registered! In order to write to a setting it needs to be registered!");
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
                TaskManager.sendToServer(new SyncSettingsTask(this.pos, nbt), TaskManager.TaskReceiver.NEARBY);
                this.syncSettings(nbt);
            }
            else
            {
                TaskManager.sendToTracking(new SyncSettingsTask(this.pos, nbt), this.world, this.getPos());
                this.markDirty();
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

    @Override
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

    @Override
    public Collection<UUID> getProcessIds()
    {
        return new HashSet<>(processes.keySet());
    }

    @Override
    public DeviceProcess<Laptop> getProcess(UUID id)
    {
        return processes.get(id);
    }

    @Override
    public LaptopDesktop getDesktop()
    {
        return desktop;
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
