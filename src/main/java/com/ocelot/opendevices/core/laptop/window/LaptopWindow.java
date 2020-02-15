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
    private int width;
    private int height;

    public LaptopWindow(Laptop laptop, UUID processId)
    {
        this.laptop = laptop;
        this.processId = processId;
        this.id = UUID.randomUUID();
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
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        return new CompoundNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {

    }
}
