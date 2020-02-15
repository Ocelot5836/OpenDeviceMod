package com.ocelot.opendevices.core.laptop.window;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.laptop.window.Window;
import com.ocelot.opendevices.api.laptop.window.WindowContentType;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.LaptopTileEntity;
import com.ocelot.opendevices.core.task.MoveWindowTask;
import com.ocelot.opendevices.core.task.SetWindowPositionTask;
import com.ocelot.opendevices.core.task.SetWindowSizeTask;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

@Deprecated
public class LaptopWindowOld implements Window, INBTSerializable<CompoundNBT>
{
    private LaptopTileEntity laptop;
    private UUID id;
    private float x;
    private float y;
    private int width;
    private int height;
    private CompoundNBT initData;
    private WindowContentType contentType;
    private ResourceLocation contentId;
    private CompoundNBT stateData;

    public LaptopWindowOld(LaptopTileEntity laptop)
    {
        this.laptop = laptop;
    }

    public LaptopWindowOld(LaptopTileEntity laptop, @Nullable CompoundNBT initData, WindowContentType contentType, ResourceLocation contentId, int width, int height)
    {
        this(laptop, initData, contentType, contentId, (DeviceConstants.LAPTOP_SCREEN_WIDTH - width) / 2f, (DeviceConstants.LAPTOP_SCREEN_HEIGHT - laptop.getTaskBar().getHeight() - (height + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT + 2)) / 2f, width, height);
    }

    public LaptopWindowOld(LaptopTileEntity laptop, @Nullable CompoundNBT initData, WindowContentType contentType, ResourceLocation contentId, float x, float y, int width, int height)
    {
        this.laptop = laptop;
        this.initData = initData;
        this.contentType = contentType;
        this.contentId = contentId;
        this.stateData = null;
        this.id = UUID.randomUUID();
        this.setPosition(x, y);
        this.setSize(width, height);
    }

    private void checkPosition()
    {
        if (this.x < 0)
            this.x = 0;
        if (this.y < 0)
            this.y = 0;
        if (this.x >= DeviceConstants.LAPTOP_SCREEN_WIDTH - this.width)
            this.x = DeviceConstants.LAPTOP_SCREEN_WIDTH - this.width;
        if (this.y >= DeviceConstants.LAPTOP_SCREEN_HEIGHT - this.height)
            this.y = DeviceConstants.LAPTOP_SCREEN_HEIGHT - this.height;
    }

    public void syncMove(float xDirection, float yDirection)
    {
        this.x += xDirection;
        this.y += yDirection;
        this.checkPosition();
    }

    public void syncSetPosition(float x, float y)
    {
        this.x = x;
        this.y = y;
        this.checkPosition();
    }

    public void syncSetSize(int width, int height)
    {
        this.width = MathHelper.clamp(width + 2, DeviceConstants.LAPTOP_MIN_APPLICATION_WIDTH, DeviceConstants.LAPTOP_MAX_APPLICATION_WIDTH + 2);
        this.height = MathHelper.clamp(height + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT + 2, DeviceConstants.LAPTOP_MIN_APPLICATION_HEIGHT, DeviceConstants.LAPTOP_MAX_APPLICATION_HEIGHT + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT + 2);

        if (width < DeviceConstants.LAPTOP_MIN_APPLICATION_WIDTH || width > DeviceConstants.LAPTOP_MAX_APPLICATION_WIDTH || height > DeviceConstants.LAPTOP_MAX_APPLICATION_HEIGHT || height < DeviceConstants.LAPTOP_MIN_APPLICATION_HEIGHT)
        {
            OpenDevices.LOGGER.warn("Windows must be between " + DeviceConstants.LAPTOP_MIN_APPLICATION_WIDTH + "x" + DeviceConstants.LAPTOP_MIN_APPLICATION_HEIGHT + " and " + DeviceConstants.LAPTOP_MAX_APPLICATION_WIDTH + "x" + DeviceConstants.LAPTOP_MAX_APPLICATION_HEIGHT + ". Clamping size to screen.");
        }
    }

    @Override
    public void move(float xDirection, float yDirection)
    {
        if (xDirection == 0 && yDirection == 0)
            return;

        if (this.laptop.isClient())
        {
            this.syncMove(xDirection, yDirection);
            TaskManager.sendToServer(new MoveWindowTask(this.laptop.getPos(), this.getId(), xDirection, yDirection), TaskManager.TaskReceiver.NEARBY);
        }
        else
        {
            TaskManager.sendToTracking(new MoveWindowTask(this.laptop.getPos(), this.getId(), xDirection, yDirection), this.laptop.getWorld(), this.laptop.getPos());
        }
    }

    @Override
    public void setPosition(float x, float y)
    {
        if (this.x == x && this.y == y)
            return;

        if (this.laptop.isClient())
        {
            this.syncSetPosition(x, y);
            TaskManager.sendToServer(new SetWindowPositionTask(this.laptop.getPos(), this.getId(), x, y), TaskManager.TaskReceiver.NEARBY);
        }
        else
        {
            TaskManager.sendToTracking(new SetWindowPositionTask(this.laptop.getPos(), this.getId(), x, y), this.laptop.getWorld(), this.laptop.getPos());
        }
    }

    @Override
    public void setSize(int width, int height)
    {
        if (this.width == width && this.height == height)
            return;

        if (this.laptop.isClient())
        {
            this.syncSetSize(width, height);
            TaskManager.sendToServer(new SetWindowSizeTask(this.laptop.getPos(), this.getId(), width, height), TaskManager.TaskReceiver.NEARBY);
        }
        else
        {
            TaskManager.sendToTracking(new SetWindowSizeTask(this.laptop.getPos(), this.getId(), width, height), this.laptop.getWorld(), this.laptop.getPos());
        }
    }

    public void create()
    {
    }

    public void init()
    {
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

    public boolean onMouseScrolled(double mouseX, double mouseY, double amount)
    {
        return false;
    }

    public void onMouseMoved(double mouseX, double mouseY)
    {
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

    public void onClose()
    {
    }

    @Override
    public LaptopTileEntity getLaptop()
    {
        return laptop;
    }

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

    public CompoundNBT getInitData()
    {
        return initData;
    }

    public CompoundNBT getStateData()
    {
        return stateData;
    }

    public void setStateData(CompoundNBT stateData)
    {
        this.stateData = stateData;
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
        if (this.initData != null)
            nbt.put("initData", this.initData);
        nbt.putByte("contentType", (byte) this.contentType.ordinal());
        nbt.putString("contentId", this.contentId.toString());
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
        if (nbt.contains("initData", Constants.NBT.TAG_COMPOUND))
            this.initData = nbt.getCompound("initData");
        this.contentType = WindowContentType.values()[nbt.getByte("contentType") % WindowContentType.values().length];
        this.contentId = new ResourceLocation(nbt.getString("contentId"));
        this.checkPosition();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof LaptopWindowOld)) return false;
        LaptopWindowOld window = (LaptopWindowOld) o;
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
