package com.fantasticsource.setbonus.client.gui.guielements;

import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import static org.lwjgl.opengl.GL11.GL_QUADS;

public class GradientRect extends GUIRectElement
{
    private Color topRight, topLeft, bottomLeft, bottomRight;

    public GradientRect(double left, double top, double right, double bottom, Color topRight, Color topLeft, Color bottomLeft, Color bottomRight)
    {
        super(left, top, right - left, bottom - top);
        this.topRight = topRight;
        this.topLeft = topLeft;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }

    @Override
    public void draw(double screenWidth, double screenHeight)
    {
        double x2 = x + width;
        double y2 = y + height;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x2, y, 0).color(topRight.r(), topRight.g(), topRight.b(), topRight.a()).endVertex();
        bufferbuilder.pos(x, y, 0).color(topLeft.r(), topLeft.g(), topLeft.b(), topLeft.a()).endVertex();
        bufferbuilder.pos(x, y2, 0).color(bottomLeft.r(), bottomLeft.g(), bottomLeft.b(), bottomLeft.a()).endVertex();
        bufferbuilder.pos(x2, y2, 0).color(bottomRight.r(), bottomRight.g(), bottomRight.b(), bottomRight.a()).endVertex();
        tessellator.draw();
    }
}
