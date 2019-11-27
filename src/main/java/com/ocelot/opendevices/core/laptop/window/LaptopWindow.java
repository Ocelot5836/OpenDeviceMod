package com.ocelot.opendevices.core.laptop.window;

import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.laptop.Laptop;
import com.ocelot.opendevices.api.laptop.window.Window;
import com.ocelot.opendevices.api.laptop.window.WindowContentType;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.task.MoveWindowTask;
import com.ocelot.opendevices.core.task.SetWindowPositionTask;
import com.ocelot.opendevices.core.task.SetWindowSizeTask;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class LaptopWindow implements Window, INBTSerializable<CompoundNBT>
{
    private Laptop laptop;
    private UUID id;
    private float x;
    private float y;
    private int width;
    private int height;
    private CompoundNBT initData;
    private WindowContentType contentType;
    private ResourceLocation contentId;

    public LaptopWindow(Laptop laptop)
    {
        this.laptop = laptop;
    }

    public LaptopWindow(Laptop laptop, @Nullable CompoundNBT initData, WindowContentType contentType, ResourceLocation contentId, int width, int height)
    {
        this(laptop, initData, contentType, contentId, (DeviceConstants.LAPTOP_SCREEN_WIDTH - width) / 2f, (DeviceConstants.LAPTOP_SCREEN_HEIGHT - laptop.getTaskBar().getHeight() - (height + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT + 2)) / 2f, width, height);
    }

    public LaptopWindow(Laptop laptop, @Nullable CompoundNBT initData, WindowContentType contentType, ResourceLocation contentId, float x, float y, int width, int height)
    {
        this.laptop = laptop;
        this.initData = initData;
        this.contentType = contentType;
        this.contentId = contentId;
        this.id = UUID.randomUUID();
        this.setPosition(x, y);
        this.setSize(width + 2, height + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT + 2);
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

    private void checkSize()
    {
        if (this.width < DeviceConstants.LAPTOP_MIN_APPLICATION_WIDTH || this.width > DeviceConstants.LAPTOP_MAX_APPLICATION_WIDTH || this.height > DeviceConstants.LAPTOP_MAX_APPLICATION_HEIGHT || this.height < DeviceConstants.LAPTOP_MIN_APPLICATION_HEIGHT)
        {
            throw new RuntimeException("Windows must be between " + DeviceConstants.LAPTOP_MIN_APPLICATION_WIDTH + "x" + DeviceConstants.LAPTOP_MIN_APPLICATION_HEIGHT + " and " + DeviceConstants.LAPTOP_MAX_APPLICATION_WIDTH + "x" + DeviceConstants.LAPTOP_MAX_APPLICATION_HEIGHT);
        }
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
        this.width = width;
        this.height = height;
        this.checkSize();
    }

    @Override
    public void focus()
    {
        this.laptop.getDesktop().focusWindow(this.id);
    }

    @Override
    public void close()
    {
        this.laptop.getDesktop().closeWindow(this.id);
    }

    @Override
    public void move(float xDirection, float yDirection)
    {
        if (this.laptop.getWorld().isRemote())
        {
            TaskManager.sendTaskToServer(new MoveWindowTask(this.laptop.getPos(), this.getId(), xDirection, yDirection), TaskManager.TaskReceiver.NEARBY);
            this.syncMove(xDirection, yDirection);
        }
        else
        {
            TaskManager.sendTaskTo(new MoveWindowTask(this.laptop.getPos(), this.getId(), xDirection, yDirection), TaskManager.TaskReceiver.SENDER_AND_NEARBY, (ServerPlayerEntity) this.laptop.getUser());
        }
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

    @Override
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
    public void center()
    {
        this.setPosition(DeviceConstants.LAPTOP_SCREEN_WIDTH - this.width / 2f, (DeviceConstants.LAPTOP_SCREEN_HEIGHT - this.laptop.getTaskBar().getHeight() - (this.height + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT + 2)) / 2f);
    }

    @Override
    public void setPosition(float x, float y)
    {
        if (this.laptop.getWorld().isRemote())
        {
            TaskManager.sendTaskToServer(new SetWindowPositionTask(this.laptop.getPos(), this.getId(), x, y), TaskManager.TaskReceiver.NEARBY);
            this.syncSetPosition(x, y);
        }
        else
        {
            TaskManager.sendTaskTo(new SetWindowPositionTask(this.laptop.getPos(), this.getId(), x, y), TaskManager.TaskReceiver.SENDER_AND_NEARBY, (ServerPlayerEntity) this.laptop.getUser());
        }
    }

    @Override
    public void setSize(int width, int height)
    {
        if (this.laptop.getWorld().isRemote())
        {
            TaskManager.sendTaskToServer(new SetWindowSizeTask(this.laptop.getPos(), this.getId(), width, height), TaskManager.TaskReceiver.NEARBY);
            this.syncSetSize(width, height);
        }
        else
        {
            TaskManager.sendTaskTo(new SetWindowSizeTask(this.laptop.getPos(), this.getId(), width, height), TaskManager.TaskReceiver.SENDER_AND_NEARBY, (ServerPlayerEntity) this.laptop.getUser());
        }
    }

    public CompoundNBT getInitData()
    {
        return initData;
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
