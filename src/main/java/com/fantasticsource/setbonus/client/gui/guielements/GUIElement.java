package com.fantasticsource.setbonus.client.gui.guielements;

public abstract class GUIElement
{
    public void draw(double width, double height)
    {
    }

    public void mousePressed(double x, double y)
    {
    }

    public void mouseReleased(double x, double y)
    {
    }

    public void mouseWheel(double x, double y, int delta)
    {
    }

    abstract boolean isWithin(double x, double y);
}
