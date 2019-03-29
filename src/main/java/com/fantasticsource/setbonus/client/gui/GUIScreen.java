package com.fantasticsource.setbonus.client.gui;

import com.fantasticsource.setbonus.client.gui.guielements.GUIElement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public abstract class GUIScreen extends GuiScreen
{
    public ArrayList<GUIElement> guiElements = new ArrayList<>();

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        GlStateManager.pushMatrix();
        GlStateManager.scale(width, height, 1);

        for (GUIElement element : guiElements) element.draw(width, height);

        GlStateManager.popMatrix();

        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    @Override
    public void handleMouseInput()
    {
        if (!Mouse.isInsideWindow())
        {
            Mouse.getDWheel(); //Clear the wheel delta, or it will trigger when mouse re-enters window
            return;
        }

        double x = (double) Mouse.getX() / mc.displayWidth;
        int displayHeight = mc.displayHeight;
        double y = (double) (displayHeight - 1 - Mouse.getY()) / displayHeight;

        int delta = Mouse.getDWheel();
        if (delta != 0)
        {
            for (GUIElement element : guiElements)
            {
                element.mouseWheel(x, y, delta);
            }
        }
    }
}
