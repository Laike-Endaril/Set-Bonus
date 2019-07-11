package com.fantasticsource.setbonus.client.gui.guielements.rect;

import com.fantasticsource.tools.Tools;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.Arrays;

public class GUIRectScrollView extends GUIRectElement
{
    public double internalHeight, progress = -1;
    private GUIRectElement background;
    private ArrayList<GUIRectElement> subElements = new ArrayList<>();
    private double lastScreenWidth, lastScreenHeight, top, bottom;

    public GUIRectScrollView(GUIRectElement background, double screenWidth, double screenHeight, GUIRectElement... subElements)
    {
        super(background.x, background.y, background.width, background.height);

        this.background = background;
        this.subElements.addAll(Arrays.asList(subElements));

        recalc(screenWidth, screenHeight);
    }

    public void recalc(double screenWidth, double screenHeight)
    {
        if (screenWidth == lastScreenWidth && screenHeight == lastScreenHeight) return;
        lastScreenWidth = screenWidth;
        lastScreenHeight = screenHeight;


        double pxWidth = screenWidth * width;
        internalHeight = 0;
        for (GUIRectElement element : subElements)
        {
            element.parent = this;
            if (element instanceof GUITextRect) ((GUITextRect) element).recalcHeight(pxWidth, screenHeight);
            internalHeight = Tools.max(internalHeight, element.y + element.height);
        }

        recalc2();
    }

    private void recalc2()
    {
        if (internalHeight <= height)
        {
            progress = -1;
            top = 0;
        }
        else
        {
            if (progress == -1) progress = 0;
            top = (internalHeight - height) * progress;
        }
        bottom = top + height;
    }

    @Override
    public void draw(double screenWidth, double screenHeight)
    {
        recalc2();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, -top, 0);

        for (GUIRectElement element : subElements)
        {
            if (element.y + height < top || element.y >= bottom) continue;
            element.draw(screenWidth, screenHeight);
        }

        GlStateManager.popMatrix();


        background.draw(screenWidth, screenHeight);
    }

    @Override
    public void mousePressed(double x, double y, int button)
    {
        recalc2();
        y -= top;
        for (GUIRectElement element : subElements)
        {
            element.mousePressed(x, y, button);
        }
    }

    @Override
    public void mouseReleased(double x, double y, int button)
    {
        recalc2();
        y -= top;
        for (GUIRectElement element : subElements)
        {
            element.mouseReleased(x, y, button);
        }
    }

    @Override
    public void mouseDrag(double x, double y, int button)
    {
        recalc2();
        y -= top;
        for (GUIRectElement element : subElements)
        {
            element.mouseDrag(x, y, button);
        }
    }

    @Override
    public void mouseWheel(double x, double y, int delta)
    {
        recalc2();
        y -= top;
        for (GUIRectElement element : subElements)
        {
            element.mouseWheel(x, y, delta);
        }
    }

    @Override
    public double childMouseYOffset()
    {
        return top;
    }
}
