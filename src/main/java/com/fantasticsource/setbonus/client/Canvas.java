package com.fantasticsource.setbonus.client;

import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.GlStateManager;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Canvas
{
    private int width, height, texture, fbo;
    private boolean destroyed = false;

    public Canvas(int width, int height)
    {
        this.width = width;
        this.height = height;

        fbo = glGenFramebuffers();
        texture = glGenTextures();

        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        GlStateManager.bindTexture(texture);
        GlStateManager.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        GlStateManager.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        GlStateManager.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_INT, (java.nio.IntBuffer) null);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void destroy()
    {
        if (!destroyed)
        {
            GlStateManager.deleteTexture(texture);
            glDeleteFramebuffers(fbo);
            destroyed = true;
        }
    }

    public void finalize()
    {
        destroy();
    }

    public void setTarget()
    {
        GlStateManager.loadIdentity();
        GlStateManager.viewport(0, 0, width, height);
        GlStateManager.ortho(0, width, 0, height, -1, 1);
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    }

    public void resetTarget(int width, int height)
    {
        GlStateManager.loadIdentity();
        GlStateManager.viewport(0, 0, width, height);
        GlStateManager.ortho(0, width, height, 0, -1, 1);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public void draw(int x, int y)
    {
        draw(x, y, 0, 0, width, height, new Color(0xFFFFFFFF));
    }

    public void draw(int x, int y, Color color)
    {
        draw(x, y, 0, 0, width, height, color);
    }

    public void draw(int x, int y, int canvasX, int canvasY, int width, int height)
    {
        draw(x, y, canvasX, canvasY, width, height, new Color(0xFFFFFFFF));
    }

    public void draw(int x, int y, int canvasX, int canvasY, int width, int height, Color color)
    {
        float tx1 = (float) canvasX / this.width;
        float tx2 = (float) (canvasX + width) / this.width;
        float ty1 = (float) canvasY / this.height;
        float ty2 = (float) (canvasY + height) / this.height;

        GlStateManager.color(color.rf(), color.gf(), color.bf(), color.af());
        GlStateManager.bindTexture(texture);
        GlStateManager.glBegin(GL_QUADS);
        GlStateManager.glTexCoord2f(tx1, ty2);
        GlStateManager.glVertex3f(x, y + height, 0);
        GlStateManager.glTexCoord2f(tx2, ty2);
        GlStateManager.glVertex3f(x + width, y + height, 0);
        GlStateManager.glTexCoord2f(tx2, ty1);
        GlStateManager.glVertex3f(x + width, y, 0);
        GlStateManager.glTexCoord2f(tx1, ty1);
        GlStateManager.glVertex3f(x, y, 0);
        GlStateManager.glEnd();
        GlStateManager.color(1, 1, 1, 1);
    }
}
