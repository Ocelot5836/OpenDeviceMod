package com.ocelot.opendevices.api.component;

import com.mojang.blaze3d.platform.GlStateManager;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.laptop.window.Window;
import com.ocelot.opendevices.api.util.RenderUtil;
import com.ocelot.opendevices.api.util.TooltipRenderer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
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
@SuppressWarnings("unused")
@Component.Register(OpenDevices.MOD_ID + ":layout")
public class Layout extends BasicComponent
{
    public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(OpenDevices.MOD_ID, "layout");

    private int x;
    private int y;
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

    public Layout(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = MathHelper.clamp(width, DeviceConstants.LAPTOP_MIN_APPLICATION_WIDTH, DeviceConstants.LAPTOP_MAX_APPLICATION_WIDTH + 2);
        this.height = MathHelper.clamp(height, DeviceConstants.LAPTOP_MIN_APPLICATION_HEIGHT, DeviceConstants.LAPTOP_MAX_APPLICATION_HEIGHT + DeviceConstants.LAPTOP_WINDOW_BAR_HEIGHT + 2);
        this.components = new ArrayList<>();

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
        Arrays.stream(components).forEach(this::addComponent);
    }

    /**
     * Adds the specified components to this layout.
     *
     * @param components The components to add
     */
    public void addComponents(Collection<Component> components)
    {
        components.forEach(this::addComponent);
    }

    /**
     * Adds the specified component to this layout.
     *
     * @param component The component to add
     */
    public void addComponent(Component component)
    {
        if (ComponentSerializer.getRegistryName(component.getClass()) == null)
            throw new RuntimeException("Attempted to add unregistered component to layout: '" + component.getClass().getName() + "'! Must be registered using Component#Register annotation.");
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
        GlStateManager.pushMatrix();
        GlStateManager.translatef(this.getX(), this.getY(), 0f);
        this.components.forEach(component ->
        {
            if (component.getX() + component.getWidth() >= this.x && component.getX() < this.x + this.width && component.getY() + component.getHeight() >= this.y && component.getY() < this.y + this.height)
            {
                component.setWindowPosition(this.getWindowX(), this.getWindowY());
                component.render(mouseX, mouseY, partialTicks);
            }
        });
        GlStateManager.popMatrix();
        RenderUtil.popScissor();
    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, int mouseX, int mouseY, float partialTicks)
    {
        if (this.isHovered(mouseX, mouseY))
        {
            this.components.forEach(component ->
            {
                if (component.getX() + component.getWidth() >= this.x && component.getX() < this.x + this.width && component.getY() + component.getHeight() >= this.y && component.getY() < this.y + this.height)
                {
                    component.setWindowPosition(this.getWindowX(), this.getWindowY());
                    component.renderOverlay(renderer, mouseX, mouseY, partialTicks);
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
                if (component.onMousePressed(mouseX, mouseY, mouseButton))
                {
                    return true;
                }
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
    public Layout copy()
    {
        Layout copy = new Layout(this.x, this.y, this.width, this.height);
        copy.addComponents(this.components);
        return copy;
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
    public void setWindow(Window window)
    {
        super.setWindow(window);
        this.components.forEach(component -> component.setWindow(window));
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("x", this.x);
        nbt.putInt("y", this.y);
        nbt.putInt("width", this.width);
        nbt.putInt("height", this.height);

        ListNBT components = new ListNBT();
        this.components.forEach(component -> components.add(ComponentSerializer.serializeComponent(component)));
        nbt.put("components", components);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        this.x = nbt.getInt("x");
        this.y = nbt.getInt("y");
        this.width = nbt.getInt("width");
        this.height = nbt.getInt("height");

        this.components.clear();
        ListNBT components = nbt.getList("components", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < components.size(); i++)
        {
            this.components.add(ComponentSerializer.deserializeComponent(components.getCompound(i)));
        }
    }
}
