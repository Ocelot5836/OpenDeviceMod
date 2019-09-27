package com.ocelot.opendevices.core.window;

import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.laptop.Laptop;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Objects;
import java.util.UUID;

public class Window implements INBTSerializable<CompoundNBT>
{
    private Laptop laptop;
    private UUID id;
    private float x;
    private float y;
    private int width;
    private int height;

    public Window(Laptop laptop)
    {
        this.laptop = laptop;
    }

    public Window(Laptop laptop, float x, float y, int width, int height)
    {
        this.laptop = laptop;
        this.id = UUID.randomUUID();
        this.x = x;
        this.y = y;
        this.width = width + 2;
        this.height = height + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT + 2;
    }

    public Window(Laptop laptop, int width, int height)
    {
        this(laptop, (DeviceConstants.LAPTOP_SCREEN_WIDTH - width) / 2f, (DeviceConstants.LAPTOP_SCREEN_HEIGHT - DeviceConstants.LAPTOP_TASK_BAR_HEIGHT - height) / 2f, width, height);
    }

    void checkPosition()
    {
        if (this.x < 0)
            this.x = 0;
        if (this.y < 0)
            this.y = 0;
        if (this.x >= DeviceConstants.LAPTOP_SCREEN_WIDTH - this.width)
            this.x = DeviceConstants.LAPTOP_SCREEN_WIDTH - this.width;
        if (this.y >= DeviceConstants.LAPTOP_SCREEN_HEIGHT - DeviceConstants.LAPTOP_TASK_BAR_HEIGHT - this.height)
            this.y = DeviceConstants.LAPTOP_SCREEN_HEIGHT - DeviceConstants.LAPTOP_TASK_BAR_HEIGHT - this.height;
    }

    public void update()
    {
    }

    public void onGainFocus()
    {
    }

    public void onLostFocus()
    {
    }

    public void onMousePressed(double mouseX, double mouseY, int mouseButton)
    {
    }

    public void onMouseReleased(double mouseX, double mouseY, int mouseButton)
    {
    }

    public void onMouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double detaY)
    {
        System.out.println("Mouse Dragged: " + (int) mouseX + ", " + (int) mouseY);
    }

    public void onKeyPressed(int keyCode)
    {
    }

    public void onKeyReleased(int keyCode)
    {
    }

    public void saveState(CompoundNBT nbt)
    {
    }

    public void loadState(CompoundNBT nbt)
    {
    }

    public void onClose()
    {
    }

    public void move(float xDirection, float yDirection)
    {
        this.x += xDirection;
        this.y += yDirection;
        this.checkPosition();
        // TODO send to server and back to clients
    }

    public Laptop getLaptop()
    {
        return laptop;
    }

    public UUID getId()
    {
        return id;
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
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
        this.id = nbt.getUniqueId("id");
        this.x = nbt.getFloat("x");
        this.y = nbt.getFloat("y");
        this.width = nbt.getInt("width");
        this.height = nbt.getInt("height");
        this.checkPosition();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Window)) return false;
        Window window = (Window) o;
        return this.id.equals(window.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.id);
    }

    @Override
    public String toString()
    {
        return getClass().getName() + "@" + this.id;
    }
}