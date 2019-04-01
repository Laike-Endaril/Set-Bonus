package com.fantasticsource.setbonus.client.gui.guielements;

public abstract class GUIRectElement extends GUIElement
{
    protected double width, height;

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
