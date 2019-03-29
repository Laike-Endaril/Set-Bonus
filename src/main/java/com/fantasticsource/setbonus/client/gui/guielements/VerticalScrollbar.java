package com.fantasticsource.setbonus.client.gui.guielements;

import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.GlStateManager;

public class VerticalScrollbar extends GUIElement
{
    public double progress = -1;

    private double top, height, sliderHeight;
    private GradientBorder background, slider;
    private GUIElement[] linkedElements;

    public VerticalScrollbar(double left, double top, double right, double bottom, Color backgroundBorder, Color backgroundCenter, Color sliderBorder, Color sliderCenter, GUIElement... linkedElements)
    {
        this.top = top;
        this.linkedElements = linkedElements;

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
            double slidertop = top + (this.height - sliderHeight) * progress;

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, slidertop, 0);

            slider.draw(width, height);

            GlStateManager.popMatrix();
        }
    }

    @Override
    public void mouseWheel(double x, double y, int delta)
    {
        boolean go = isWithin(x, y);

        if (!go)
        {
            for (GUIElement element : linkedElements)
            {
                if (element.isWithin(x, y))
                {
                    go = true;
                    break;
                }
            }
        }

        if (go)
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

    @Override
    boolean isWithin(double x, double y)
    {
        return background.isWithin(x, y);
    }
}
