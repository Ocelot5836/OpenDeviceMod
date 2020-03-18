package com.ocelot.opendevices.api.component;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.api.util.SyncHelper;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * <p>Allows the ability to collect {@link Component} together and swap easily between different layouts.</p>
 * <p>Components can be added by calling {@link #addComponent(Component)}, {@link #addComponents(Component...)}, or {@link #addComponents(Collection)}.</p>
 *
 * @author Ocelot
 * @see Component
 */
public class Layout extends StandardComponent
{
    private float x;
    private float y;
    private int width;
    private int height;

    private List<Component> components;

    public Layout()
    {
        this(0, 0, DeviceConstants.LAPTOP_DEFAULT_APPLICATION_WIDTH, DeviceConstants.LAPTOP_DEFAULT_APPLICATION_HEIGHT);
    }

    public Layout(int width, int height)
    {
        this(0, 0, width, height);
    }

    public Layout(float x, float y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = MathHelper.clamp(width, DeviceConstants.LAPTOP_MIN_APPLICATION_WIDTH, DeviceConstants.LAPTOP_MAX_APPLICATION_WIDTH + 2);
        this.height = MathHelper.clamp(height, DeviceConstants.LAPTOP_MIN_APPLICATION_HEIGHT, DeviceConstants.LAPTOP_MAX_APPLICATION_HEIGHT + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT + 2);
        this.components = new ArrayList<>();

        SyncHelper syncHelper = new SyncHelper(this::markDirty);
        {
            syncHelper.addSerializer("components", nbt ->
            {
                ListNBT componentsNbt = new ListNBT();
                this.components.forEach(component -> componentsNbt.add(component.serializeNBT()));
                nbt.put("components", componentsNbt);
            }, nbt ->
            {
                ListNBT componentsNbt = nbt.getList("components", Constants.NBT.TAG_COMPOUND);
                if (this.components.size() != componentsNbt.size())
                {
                    OpenDevices.LOGGER.warn("Components sync tag size did not equal existing components size!");
                    return;
                }
                for (int i = 0; i < componentsNbt.size(); i++)
                {
                    this.components.get(i).deserializeNBT(componentsNbt.getCompound(i));
                }
            });
        }
        this.setClientSerializer(syncHelper);

        if (width < DeviceConstants.LAPTOP_MIN_APPLICATION_WIDTH || width > DeviceConstants.LAPTOP_MAX_APPLICATION_WIDTH || height > DeviceConstants.LAPTOP_MAX_APPLICATION_HEIGHT || height < DeviceConstants.LAPTOP_MIN_APPLICATION_HEIGHT)
        {
            OpenDevices.LOGGER.warn("Layouts must be between " + DeviceConstants.LAPTOP_MIN_APPLICATION_WIDTH + "x" + DeviceConstants.LAPTOP_MIN_APPLICATION_HEIGHT + " and " + DeviceConstants.LAPTOP_MAX_APPLICATION_WIDTH + "x" + DeviceConstants.LAPTOP_MAX_APPLICATION_HEIGHT + ". Clamping size to screen.");
        }
    }

    /**
     * Adds the specified components to this layout.
     *
     * @param components The components to add
     */
    public void addComponents(Component... components)
    {
        this.components.addAll(Arrays.asList(components));
    }

    /**
     * Adds the specified components to this layout.
     *
     * @param components The components to add
     */
    public void addComponents(Collection<Component> components)
    {
        this.components.addAll(components);
    }

    /**
     * Adds the specified component to this layout.
     *
     * @param component The component to add
     */
    public void addComponent(Component component)
    {
        this.components.add(component);
    }

    @Override
    public void update()
    {
        this.components.forEach(Component::update);
    }

    @Override
    public void render(float posX, float posY, int mouseX, int mouseY, float partialTicks)
    {
        RenderUtil.pushScissor(posX + this.getX(), posY + this.getY(), this.getWidth(), this.getHeight());
        this.components.forEach(component ->
        {
            if (component.getX() + component.getWidth() >= this.x && component.getX() < this.x + this.width && component.getY() + component.getHeight() >= this.y && component.getY() < this.y + this.height)
            {
                component.render(this.getX() + posX, this.getY() + posY, mouseX, mouseY, partialTicks);
            }
        });
        RenderUtil.popScissor();
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, float posX, float posY, int mouseX, int mouseY, float partialTicks)
    {
        if (this.isHovered(mouseX - (int) posX, mouseY - (int) posY))
        {
            this.components.forEach(component ->
            {
                if (component.getX() + component.getWidth() >= this.x && component.getX() < this.x + this.width && component.getY() + component.getHeight() >= this.y && component.getY() < this.y + this.height)
                {
                    component.renderOverlay(renderer, this.getX() + posX, this.getY() + posY, mouseX, mouseY, partialTicks);
                }
            });
        }
    }

    @Override
    public boolean onMousePressed(double mouseX, double mouseY, int mouseButton)
    {
        if (this.isHovered(mouseX, mouseY))
        {
            for (Component component : this.components)
            {
                if (component.onMousePressed(mouseX - this.x, mouseY - this.y, mouseButton))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        for (Component component : this.components)
        {
            if (component.onMouseReleased(mouseX - this.x, mouseY - this.y, mouseButton))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double amount)
    {
        for (Component component : this.components)
        {
            if (component.onMouseScrolled(mouseX - this.x, mouseY - this.y, amount))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMouseMoved(double mouseX, double mouseY)
    {
        this.components.forEach(component -> component.onMouseMoved(mouseX - this.x, mouseY - this.y));
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
    {
        for (Component component : this.components)
        {
            if (component.onMouseDragged(mouseX - this.x, mouseY - this.y, mouseButton, deltaX, deltaY))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onKeyPressed(int keyCode, int scanCode, int mods)
    {
        for (Component component : this.components)
        {
            if (component.onKeyPressed(keyCode, scanCode, mods))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onKeyReleased(int keyCode, int scanCode, int mods)
    {
        for (Component component : this.components)
        {
            if (component.onKeyPressed(keyCode, scanCode, mods))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onGainFocus()
    {
        this.components.forEach(Component::onGainFocus);
    }

    @Override
    public void onLostFocus()
    {
        this.components.forEach(Component::onLostFocus);
    }

    @Override
    public void onClose()
    {
        this.components.forEach(Component::onClose);
    }

    @Override
    public void onLayoutLoad()
    {
        this.components.forEach(Component::onLayoutLoad);
    }

    @Override
    public void onLayoutUnload()
    {
        this.components.forEach(Component::onLayoutUnload);
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
    public boolean isDirty()
    {
        if (!super.isDirty())
        {
            for (Component component : this.components)
            {
                if (component.isDirty())
                    this.markDirty();
            }
        }
        return super.isDirty();
    }

    @Override
    public void setDirty(boolean dirty)
    {
        this.components.forEach(component -> component.setDirty(dirty));
        super.setDirty(dirty);
    }
}
