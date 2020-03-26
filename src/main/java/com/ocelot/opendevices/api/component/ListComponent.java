package com.ocelot.opendevices.api.component;

import com.ocelot.opendevices.api.util.TooltipRenderer;

import java.util.List;

public class ListComponent<T> extends StandardComponent
{
    private float x;
    private float y;
    private int width;
    private int height;
    private int visibleHeight;

    private List<T> items;

    @Override
    public void update()
    {

    }

    @Override
    public void render(float posX, float posY, int mouseX, int mouseY, boolean main, float partialTicks)
    {

    }

    @Override
    public void renderOverlay(TooltipRenderer renderer, float posX, float posY, int mouseX, int mouseY, float partialTicks)
    {

    }

    @Override
    public float getX()
    {
        return 0;
    }

    @Override
    public float getY()
    {
        return 0;
    }

    @Override
    public int getWidth()
    {
        return 0;
    }

    @Override
    public int getHeight()
    {
        return 0;
    }

    public interface Renderer<T>
    {
        void render(float posX, float posY, ListComponent<T> component, T item, int width, int height);
    }
}
