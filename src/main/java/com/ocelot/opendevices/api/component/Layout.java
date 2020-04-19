package com.ocelot.opendevices.api.component;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.api.util.SyncHelper;
import io.github.ocelot.client.ScissorHelper;
import io.github.ocelot.client.TooltipRenderer;
import net.minecraft.nbt.ListNBT;
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
    private final int width;
    private final int height;
    private boolean visible;

    protected final List<Component> components;

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
        this.setValueSerializer(this.createSyncHelper());
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.visible = true;

        this.components = new ArrayList<>();
    }

    protected SyncHelper createSyncHelper()
    {
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
            syncHelper.addSerializer("visible", nbt -> nbt.putBoolean("visible", this.visible), nbt -> this.visible = nbt.getBoolean("visible"));
        }
        return syncHelper;
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
        if (this.visible)
        {
            this.components.forEach(Component::update);
        }
    }

    @Override
    public void render(float posX, float posY, int mouseX, int mouseY, boolean main, float partialTicks)
    {
        if (this.visible)
        {
            ScissorHelper.push(posX + this.getX(), posY + this.getY(), this.getWidth(), this.getHeight());
            this.components.forEach(component ->
            {
                if ((component.getX() + component.getWidth() >= this.getX() || component.getX() < this.getX() + this.getWidth()) && (component.getY() + component.getHeight() >= this.getY() || component.getY() < this.getY() + this.getHeight()))
                {
                    component.render(this.getX() + posX, this.getY() + posY, mouseX, mouseY, main, partialTicks);
                }
            });
            ScissorHelper.pop();
        }
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, float posX, float posY, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible && this.isHovered(mouseX - (int) posX, mouseY - (int) posY))
        {
            this.components.forEach(component ->
            {
                if ((component.getX() + component.getWidth() >= this.getX() || component.getX() < this.getX() + this.getWidth()) && (component.getY() + component.getHeight() >= this.getY() || component.getY() < this.getY() + this.getHeight()))
                {
                    component.renderOverlay(renderer, this.getX() + posX, this.getY() + posY, mouseX, mouseY, partialTicks);
                }
            });
        }
    }

    @Override
    public boolean onMousePressed(double mouseX, double mouseY, int mouseButton)
    {
        if (this.visible && this.isHovered(mouseX, mouseY))
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
        if (this.visible)
        {
            for (Component component : this.components)
            {
                if (component.onMouseReleased(mouseX - this.x, mouseY - this.y, mouseButton))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double amount)
    {
        if (this.visible && this.isHovered(mouseX, mouseY))
        {
            for (Component component : this.components)
            {
                if (component.onMouseScrolled(mouseX - this.x, mouseY - this.y, amount))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onMouseMoved(double mouseX, double mouseY)
    {
        if (this.visible)
        {
            this.components.forEach(component -> component.onMouseMoved(mouseX - this.x, mouseY - this.y));
        }
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY)
    {
        if (this.visible)
        {
            for (Component component : this.components)
            {
                if (component.onMouseDragged(mouseX - this.x, mouseY - this.y, mouseButton, deltaX, deltaY))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onKeyPressed(int keyCode, int scanCode, int mods)
    {
        if (this.visible)
        {
            for (Component component : this.components)
            {
                if (component.onKeyPressed(keyCode, scanCode, mods))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onKeyReleased(int keyCode, int scanCode, int mods)
    {
        if (this.visible)
        {
            for (Component component : this.components)
            {
                if (component.onKeyPressed(keyCode, scanCode, mods))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onGainFocus()
    {
        if (this.visible)
        {
            this.components.forEach(Component::onGainFocus);
        }
    }

    @Override
    public void onLostFocus()
    {
        if (this.visible)
        {
            this.components.forEach(Component::onLostFocus);
        }
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

    /**
     * @return Whether or not this component can be seen and interacted with
     */
    public boolean isVisible()
    {
        return visible;
    }

    /**
     * Marks this component as able to be seen or not.
     *
     * @param visible Whether or not this component is visible
     */
    public Layout setVisible(boolean visible)
    {
        this.visible = visible;
        this.getValueSerializer().markDirty("visible");
        return this;
    }

    @Override
    public boolean isDirty()
    {
        if (!super.isDirty())
        {
            for (Component component : this.components)
            {
                if (component.isDirty())
                {
                    this.getValueSerializer().markDirty("components");
                    break;
                }
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
