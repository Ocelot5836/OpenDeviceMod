package com.ocelot.opendevices.core;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.device.DeviceTileEntity;
import com.ocelot.opendevices.api.laptop.Laptop;
import com.ocelot.opendevices.api.laptop.settings.LaptopSetting;
import com.ocelot.opendevices.api.DeviceRegistries;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.task.SyncSettingsTask;
import com.ocelot.opendevices.init.DeviceBlocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LaptopTileEntity extends DeviceTileEntity implements Laptop, ITickableTileEntity
{
    private static final AxisAlignedBB RENDER_AABB = VoxelShapes.fullCube().getBoundingBox();

    private UUID user;
    private boolean open;
    private Queue<Runnable> executionQueue;

    private UUID address;
    private CompoundNBT settings;
    private LaptopDesktop desktop;
    private LaptopTaskBar taskBar;

    @OnlyIn(Dist.CLIENT)
    private float rotation;
    @OnlyIn(Dist.CLIENT)
    private float lastRotation;

    public LaptopTileEntity()
    {
        super(DeviceBlocks.TE_LAPTOP);
        this.user = null;
        this.open = false;
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
                OpenDevices.LOGGER.debug("Executing {} task" + (this.executionQueue.size() != 1 ? "s" : ""), this.executionQueue.size());
            }

            Runnable runnable;
            while ((runnable = this.executionQueue.poll()) != null)
            {
                runnable.run();
            }

            this.desktop.update();
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
        nbt.putUniqueId("address", this.address);
        nbt.put("settings", this.settings);
        nbt.put("desktop", this.desktop.serializeNBT());
        nbt.put("taskBar", this.taskBar.serializeNBT());
    }

    @Override
    public void load(CompoundNBT nbt)
    {
        this.address = nbt.getUniqueId("address");
        this.settings = nbt.getCompound("settings");
        this.desktop.deserializeNBT(nbt.getCompound("desktop"));
        this.taskBar.deserializeNBT(nbt.getCompound("taskBar"));
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
                TaskManager.sendTo(new SyncSettingsTask(this.pos, nbt), TaskManager.TaskReceiver.SENDER_AND_NEARBY, (ServerPlayerEntity) this.getUser());
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
