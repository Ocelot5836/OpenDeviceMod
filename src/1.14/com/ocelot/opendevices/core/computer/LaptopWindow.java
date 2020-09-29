package com.ocelot.opendevices.core.computer;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.computer.Computer;
import com.ocelot.opendevices.api.computer.window.Window;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;

public class LaptopWindow implements Window, INBTSerializable<CompoundNBT>
{
    private Computer computer;
    private UUID processId;
    private UUID id;

    private String title;
    private float x;
    private float y;
    private float lastX;
    private float lastY;
    private int width;
    private int height;
    private ResourceLocation icon;

    public LaptopWindow(Computer computer, UUID processId)
    {
        this.computer = computer;
        this.processId = processId;
        this.id = UUID.randomUUID();
        this.title = String.valueOf(this.id);
        this.width = DeviceConstants.LAPTOP_DEFAULT_APPLICATION_WIDTH + 2;
        this.height = DeviceConstants.LAPTOP_DEFAULT_APPLICATION_HEIGHT + 2 + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT;
        this.icon = null;
    }

    public LaptopWindow(Computer computer, CompoundNBT nbt)
    {
        this.computer = computer;
        this.deserializeNBT(nbt);
    }

    public void update()
    {
        this.lastX = this.x;
        this.lastY = this.y;
    }

    @Override
    public Computer getComputer()
    {
        return computer;
    }

    @Override
    public UUID getProcessId()
    {
        return processId;
    } // All windows will be created from a process

    @Override
    public UUID getId()
    {
        return id;
    }

    @Override
    public String getTitle()
    {
        return title;
    }

    @Override
    public float getX()
    {
        return x;
    }

    @Override
    public float getY()
    {
        return y;
    }

    @Override
    public float getLastX()
    {
        return lastX;
    }

    @Override
    public float getLastY()
    {
        return lastY;
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Nullable
    @Override
    public ResourceLocation getIconSprite()
    {
        return icon;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setX(float x)
    {
        this.x = x;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    public void setLastX(float lastX)
    {
        this.lastX = lastX;
    }

    public void setLastY(float lastY)
    {
        this.lastY = lastY;
    }

    public void setWidth(int width)
    {
        this.width = MathHelper.clamp(width + 2, DeviceConstants.LAPTOP_MIN_APPLICATION_WIDTH, DeviceConstants.LAPTOP_MAX_APPLICATION_WIDTH + 2);

        if (width < DeviceConstants.LAPTOP_MIN_APPLICATION_WIDTH || width > DeviceConstants.LAPTOP_MAX_APPLICATION_WIDTH)
        {
            OpenDevices.LOGGER.warn("Windows must be between " + DeviceConstants.LAPTOP_MIN_APPLICATION_WIDTH + "x" + DeviceConstants.LAPTOP_MIN_APPLICATION_HEIGHT + " and " + DeviceConstants.LAPTOP_MAX_APPLICATION_WIDTH + "x" + DeviceConstants.LAPTOP_MAX_APPLICATION_HEIGHT + ". Clamping size to screen.");
        }
    }

    public void setHeight(int height)
    {
        this.height = MathHelper.clamp(height + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT + 2, DeviceConstants.LAPTOP_MIN_APPLICATION_HEIGHT, DeviceConstants.LAPTOP_MAX_APPLICATION_HEIGHT + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT + 2);

        if (height > DeviceConstants.LAPTOP_MAX_APPLICATION_HEIGHT || height < DeviceConstants.LAPTOP_MIN_APPLICATION_HEIGHT)
        {
            OpenDevices.LOGGER.warn("Windows must be between " + DeviceConstants.LAPTOP_MIN_APPLICATION_WIDTH + "x" + DeviceConstants.LAPTOP_MIN_APPLICATION_HEIGHT + " and " + DeviceConstants.LAPTOP_MAX_APPLICATION_WIDTH + "x" + DeviceConstants.LAPTOP_MAX_APPLICATION_HEIGHT + ". Clamping size to screen.");
        }
    }

    public void setIcon(ResourceLocation icon)
    {
        this.icon = icon;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putUniqueId("processId", this.processId);
        nbt.putUniqueId("id", this.id);
        nbt.putString("title", this.title);
        nbt.putFloat("x", this.x);
        nbt.putFloat("y", this.y);
        nbt.putInt("width", this.width);
        nbt.putInt("height", this.height);
        if (this.icon != null)
            nbt.putString("icon", this.icon.toString());

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.processId = nbt.getUniqueId("processId");
        this.id = nbt.getUniqueId("id");
        this.title = nbt.getString("title");
        this.x = nbt.getFloat("x");
        this.y = nbt.getFloat("y");
        this.lastX = this.x;
        this.lastY = this.y;
        this.width = nbt.getInt("width");
        this.height = nbt.getInt("height");
        this.icon = nbt.contains("icon", Constants.NBT.TAG_STRING) ? new ResourceLocation(nbt.getString("icon")) : null;
    }

    @Override
    public String toString()
    {
        return "LaptopWindow{processId=" + this.processId + ",id=" + this.id + ",title=" + this.title + "}";
    }
}
