package com.ocelot.opendevices.core.laptop.window;

import com.ocelot.opendevices.api.laptop.Laptop;
import com.ocelot.opendevices.api.laptop.window.Window;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public class LaptopWindow implements Window, INBTSerializable<CompoundNBT>
{
    private Laptop laptop;
    private UUID processId;
    private UUID id;

    private float x;
    private float y;
    private float lastX;
    private float lastY;
    private int width;
    private int height;

    public LaptopWindow(Laptop laptop, UUID processId)
    {
        this.laptop = laptop;
        this.processId = processId;
        this.id = UUID.randomUUID();
        this.width = 200;
        this.height = 100;
    }

    public LaptopWindow(Laptop laptop, CompoundNBT nbt)
    {
        this.laptop = laptop;
        this.deserializeNBT(nbt);
    }

    public void update()
    {
        this.lastX = this.x;
        this.lastY = this.y;
    }

    @Override
    public Laptop getLaptop()
    {
        return laptop;
    }

    @Override
    public UUID getProcessId()
    {
        return processId;
    }//All windows will be created from a process

    @Override
    public UUID getId()
    {
        return id;
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
        this.width = width;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putUniqueId("processId", this.processId);
        nbt.putUniqueId("id", this.id);
        nbt.putFloat("x", this.x);
        nbt.putFloat("y", this.y);
        nbt.putInt("width", this.width);
        nbt.putInt("height", this.height);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.processId = nbt.getUniqueId("processId");
        this.id = nbt.getUniqueId("id");
        this.x = nbt.getFloat("x");
        this.y = nbt.getFloat("y");
        this.width = nbt.getInt("width");
        this.height = nbt.getInt("height");
    }
}
