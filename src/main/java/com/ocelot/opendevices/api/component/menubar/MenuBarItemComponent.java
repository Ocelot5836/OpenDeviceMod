package com.ocelot.opendevices.api.component.menubar;

import javax.annotation.Nullable;
import java.util.List;

/**
 * <p>An abstract component that can be inserted into a menu bar.</p>
 *
 * @author Ocelot5836
 * @see MenuBarComponent
 * @see MenuBarItem
 */
public interface MenuBarItemComponent
{
    void render(int x, int y, int screenX, int screenY, int mouseX, int mouseY, float partialTicks);
    void renderOverlay(int mouseX, int mouseY, float partialTicks);

    boolean onClicked(double mouseX, double mouseY, int mouseButton);

    @Nullable
    List<MenuBarItemComponent> getSubList();

    /**
     * @return The padding for this button
     */
    int getPadding();
}