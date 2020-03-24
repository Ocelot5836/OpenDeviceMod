package com.ocelot.opendevices.api.component;

import com.ocelot.opendevices.api.DeviceConstants;
import com.ocelot.opendevices.api.util.SyncHelper;

public class ScrollableLayout extends Layout
{
    private int scrollHeight;

    public ScrollableLayout(int scrollHeight)
    {
        this(0, 0, DeviceConstants.LAPTOP_DEFAULT_APPLICATION_WIDTH, DeviceConstants.LAPTOP_DEFAULT_APPLICATION_HEIGHT, scrollHeight);
    }

    public ScrollableLayout(int width, int height, int scrollHeight)
    {
        this(0, 0, width, height, scrollHeight);
    }

    public ScrollableLayout(float x, float y, int width, int height, int scrollHeight)
    {
        super(x, y, width, height);
        this.scrollHeight = scrollHeight;
    }

    @Override
    protected SyncHelper createSyncHelper()
    {
        SyncHelper syncHelper = super.createSyncHelper();
        
        return syncHelper;
    }
}
