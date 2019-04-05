package com.fantasticsource.setbonus.client.gui.guielements;

import com.fantasticsource.setbonus.client.gui.guielements.rect.GUIRectElement;
import com.fantasticsource.setbonus.client.gui.guielements.rect.GUIRectScrollView;
import com.fantasticsource.setbonus.client.gui.guielements.rect.GradientBorder;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;

public class VerticalScrollbar extends GUIRectElement
{
    private double progress = 0;
    private double height, sliderHeight;
    private GradientBorder background, slider;
    private GUIRectScrollView scrollView;
    private boolean active;

    public VerticalScrollbar(double left, double top, double right, double bottom, Color backgroundBorder, Color backgroundCenter, Color sliderBorder, Color sliderCenter, GUIRectScrollView scrollView)
    {
        super(left, top, right - left, bottom - top);
        this.scrollView = scrollView;

        double thickness = (right - left) / 3;
        background = new GradientBorder(left, top, right, bottom, thickness, backgroundBorder, backgroundCenter);
        height = background.height;
        sliderHeight = height / 10;

        slider = new GradientBorder(left, 0, right, sliderHeight, thickness, sliderBorder, sliderCenter);
    }

    @Override
    public void draw(double screenWidth, double screenHeight)
    {
        background.draw(screenWidth, screenHeight);

        if (progress >= 0 && progress <= 1)
        {
            slider.y = y + (this.height - sliderHeight) * progress;
            slider.draw(screenWidth, screenHeight);
        }
    }

    @Override
    public void mouseWheel(double x, double y, int delta)
    {
        if (isWithin(x, y) || scrollView.isWithin(x, y))
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
            progress = Tools.min(Tools.max((y - this.y - slider.height * 0.5) / (height - slider.height), 0), 1);
        }
    }

    @Override
    public void mouseReleased(double x, double y, int button)
    {
        if (button == 0) active = false;
    }

    @Override
    public void mouseDrag(double x, double y, int button)
    {
        if (active && button == 0)
        {
            if (progress == -1) active = false;
            else progress = Tools.min(Tools.max((y - this.y - slider.height * 0.5) / (height - slider.height), 0), 1);
        }
    }
}
