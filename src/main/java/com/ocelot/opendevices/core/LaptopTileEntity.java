package com.ocelot.opendevices.core;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.device.DeviceTileEntity;
import com.ocelot.opendevices.api.laptop.Laptop;
import com.ocelot.opendevices.api.laptop.settings.LaptopSetting;
import com.ocelot.opendevices.api.laptop.settings.SettingsManager;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.task.SyncSettingsTask;
import com.ocelot.opendevices.init.DeviceBlocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LaptopTileEntity extends DeviceTileEntity implements Laptop, ITickableTileEntity
{
    public static final int OPENED_ANGLE = 102;

    private UUID user;
    private boolean open;
    private Queue<Runnable> executionQueue;

    private CompoundNBT settings;
    private LaptopDesktop desktop;

    //    private int rotation;
    //    private int prevRotation;

    public LaptopTileEntity()
    {
        super(DeviceBlocks.TE_LAPTOP);
        this.user = null;
        this.open = false;
        this.executionQueue = new ConcurrentLinkedQueue<>();

        this.settings = new CompoundNBT();
        this.desktop = new LaptopDesktop(this);
    }

    @Override
    public void tick()
    {
        if (!this.executionQueue.isEmpty())
        {
            OpenDevices.LOGGER.debug("Executing {} tasks", this.executionQueue.size());
        }

        Runnable runnable;
        while ((runnable = this.executionQueue.poll()) != null)
        {
            runnable.run();
        }

        this.desktop.update();
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
        nbt.put("settings", this.settings);
        nbt.put("desktop", this.desktop.serializeNBT());
    }

    @Override
    public void load(CompoundNBT nbt)
    {
        this.settings = nbt.getCompound("settings");
        this.desktop.deserializeNBT(nbt.getCompound("desktop"));
    }

    public void syncSettings(CompoundNBT nbt)
    {
        if (this.world != null)
        {
            this.settings.merge(nbt);
            if (!this.world.isRemote())
            {
                TaskManager.sendTask(new SyncSettingsTask(this.pos, nbt));
            }
        }
    }

    @Override
    public <T> T readSetting(LaptopSetting<T> setting)
    {
        return setting.contains(this.settings) ? setting.read(this.settings) : setting.getDefaultValue();
    }

    @Override
    public <T> void writeSetting(LaptopSetting<T> setting, T value)
    {
        if (!SettingsManager.isRegistered(setting))
        {
            OpenDevices.LOGGER.warn("Setting " + setting.getRegistryName() + " is not registered! In order to write to a setting it needs to be registered!");
            return;
        }

        if (this.world != null)
        {
            CompoundNBT nbt = new CompoundNBT();
            setting.write(value, nbt);
            this.syncSettings(nbt);
            if (this.world.isRemote())
            {
                TaskManager.sendTask(new SyncSettingsTask(this.pos, nbt));
            }
        }
    }

    public void toggleOpen(PlayerEntity player)
    {
        if (this.hasUser() || !this.canInteract(player))
            return;
        this.open = !this.open;
        this.notifyUpdate();
    }

    public boolean view(PlayerEntity player)
    {
        if (this.hasUser() || !this.open)
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

    public boolean canInteract(@Nullable PlayerEntity player)
    {
        if (player == null)
            return false;
        return player.getDistanceSq(this.pos.getX(), this.pos.getY(), this.pos.getZ()) <= 64D;
    }

    @Nullable
    public PlayerEntity getUserPlayer()
    {
        if (this.user == null || this.world == null)
            return null;
        return this.world.getPlayerByUuid(this.user);
    }

    public boolean hasUser()
    {
        return this.canInteract(this.getUserPlayer());
    }

    public boolean isOpen()
    {
        return open;
    }

    @Nullable
    public UUID getUser()
    {
        return user;
    }

    @Override
    public LaptopDesktop getDesktop()
    {
        return desktop;
    }

    //    @OnlyIn(Dist.CLIENT)
    //    public float getScreenAngle(float partialTicks)
    //    {
    //        return -OPENED_ANGLE * ((this.prevRotation + (this.rotation - this.prevRotation) * partialTicks) / OPENED_ANGLE); //TODO optimize
    //    }
}