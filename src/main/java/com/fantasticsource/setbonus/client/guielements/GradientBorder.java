package com.fantasticsource.setbonus.client.guielements;

import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import static org.lwjgl.opengl.GL11.GL_QUADS;

public class GradientBorder implements IGUIElement
{
    private double left, top, right, bottom, thickness;
    private Color border, center;

    public GradientBorder(double left, double top, double right, double bottom, double borderThickness, Color border, Color center)
    {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.thickness = borderThickness;
        this.border = border;
        this.center = center;
    }

    @Override
    public void draw(double width, double height)
    {
        double min = Tools.min((right - left) * 0.5 * width, (bottom - top) * 0.5 * height, thickness * width, thickness * height);
        double xThickness = min / width;
        double yThickness = min / height;

        GlStateManager.pushMatrix();
        GlStateManager.scale(width, height, 1);


        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);


        bufferbuilder.pos(right - xThickness, top + yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(left + xThickness, top + yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(left + xThickness, bottom - yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(right - xThickness, bottom - yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();


        bufferbuilder.pos(right - xThickness, top, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(left + xThickness, top, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(left + xThickness, top + yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(right - xThickness, top + yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();

        bufferbuilder.pos(right - xThickness, bottom - yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(left + xThickness, bottom - yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(left + xThickness, bottom, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(right - xThickness, bottom, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();

        bufferbuilder.pos(right, top + yThickness, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(right - xThickness, top + yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(right - xThickness, bottom - yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(right, bottom - yThickness, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();

        bufferbuilder.pos(left + xThickness, top + yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(left, top + yThickness, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(left, bottom - yThickness, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(left + xThickness, bottom - yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();


        //Mind the vertex ordering; not only the winding order, but the specific order of corners matters here to correctly interpolate the compound gradients for corners

        bufferbuilder.pos(right, top, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(right - xThickness, top, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(right - xThickness, top + yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(right, top + yThickness, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();

        bufferbuilder.pos(left + xThickness, bottom - yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(left, bottom - yThickness, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(left, bottom, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(left + xThickness, bottom, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();

        bufferbuilder.pos(right - xThickness, bottom - yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(right - xThickness, bottom, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(right, bottom, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(right, bottom - yThickness, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();

        bufferbuilder.pos(left, top, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(left, top + yThickness, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(left + xThickness, top + yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(left + xThickness, top, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();


        tessellator.draw();


        GlStateManager.popMatrix();
    }
}
