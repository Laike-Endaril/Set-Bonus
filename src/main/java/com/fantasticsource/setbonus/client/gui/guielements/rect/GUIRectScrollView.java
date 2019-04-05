package com.fantasticsource.setbonus.client.gui.guielements.rect;

import com.fantasticsource.setbonus.client.Canvas;
import com.fantasticsource.tools.Tools;

import java.util.ArrayList;
import java.util.Arrays;

public class GUIRectScrollView extends GUIRectElement
{
    private Canvas canvas;
    private GUIRectElement background;
    private ArrayList<GUIRectElement> subElements = new ArrayList<>();
    private double viewHeight = 0, lastScreenWidth = 0, lastScreenHeight = 0;
    private double progress = 0;

    public GUIRectScrollView(GUIRectElement background, double screenWidth, double screenHeight, GUIRectElement... subElements)
    {
        super(background.x, background.y, background.width, background.height);

        this.background = background;
        this.subElements.addAll(Arrays.asList(subElements));

        recalcHeight(screenWidth, screenHeight);
    }

    public void recalcHeight(double screenWidth, double screenHeight)
    {
        if (screenWidth == lastScreenWidth || screenHeight == lastScreenHeight) return;

        lastScreenWidth = screenWidth;
        lastScreenHeight = screenHeight;


        double pxWidth = screenWidth * width;
        viewHeight = 0;
        for (GUIRectElement element : subElements)
        {
            if (element instanceof GUITextRect) ((GUITextRect) element).recalcHeight(pxWidth, screenHeight);
            viewHeight = Tools.max(viewHeight, element.x + element.height);
        }
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
