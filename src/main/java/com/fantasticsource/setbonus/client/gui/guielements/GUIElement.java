package com.fantasticsource.setbonus.client.gui.guielements;

import com.fantasticsource.setbonus.client.gui.GUIScreen;

import java.util.ArrayList;

public abstract class GUIElement
{
    public GUIElement parent = null;
    public ArrayList<GUIElement> children = new ArrayList<>();
    public double x, y;

    public GUIElement(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public abstract boolean isWithin(double x, double y);

    public void draw(double screenWidth, double screenHeight)
    {
        for (GUIElement child : children) child.draw(screenWidth, screenHeight);
    }

    public void mouseWheel(double x, double y, int delta)
    {
        for (GUIElement child : children) child.mouseWheel(x - this.x, y - this.y, delta);
    }

    public void mousePressed(double x, double y, int button)
    {
        for (GUIElement child : children) child.mousePressed(x - this.x, y - this.y, button);
    }

    public void mouseReleased(double x, double y, int button)
    {
        for (GUIElement child : children) child.mouseReleased(x - this.x, y - this.y, button);
    }

    public void mouseDrag(double x, double y, int button)
    {
        for (GUIElement child : children) child.mouseDrag(x - this.x, y - this.y, button);
    }

    public double getScreenX()
    {
        if (parent == null) return x;
        return parent.getScreenX() + x;
    }

    public double getScreenY()
    {
        if (parent == null) return y;
        return parent.getScreenY() + y;
    }

    public double mouseX()
    {
        if (parent == null) return GUIScreen.mouseX;
        return parent.mouseX() + parent.childMouseXOffset();
    }

    public double mouseY()
    {
        if (parent == null) return GUIScreen.mouseY;
        return parent.mouseY() + parent.childMouseYOffset();
    }

    public double childMouseXOffset()
    {
        return 0;
    }

    public double childMouseYOffset()
    {
        return 0;
    }

    public boolean isMouseWithin()
    {
        return isWithin(mouseX(), mouseY());
    }
}
