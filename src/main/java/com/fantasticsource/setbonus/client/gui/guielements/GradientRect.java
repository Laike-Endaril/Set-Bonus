package com.fantasticsource.setbonus.client.gui.guielements;

import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import static org.lwjgl.opengl.GL11.GL_QUADS;

public class GradientRect extends GUIElement
{
    private double left, top, right, bottom;
    private Color topRight, topLeft, bottomLeft, bottomRight;

    public GradientRect(double left, double top, double right, double bottom, Color topRight, Color topLeft, Color bottomLeft, Color bottomRight)
    {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.topRight = topRight;
        this.topLeft = topLeft;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }

    @Override
    public void draw(double width, double height)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, 0).color(topRight.r(), topRight.g(), topRight.b(), topRight.a()).endVertex();
        bufferbuilder.pos(left, top, 0).color(topLeft.r(), topLeft.g(), topLeft.b(), topLeft.a()).endVertex();
        bufferbuilder.pos(left, bottom, 0).color(bottomLeft.r(), bottomLeft.g(), bottomLeft.b(), bottomLeft.a()).endVertex();
        bufferbuilder.pos(right, bottom, 0).color(bottomRight.r(), bottomRight.g(), bottomRight.b(), bottomRight.a()).endVertex();
        tessellator.draw();
    }

    @Override
    boolean isWithin(double x, double y)
    {
        return left <= x && x < right && top <= y && y < bottom;
    }
}
