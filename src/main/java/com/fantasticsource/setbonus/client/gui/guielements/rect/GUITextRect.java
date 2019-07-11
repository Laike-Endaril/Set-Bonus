package com.fantasticsource.setbonus.client.gui.guielements.rect;

import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class GUITextRect extends GUIRectElement
{
    private static FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    private String text;
    private Color color, hoverColor, pressedColor;
    private boolean active = false;

    public GUITextRect(double x, double y, double width, String text, Color color, Color hoverColor, Color pressedColor)
    {
        super(x, y, width, 0);
        this.text = text;
        this.color = color;
        this.hoverColor = hoverColor;
        this.pressedColor = pressedColor;
    }

    public void recalcHeight(double width, double screenHeight)
    {
        height = (double) fontRenderer.getWordWrappedHeight(text, (int) width) / screenHeight;
    }

    @Override
    public void mousePressed(double x, double y, int button)
    {
        if (button != 0 || !isMouseWithin()) return;

        active = true;
    }

    @Override
    public void mouseReleased(double x, double y, int button)
    {
        if (button != 0 || !active) return;
        active = false;

        if (!isMouseWithin()) return;

        //TODO Do stuff here!
        System.out.println(text + " pressed!");
    }

    @Override
    public void draw(double screenWidth, double screenHeight)
    {
        double xx = getScreenX(), yy = getScreenY();
        Color c;

        //Hitbox debugging
//        c = new Color(255, isWithin(getMouseX(), getMouseY()) ? 255 : 0, 0, 100);
//        new GradientRect(xx, yy, xx + width, yy + height, c, c, c, c).draw(screenWidth, screenHeight);


        GlStateManager.enableTexture2D();

        GlStateManager.pushMatrix();
        GlStateManager.translate(xx, yy, 0);
        GlStateManager.scale(1 / screenWidth, 1 / screenHeight, 1);

        c = !isMouseWithin() ? color : active ? pressedColor : hoverColor;
        fontRenderer.drawString(text, 0, 0, (c.color() >> 8) | c.a() << 24, false);

        GlStateManager.popMatrix();
    }
}
