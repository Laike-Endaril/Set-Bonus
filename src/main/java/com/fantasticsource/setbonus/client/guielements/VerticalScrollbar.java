package com.fantasticsource.setbonus.client.guielements;

import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.GlStateManager;

public class VerticalScrollbar implements IGUIElement
{
    public double progress = -1;

    private double top;
    private double height;
    private double sliderHeight;
    private GradientBorder background, slider;

    public VerticalScrollbar(double left, double top, double right, double bottom, Color backgroundBorder, Color backgroundCenter, Color sliderBorder, Color sliderCenter)
    {
        this.top = top;

        double thickness = (right - left) / 3;
        height = bottom - top;
        sliderHeight = height / 10;

        background = new GradientBorder(left, top, right, bottom, thickness, backgroundBorder, backgroundCenter);
        slider = new GradientBorder(left, 0, right, sliderHeight, thickness, sliderBorder, sliderCenter);
    }

    @Override
    public void draw(double screenWidth, double screenHeight)
    {
        background.draw(screenWidth, screenHeight);

        if (progress >= 0 && progress <= 1)
        {
            double slidertop = (top + (height - sliderHeight) * progress) * screenHeight;
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, slidertop, 0);
            slider.draw(screenWidth, screenHeight);
            GlStateManager.popMatrix();
        }
    }
}
