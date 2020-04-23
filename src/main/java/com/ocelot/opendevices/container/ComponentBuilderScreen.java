package com.ocelot.opendevices.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.IconManager;
import com.ocelot.opendevices.api.task.TaskManager;
import com.ocelot.opendevices.core.task.SetComponentBuilderLayoutTask;
import com.ocelot.opendevices.crafting.componentbuilder.ComponentBuilderLayout;
import com.ocelot.opendevices.crafting.componentbuilder.ComponentBuilderLayoutManager;
import io.github.ocelot.client.TooltipRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public class ComponentBuilderScreen extends ContainerScreen<ComponentBuilderContainer> implements TooltipRenderer
{
    public static final ResourceLocation CONTAINER_TEXTURE = new ResourceLocation(OpenDevices.MOD_ID, "textures/gui/container/component_builder.png");

    private final List<ResourceLocation> layouts;
    private int scroll;
    private int selectedIndex;

    public ComponentBuilderScreen(ComponentBuilderContainer screenContainer, PlayerInventory playerInventory, ITextComponent title)
    {
        super(screenContainer, playerInventory, title);
        this.xSize = 176;
        this.ySize = 176;
        this.layouts = new ArrayList<>();
    }

    @Override
    protected void init()
    {
        super.init();
        this.initTabs(ComponentBuilderLayoutManager.get(this.playerInventory.player.world));
    }

    private void setBoardLayout(ResourceLocation layout)
    {
        if (this.container.getLayout() == layout)
            return;
        this.container.setLayout(layout);
        TaskManager.sendToServer(new SetComponentBuilderLayoutTask(this.container.windowId, layout), TaskManager.TaskReceiver.SENDER);
    }

    public void initTabs(ComponentBuilderLayoutManager layoutManager)
    {
        this.scroll = 0;
        this.buttons.clear();
        this.children.clear();
        this.layouts.clear();
        this.layouts.addAll(layoutManager.getKeys());
        this.layouts.sort(Comparator.comparingInt(a -> layoutManager.getLayout(a).getSlotsUsedCount()));
        if (this.selectedIndex >= layoutManager.getKeys().size())
            this.selectedIndex = 0;
        for (int i = 0; i < Math.min(7, layoutManager.getKeys().size()); i++)
            this.addButton(new ComponentBuilderLayoutTab(this, layoutManager, this.guiLeft - 21, 4 + this.guiTop + i * 24, i));
        this.setBoardLayout(this.layouts.isEmpty() ? ComponentBuilderLayout.EMPTY_LOCATION : this.layouts.get(0));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize - 96 + 3), 4210752);

        for (Widget widget : this.buttons)
        {
            if (widget.isHovered())
            {
                widget.renderToolTip(mouseX - this.guiLeft, mouseY - this.guiTop);
                break;
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        this.renderBackground();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.getMinecraft().getTextureManager().bindTexture(CONTAINER_TEXTURE);
        this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        if (this.container.hasCircuitBoard())
        {
            this.getMinecraft().getTextureManager().bindTexture(IconManager.LOCATION_OPENDEVICES_GUI_ATLAS);
            blit(this.guiLeft + 7, this.guiTop + 17, 0, 64, 64, IconManager.getBoardTexture(this.container.getInputAreaInventory().getStackInSlot(0).getItem()));
            blit(this.guiLeft + 7, this.guiTop + 17, 0, 64, 64, IconManager.getLayoutTexture(ComponentBuilderLayoutManager.get(Objects.requireNonNull(this.getMinecraft().world)).getLayout(this.container.getLayout())));
        }
    }

    public int getScroll()
    {
        return scroll;
    }

    public int getSelectedIndex()
    {
        return selectedIndex;
    }

    public ResourceLocation getLayout(int index)
    {
        return index < 0 || index >= this.layouts.size() ? ComponentBuilderLayout.EMPTY_LOCATION : this.layouts.get(index);
    }

    public void setSelectedIndex(int selectedIndex)
    {
        this.selectedIndex = selectedIndex;
        this.setBoardLayout(this.getLayout(selectedIndex));
    }

    @Override
    public void renderTooltip(ItemStack stack, int posX, int posY)
    {
        super.renderTooltip(stack, posX, posY);
    }

    @Override
    public void renderTooltip(String tooltip, int posX, int posY, FontRenderer fontRenderer)
    {
        this.renderTooltip(Collections.singletonList(tooltip), posX, posY, fontRenderer);
    }

    @Override
    public void renderComponentHoverEffect(ITextComponent textComponent, int posX, int posY)
    {
        super.renderComponentHoverEffect(textComponent, posX, posY);
    }
}
