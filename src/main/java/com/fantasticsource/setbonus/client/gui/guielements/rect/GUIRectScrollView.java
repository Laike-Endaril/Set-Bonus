package com.fantasticsource.setbonus.client.gui.guielements.rect;

import com.fantasticsource.setbonus.client.Canvas;
import com.fantasticsource.tools.Tools;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.GL_QUADS;

public class GUIRectScrollView extends GUIRectElement
{
    private Canvas canvas;
    private GUIRectElement background;
    private ArrayList<GUIRectElement> subElements = new ArrayList<>();
    private double internalHeight = 0, lastScreenWidth = 0, lastScreenHeight = 0;
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

        canvas = new Canvas((int) (screenWidth * width), (int) (screenHeight * height));
        canvas.setTarget();
        GlStateManager.color(0, 0, 0, 1);
        GlStateManager.glBegin(GL_QUADS);
        GlStateManager.glVertex3f(30, -30, 0);
        GlStateManager.glVertex3f(-30, -30, 0);
        GlStateManager.glVertex3f(-30, 30, 0);
        GlStateManager.glVertex3f(30, 30, 0);
        GlStateManager.glEnd();
        canvas.resetTarget((int) screenWidth, (int) screenHeight);
    }

    @Override
    public void draw(double screenWidth, double screenHeight)
    {
        background.draw(screenWidth, screenHeight);

        for (GUIRectElement element : subElements)
        {
            //TODO Update canvas if elements changed
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(1 / screenWidth, 1 / screenHeight, 1);
        canvas.draw((int) x, (int) y);
        GlStateManager.popMatrix();
    }
}
