package com.fantasticsource.setbonus.client.gui.guielements.rect;

import com.fantasticsource.setbonus.client.gui.guielements.GUIElement;

public abstract class GUIRectElement extends GUIElement
{
    public double width, height;

    public GUIRectElement(double x, double y, double width, double height)
    {
        super(x, y);
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean isWithin(double x, double y)
    {
        return this.x <= x && x < this.x + width && this.y <= y && y < this.y + height;
    }
}
