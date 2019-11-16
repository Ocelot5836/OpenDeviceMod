package com.ocelot.opendevices.api.laptop.component;

import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
@OnlyIn(Dist.CLIENT)
public class Layout extends BasicComponent
{
    private float x;
    private float y;
    private float width;
    private float height;

    private List<Component> components;

    public Layout()
    {
        this(0, 0, DeviceConstants.LAPTOP_DEFAULT_APPLICATION_WIDTH, DeviceConstants.LAPTOP_DEFAULT_APPLICATION_HEIGHT);
    }

    public Layout(float width, float height)
    {
        this(0, 0, width, height);
    }

    public Layout(float x, float y, float width, float height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.components = new ArrayList<>();

        if (this.width < DeviceConstants.LAPTOP_MIN_APPLICATION_WIDTH || this.width > DeviceConstants.LAPTOP_MAX_APPLICATION_WIDTH || this.height > DeviceConstants.LAPTOP_MAX_APPLICATION_HEIGHT || this.height < DeviceConstants.LAPTOP_MIN_APPLICATION_HEIGHT)
        {
            throw new RuntimeException("Windows must be between " + DeviceConstants.LAPTOP_MIN_APPLICATION_WIDTH + "x" + DeviceConstants.LAPTOP_MIN_APPLICATION_HEIGHT + " and " + DeviceConstants.LAPTOP_MAX_APPLICATION_WIDTH + "x" + DeviceConstants.LAPTOP_MAX_APPLICATION_HEIGHT);
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
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        RenderUtil.pushScissor(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        this.components.forEach(component -> component.render(mouseX, mouseY, partialTicks));
        RenderUtil.popScissor();
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, int mouseX, int mouseY, float partialTicks)
    {
        this.components.forEach(component -> component.renderOverlay(renderer, mouseX, mouseY, partialTicks));
    }

    @Override
    public boolean onMousePressed(double mouseX, double mouseY, int mouseButton)
    {
        for (Component component : this.components)
        {
            if (component.onMousePressed(mouseX, mouseY, mouseButton))
            {
                return true;
            }
        }
        return super.onMousePressed(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseReleased(double mouseX, double mouseY, int mouseButton)
    {
        for (Component component : this.components)
        {
            if (component.onMouseReleased(mouseX, mouseY, mouseButton))
            {
                return true;
            }
        }
        return super.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
    {
        for (Component component : this.components)
        {
            if (component.onMouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY))
            {
                return true;
            }
        }
        return super.onMouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
    }

    @Override
    public boolean onKeyPressed(int keyCode)
    {
        return false;
    }

    @Override
    public boolean onKeyReleased(int keyCode)
    {
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
    public float getWidth()
    {
        return width;
    }

    @Override
    public float getHeight()
    {
        return height;
    }
}
