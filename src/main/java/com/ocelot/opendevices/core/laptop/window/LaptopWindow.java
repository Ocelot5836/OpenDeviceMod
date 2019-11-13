package com.ocelot.opendevices.core.laptop.window;

import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.laptop.Laptop;
import com.ocelot.opendevices.api.laptop.window.Window;
import com.ocelot.opendevices.api.laptop.window.WindowContentType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Objects;
import java.util.UUID;

public class LaptopWindow implements Window, INBTSerializable<CompoundNBT>
{
    private Laptop laptop;
    private UUID id;
    private int x;
    private int y;
    private int width;
    private int height;
    private WindowContentType contentType;
    private ResourceLocation contentId;

    public LaptopWindow(Laptop laptop)
    {
        this.laptop = laptop;
    }

    public LaptopWindow(Laptop laptop, WindowContentType contentType, ResourceLocation contentId, int width, int height)
    {
        this(laptop, contentType, contentId, (DeviceConstants.LAPTOP_SCREEN_WIDTH - width) / 2, (DeviceConstants.LAPTOP_SCREEN_HEIGHT - DeviceConstants.LAPTOP_TASK_BAR_HEIGHT - height) / 2, width, height);
    }

    public LaptopWindow(Laptop laptop, WindowContentType contentType, ResourceLocation contentId, int x, int y, int width, int height)
    {
        this.laptop = laptop;
        this.contentType = contentType;
        this.contentId = contentId;
        this.id = UUID.randomUUID();
        this.x = x;
        this.y = y;
        this.width = width + 2;
        this.height = height + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT + 2;
    }

    private void checkPosition()
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

    @Override
    public void focus()
    {
        if (!this.id.equals(this.laptop.getDesktop().getFocusedWindowId()))
        {
            this.laptop.getDesktop().focusWindow(this.id);
        }
    }

    @Override
    public void close()
    {
        this.laptop.getDesktop().closeWindow(this.id);
    }

    @Override
    public void move(float xDirection, float yDirection)
    {
        this.x += xDirection;
        this.y += yDirection;
        this.checkPosition();
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

    public boolean onMousePressed(double mouseX, double mouseY, int mouseButton)
    {
        return false;
    }

    public boolean onMouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        return false;
    }

    public boolean onMouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double detaY)
    {
        return false;
    }

    public boolean onKeyPressed(int keyCode)
    {
        return false;
    }

    public boolean onKeyReleased(int keyCode)
    {
        return false;
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

    public Laptop getLaptop()
    {
        return laptop;
    }

    @Override
    public UUID getId()
    {
        return id;
    }

    @Override
    public int getX()
    {
        return x;
    }

    @Override
    public int getY()
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
    public WindowContentType getContentType()
    {
        return contentType;
    }

    @Override
    public ResourceLocation getContentId()
    {
        return contentId;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putUniqueId("id", this.id);
        nbt.putInt("x", this.x);
        nbt.putInt("y", this.y);
        nbt.putInt("width", this.width);
        nbt.putInt("height", this.height);
        nbt.putByte("contentType", (byte) this.contentType.ordinal());
        nbt.putString("contentId", this.contentId.toString());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.id = nbt.getUniqueId("id");
        this.x = nbt.getInt("x");
        this.y = nbt.getInt("y");
        this.width = nbt.getInt("width");
        this.height = nbt.getInt("height");
        this.contentType = WindowContentType.values()[nbt.getByte("contentType") % WindowContentType.values().length];
        this.contentId = new ResourceLocation(nbt.getString("contentId"));
        this.checkPosition();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof LaptopWindow)) return false;
        LaptopWindow window = (LaptopWindow) o;
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
