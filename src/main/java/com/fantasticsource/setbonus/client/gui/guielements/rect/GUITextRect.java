package com.fantasticsource.setbonus.client.gui.guielements.rect;

import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class GUITextRect extends GUIRectElement
{
    private static FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    private String text;
    private Color color;

    public GUITextRect(double x, double y, double width, String text, Color color)
    {
        super(x, y, width, 0);
        this.text = text;
        this.color = color;
    }

    public void recalcHeight(double width, double screenHeight)
    {
        height = (double) fontRenderer.getWordWrappedHeight(text, (int) width) / screenHeight;
    }

    @Override
    public void draw(double screenWidth, double screenHeight)
    {
        GlStateManager.enableTexture2D();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        double guiScale = Minecraft.getMinecraft().gameSettings.guiScale;
        GlStateManager.scale(1 / screenWidth, 1 / screenHeight, 1);

        fontRenderer.drawString(text, (float) x, (float) y, (color.color() >> 8) | color.a() << 24, false);

        GlStateManager.popMatrix();
    }
}
