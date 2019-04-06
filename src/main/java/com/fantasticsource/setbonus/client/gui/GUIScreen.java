package com.fantasticsource.setbonus.client.gui;

import com.fantasticsource.setbonus.client.gui.guielements.GUIElement;
import com.fantasticsource.setbonus.client.gui.guielements.rect.GUIRectScrollView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public abstract class GUIScreen extends GuiScreen
{
    protected ArrayList<GUIElement> guiElements = new ArrayList<>();
    private ArrayList<Integer> mouseButtons = new ArrayList<>();

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
    public void onResize(Minecraft mcIn, int w, int h)
    {
        super.onResize(mcIn, w, h);

        for (GUIElement element : guiElements)
        {
            if (element instanceof GUIRectScrollView) ((GUIRectScrollView) element).recalc(w, h);
        }
    }

    @Override
    public void handleMouseInput()
    {
        //Cancel if outside game window
        if (!Mouse.isInsideWindow())
        {
            //Note: isInsideWindow returns true if you drag a mouse button from inside the window to outside it, until you release said button (inclusive)
            Mouse.getDWheel(); //Clear the wheel delta, or it will trigger when mouse re-enters window
            return;
        }


        //General setup
        double x = (double) Mouse.getX() / mc.displayWidth;
        int displayHeight = mc.displayHeight;
        double y = (double) (displayHeight - 1 - Mouse.getY()) / displayHeight;


        //Mouse wheel
        int delta = Mouse.getDWheel();
        if (delta != 0)
        {
            for (GUIElement element : guiElements)
            {
                element.mouseWheel(x, y, delta);
            }
        }


        //Mouse press, release, and drag
        int btn = Mouse.getEventButton();
        if (btn != -1)
        {
            if (Mouse.isButtonDown(btn))
            {
                mouseButtons.add(btn);
                for (GUIElement element : guiElements)
                {
                    element.mousePressed(x, y, btn);
                }
            }
            else
            {
                mouseButtons.remove(btn);
                for (GUIElement element : guiElements)
                {
                    element.mouseReleased(x, y, btn);
                }
            }
        }
        else
        {
            for (int b : mouseButtons)
            {
                for (GUIElement element : guiElements)
                {
                    element.mouseDrag(x, y, b);
                }
            }
        }
    }
}
