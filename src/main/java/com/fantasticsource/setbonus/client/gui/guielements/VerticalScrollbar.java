package com.fantasticsource.setbonus.client.gui.guielements;

import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.GlStateManager;

public class VerticalScrollbar extends GUIElement
{
    private double progress = 0;
    private double height, sliderHeight;
    private GradientBorder background, slider;
    private GUIElement container;
    private GUIElement[] listedElements;
    private boolean active;

    public VerticalScrollbar(double left, double top, double right, double bottom, Color backgroundBorder, Color backgroundCenter, Color sliderBorder, Color sliderCenter, GUIRectElement container, GUIRectElement... listedElements)
    {
        super(left, top);
        this.container = container;
        this.listedElements = listedElements;

        double thickness = (right - left) / 3;
        height = bottom - top;
        sliderHeight = height / 10;

        background = new GradientBorder(left, top, right, bottom, thickness, backgroundBorder, backgroundCenter);
        slider = new GradientBorder(left, 0, right, sliderHeight, thickness, sliderBorder, sliderCenter);
    }

    @Override
    public void draw(double width, double height)
    {
        background.draw(width, height);

        if (progress >= 0 && progress <= 1)
        {
            double slidertop = y + (this.height - sliderHeight) * progress;

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, slidertop, 0);

            slider.draw(width, height);

            GlStateManager.popMatrix();
        }
    }

    @Override
    public void mouseWheel(double x, double y, int delta)
    {
        if (isWithin(x, y) || container.isWithin(x, y))
        {
            if (delta < 0)
            {
                progress += 0.1;
                if (progress > 1) progress = 1;
            }
            else
            {
                progress -= 0.1;
                if (progress < 0) progress = 0;
            }
        }
    }

    public boolean isWithin(double x, double y)
    {
        return background.isWithin(x, y);
    }

    @Override
    public void mousePressed(double x, double y, int button)
    {
        if (progress != -1 && button == 0 && isWithin(x, y))
        {
            active = true;
            progress = Tools.min(Tools.max((y - this.y) / height, 0), 1);
        }
        System.out.println(progress);
    }

    @Override
    public void mouseReleased(double x, double y, int button)
    {
        if (button == 0) active = false;
        System.out.println(progress);
    }

    @Override
    public void mouseDrag(double x, double y, int button)
    {
        if (active && button == 0)
        {
            if (progress == -1) active = false;
            else progress = Tools.min(Tools.max((y - this.y) / height, 0), 1);
        }
        System.out.println(progress);
    }
}
