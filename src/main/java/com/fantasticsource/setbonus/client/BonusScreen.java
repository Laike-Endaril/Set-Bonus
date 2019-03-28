package com.fantasticsource.setbonus.client;

import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import static com.fantasticsource.setbonus.client.Keys.BONUS_SCREEN_KEY;
import static org.lwjgl.opengl.GL11.GL_QUADS;

@SideOnly(Side.CLIENT)
public class BonusScreen extends GuiScreen
{
    private static long debounce = System.currentTimeMillis();
    private static final Color BLANK = new Color(0);


    public BonusScreen()
    {
        Minecraft.getMinecraft().displayGuiScreen(this);
    }

    public BonusScreen(ItemStack stack)
    {
        Minecraft.getMinecraft().displayGuiScreen(this);
    }


    @SubscribeEvent
    public static void tooltip(RenderTooltipEvent.PostText event)
    {
        ItemStack stack = event.getStack();
        if (stack != ItemStack.EMPTY && BONUS_SCREEN_KEY.getKeyConflictContext().isActive() && BONUS_SCREEN_KEY.getKeyModifier().isActive() && Keyboard.isKeyDown(BONUS_SCREEN_KEY.getKeyCode()))
        {
            long time = System.currentTimeMillis();
            if (time - debounce > 200)
            {
                debounce = time;
                new BonusScreen(stack);
            }
        }
    }

    @SubscribeEvent
    public static void keyPress(InputEvent.KeyInputEvent event)
    {
        if (BONUS_SCREEN_KEY.isPressed() && BONUS_SCREEN_KEY.getKeyConflictContext().isActive())
        {
            new BonusScreen();
        }
    }


    static double progress = 0;
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);


        Color black = new Color(0xCC), aqua = new Color(0x3366CC), white = new Color(0xFFFFFF33), white2 = new Color(0xFFFFFF77);

        drawGradientRect(0, 0, width, height, black, black, aqua, aqua);

        //Left
        drawGradientBorder(0, 0, (double) width / 3 - 10, height, 20, white, BLANK);
        drawGradientVScrollbar((double) width / 3 - 10, 0, (double) width / 3, height, white2, BLANK, white2, BLANK, -1);

        //Center
        drawGradientBorder((double) width / 3, 0, (double) width * 2 / 3, (double) height / 10, 7, white2, BLANK);
        drawGradientBorder((double) width / 3, (double) height / 10, (double) width * 2 / 3 - 10, height, 20, white, BLANK);
        drawGradientVScrollbar((double) width * 2 / 3 - 10, (double) height / 10, (double) width * 2 / 3, height, white2, BLANK, white2, BLANK, 0);

        //Right
        drawGradientBorder((double) width * 2 / 3, 0, width - 10, height, 20, white, BLANK);
        progress += 0.001;
        if (progress >= 2) progress -= 2;
        drawGradientVScrollbar(width - 10, 0, width, height, white2, BLANK, white2, BLANK, progress >= 1 ? 2 - progress : progress);


        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }


    private void drawGradientRect(double left, double top, double right, double bottom, Color topRight, Color topLeft, Color bottomLeft, Color bottomRight)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, zLevel).color(topRight.r(), topRight.g(), topRight.b(), topRight.a()).endVertex();
        bufferbuilder.pos(left, top, zLevel).color(topLeft.r(), topLeft.g(), topLeft.b(), topLeft.a()).endVertex();
        bufferbuilder.pos(left, bottom, zLevel).color(bottomLeft.r(), bottomLeft.g(), bottomLeft.b(), bottomLeft.a()).endVertex();
        bufferbuilder.pos(right, bottom, zLevel).color(bottomRight.r(), bottomRight.g(), bottomRight.b(), bottomRight.a()).endVertex();
        tessellator.draw();
    }


    private void drawGradientBorder(double left, double top, double right, double bottom, double thickness, Color border, Color center)
    {
        thickness = Tools.min(thickness, (bottom - top) / 2, (right - left) / 2);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);


        bufferbuilder.pos(right - thickness, top + thickness, zLevel).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(left + thickness, top + thickness, zLevel).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(left + thickness, bottom - thickness, zLevel).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(right - thickness, bottom - thickness, zLevel).color(center.r(), center.g(), center.b(), center.a()).endVertex();


        bufferbuilder.pos(right - thickness, top, zLevel).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(left + thickness, top, zLevel).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(left + thickness, top + thickness, zLevel).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(right - thickness, top + thickness, zLevel).color(center.r(), center.g(), center.b(), center.a()).endVertex();

        bufferbuilder.pos(right - thickness, bottom - thickness, zLevel).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(left + thickness, bottom - thickness, zLevel).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(left + thickness, bottom, zLevel).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(right - thickness, bottom, zLevel).color(border.r(), border.g(), border.b(), border.a()).endVertex();

        bufferbuilder.pos(right, top + thickness, zLevel).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(right - thickness, top + thickness, zLevel).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(right - thickness, bottom - thickness, zLevel).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(right, bottom - thickness, zLevel).color(border.r(), border.g(), border.b(), border.a()).endVertex();

        bufferbuilder.pos(left + thickness, top + thickness, zLevel).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(left, top + thickness, zLevel).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(left, bottom - thickness, zLevel).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(left + thickness, bottom - thickness, zLevel).color(center.r(), center.g(), center.b(), center.a()).endVertex();


        //Mind the vertex ordering; not only the winding order, but the specific order of corners matters here to correctly interpolate the compound gradients for corners

        bufferbuilder.pos(right, top, zLevel).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(right - thickness, top, zLevel).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(right - thickness, top + thickness, zLevel).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(right, top + thickness, zLevel).color(border.r(), border.g(), border.b(), border.a()).endVertex();

        bufferbuilder.pos(left + thickness, bottom - thickness, zLevel).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(left, bottom - thickness, zLevel).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(left, bottom, zLevel).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(left + thickness, bottom, zLevel).color(border.r(), border.g(), border.b(), border.a()).endVertex();

        bufferbuilder.pos(right - thickness, bottom - thickness, zLevel).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(right - thickness, bottom, zLevel).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(right, bottom, zLevel).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(right, bottom - thickness, zLevel).color(border.r(), border.g(), border.b(), border.a()).endVertex();

        bufferbuilder.pos(left, top, zLevel).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(left, top + thickness, zLevel).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(left + thickness, top + thickness, zLevel).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(left + thickness, top, zLevel).color(border.r(), border.g(), border.b(), border.a()).endVertex();


        tessellator.draw();
    }

    private void drawGradientVScrollbar(double left, double top, double right, double bottom, Color backgroundBorder, Color backgroundCenter, Color sliderBorder, Color sliderCenter, double progress)
    {
        double thickness = (right - left) / 3;
        drawGradientBorder(left, top, right, bottom, thickness, backgroundBorder, backgroundCenter);

        if (progress >= 0 && progress <= 1)
        {
            double height = bottom - top;
            double sliderHeight = height / 10;
            double slidertop = top + (height - sliderHeight) * progress;

            drawGradientBorder(left, slidertop, right, slidertop + sliderHeight, thickness, sliderBorder, sliderCenter);
        }
    }
}
