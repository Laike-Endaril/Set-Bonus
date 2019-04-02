package com.fantasticsource.setbonus.client.gui.guielements;

import com.fantasticsource.tools.Tools;

import java.util.ArrayList;
import java.util.Arrays;

public class GUIRectScrollView extends GUIRectElement
{
    private GUIRectElement background;
    private ArrayList<GUIRectElement> subElements = new ArrayList<>();
    private double viewHeight = 0;
    private double progress = 0;

    public GUIRectScrollView(GUIRectElement background, GUIRectElement... subElements)
    {
        super(background.x, background.y, background.width, background.height);

        this.background = background;
        this.subElements.addAll(Arrays.asList(subElements));
        recalcHeight();
    }

    public void recalcHeight()
    {
        viewHeight = 0;
        for (GUIRectElement element : subElements) viewHeight = Tools.max(viewHeight, element.x + element.height);
    }

    @Override
    public void draw(double screenWidth, double screenHeight)
    {
        background.draw(screenWidth, screenHeight);

        for (GUIRectElement element : subElements)
        {
        }
    }
}
