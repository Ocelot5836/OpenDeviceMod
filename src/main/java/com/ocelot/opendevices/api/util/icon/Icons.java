package com.ocelot.opendevices.api.util.icon;

import com.ocelot.opendevices.OpenDevices;
import com.ocelot.opendevices.api.computer.desktop.DesktopBackgroundType;
import net.minecraft.util.ResourceLocation;

/**
 * <p>A collection of in-built icons that can be used as an implementation of {@link IIcon}.</p>
 *
 * @author MrCrayfish
 */
public enum Icons implements IIcon
{
    ARROW_RIGHT,
    ARROW_DOWN,
    ARROW_UP,
    ARROW_LEFT,
    CHECK,
    CROSS,
    PLUS,
    IMPORT,
    EXPORT,
    NEW_FILE,
    NEW_FOLDER,
    LOAD,
    COPY,
    CUT,
    CLIPBOARD,
    SAVE,
    TRASH,
    RENAME,
    EDIT,
    SEARCH,
    RELOAD,
    CHEESE,
    CHEVRON_RIGHT,
    CHEVRON_DOWN,
    CHEVRON_UP,
    CHEVRON_LEFT,
    MAIL,
    BELL,
    LOCK,
    UNLOCK,
    KEY,
    WIFI_HIGH,
    WIFI_MED,
    WIFI_LOW,
    WIFI_NONE,
    PLAY,
    STOP,
    PAUSE,
    PREVIOUS,
    NEXT,
    INFO,
    WARNING,
    ERROR,
    VOLUME_ON,
    VOLUME_OFF,
    STAR_OFF,
    STAR_HALF,
    STAR_ON,
    CHAT,
    EJECT,
    CLOCK,
    SHOPPING_CART,
    SHOPPING_CART_ADD,
    SHOPPING_CART_REMOVE,
    USER,
    USER_ADD,
    USER_REMOVE,
    COMMUNITY,
    SHARE,
    CONTACTS,
    COMPUTER,
    PRINTER,
    GAME_CONTROLLER,
    CAMERA,
    HEADPHONES,
    TELEVISION,
    SMART_PHONE,
    USB,
    INTERNAL_DRIVE,
    EXTERNAL_DRIVE,
    NETWORK_DRIVE,
    DATABASE,
    CD,
    BATTERY_FULL,
    BATTERY_HALF,
    BATTERY_LOW,
    BATTERY_EMPTY,
    POWER_ON,
    POWER_OFF,
    EARTH,
    PICTURE,
    SHOP,
    HOME,
    THUMBS_UP_OFF,
    THUMBS_UP_ON,
    THUMBS_DOWN_OFF,
    THUMBS_DOWN_ON,
    BOOKMARK_OFF,
    BOOKMARK_ON,
    UNDO,
    REDO,
    WRENCH,
    HAMMER,
    FORBIDDEN,
    MUSIC,
    EYE_DROPPER,
    DOTS_VERTICAL,
    DOTS_HORIZONTAL,
    EXPAND,
    SHRINK,
    SORT,
    FONT,
    ALIGN_LEFT,
    ALIGN_CENTER,
    ALIGN_RIGHT,
    ALIGN_JUSTIFY,
    COIN,
    CASH,
    VERIFIED,
    BOOK_CLOSED,
    BOOK_OPEN,
    VIDEO_ROLL,
    VIDEO_CAMERA,
    LIGHT_BULB_OFF,
    LIGHT_BULB_ON,
    LOCATION,
    SEND,
    LOGIN,
    LOGOUT,
    HELP,
    HEART_OFF,
    HEART_ON,
    MAP,
    BRIGHTNESS,
    GIFT_RED,
    GIFT_CYAN,
    GIFT_GREEN,
    GIFT_BLUE,
    CREDIT_CARD,
    PIN,
    VISIBILITY_OFF,
    VISIBILITY_ON,
    HOURGLASS,
    OFFLINE,
    LIVE,
    ONLINE,
    CONNECTING,
    MICROPHONE,
    MICROPHONE_MUTE,
    HEADPHONES_MUTE,
    ZOOM_IN,
    ZOOM_OUT,
    FILE,
    FOLDER,
    SAVE_AS;

    private static final ResourceLocation ICON_LOCATION = new ResourceLocation(OpenDevices.MOD_ID, "textures/gui/icons.png");

    private static final int ICON_SIZE = 10;
    private static final int GRID_SIZE = 20;

    @Override
    public ResourceLocation getIconLocation()
    {
        return ICON_LOCATION;
    }

    @Override
    public int getU()
    {
        return (this.ordinal() % GRID_SIZE) * ICON_SIZE;
    }

    @Override
    public int getV()
    {
        return (this.ordinal() / GRID_SIZE) * ICON_SIZE;
    }

    @Override
    public int getWidth()
    {
        return ICON_SIZE;
    }

    @Override
    public int getHeight()
    {
        return ICON_SIZE;
    }

    @Override
    public int getGridWidth()
    {
        return GRID_SIZE;
    }

    @Override
    public int getGridHeight()
    {
        return GRID_SIZE;
    }

    @Override
    public int getIndex()
    {
        return this.ordinal();
    }
}
