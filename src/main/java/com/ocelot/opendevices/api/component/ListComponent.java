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

    private Renderer<T> renderer;
    private List<T> items;

    private float scroll;
    private float scrollSpeed;

    private float lastScroll;
    private float nextScroll;

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
        return visibleHeight;
    }


    public int getPhysicalHeight()
    {
        return height;
    }

    public float getScroll()
    {
        return scroll;
    }

    public float getScrollSpeed()
    {
        return scrollSpeed;
    }

    public interface Renderer<T>
    {
        void render(float posX, float posY, ListComponent<T> component, T item, int width, int height);
    }
}
