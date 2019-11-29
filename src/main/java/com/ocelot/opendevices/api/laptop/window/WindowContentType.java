package com.ocelot.opendevices.api.laptop.window;

import com.ocelot.opendevices.api.DeviceRegistries;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

/**
 * <p>One of two types of content that can actually be put into a {@link Window}. Wither {@link #APPLICATION} or {@link #DIALOG}</p>
 *
 * @author Ocelot
 */
public enum WindowContentType
{
    APPLICATION(applicationId -> DeviceRegistries.APPLICATIONS.containsKey(applicationId)), DIALOG(dialogId -> false);

    private Function<ResourceLocation, Boolean> valid;

    WindowContentType(Function<ResourceLocation, Boolean> valid)
    {
        this.valid = valid;
    }

    /**
     * Validates if the provided content id is valid.
     *
     * @param contentId The id of the content to check
     * @return Whether or not that id is actually valid content
     */
    public boolean isValid(ResourceLocation contentId)
    {
        return valid.apply(contentId);
    }
}
