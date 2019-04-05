package com.fantasticsource.setbonus.client.gui.guielements.rect;

import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class GUITextRect extends GUIRectElement
{
    private static FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    private String text;

    public GUITextRect(double x, double y, double width, String text, Color color)
    {
        super(x, y, width, 0);
        this.text = text;
    }

    public void recalcHeight(double width, double screenHeight)
    {
        height = (double) fontRenderer.getWordWrappedHeight(text, (int) width) / screenHeight;
    }

    @Override
    public void draw(double screenWidth, double screenHeight)
    {
    }
}
