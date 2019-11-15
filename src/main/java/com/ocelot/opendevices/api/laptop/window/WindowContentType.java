package com.ocelot.opendevices.api.laptop.window;

import com.ocelot.opendevices.api.laptop.window.application.ApplicationLoader;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

/**
 * <p>One of two types of content that can actually be put into a {@link Window}. Wither {@link #APPLICATION} or {@link #DIALOG}</p>
 *
 * @author Ocelot
 */
public enum WindowContentType
{
    APPLICATION(applicationId -> ApplicationLoader.REGISTRY.containsKey(applicationId)), DIALOG(dialogId -> false);

    private Function<ResourceLocation, Boolean> valid;

    WindowContentType(Function<ResourceLocation, Boolean> valid)
    {
        this.valid = valid;
    }

    public boolean isValid(ResourceLocation contentId)
    {
        return valid.apply(contentId);
    }
}
