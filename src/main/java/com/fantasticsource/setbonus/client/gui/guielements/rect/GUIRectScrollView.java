package com.fantasticsource.setbonus.client.gui.guielements.rect;

import com.fantasticsource.tools.Tools;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_QUADS;

public class GUIRectScrollView extends GUIRectElement
{
    public double internalHeight, progress = -1;
    private GUIRectElement background;
    private ArrayList<GUIRectElement> subElements = new ArrayList<>();
    private double lastScreenWidth, lastScreenHeight;

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
            if (element instanceof GUITextRect) ((GUITextRect) element).recalcHeight(pxWidth, screenHeight);
            internalHeight = Tools.max(internalHeight, element.x + element.height);
        }
    }

    @Override
    public void draw(double screenWidth, double screenHeight)
    {
        double top;
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
        double bottom = top + height;

        GlStateManager.disableTexture2D();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, top, 0);


//        GlStateManager.scale(screenWidth, screenHeight, 1);
        for (GUIRectElement element : subElements)
        {
            if (element.y + height < top || element.y >= bottom) continue;
            element.draw(screenWidth, screenHeight);
        }


        GlStateManager.popMatrix();


        background.draw(screenWidth, screenHeight);

        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / screenWidth, 1 / screenHeight, 1);
        GlStateManager.popMatrix();
    }
}
