package com.fantasticsource.setbonus.client;

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


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);


        Color black = new Color(0xCC), aqua = new Color(0x3366CC), white = new Color(0xFFFFFF33);

        drawGradientRect(0, 0, width, height, black, black, aqua, aqua);

        drawVBar(0, height, (double) width / 3, white, white);
        drawVBar(0, height, (double) width * 2 / 3, white, white);


        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }


    private void drawHBar(int left, int right, int y, Color leftC, Color rightC)
    {

    }

    private void drawVBar(double top, double bottom, double x, Color topC, Color bottomC)
    {
        drawGradientRect(x - 1, top, x, bottom, topC, BLANK, BLANK, bottomC);
        drawGradientRect(x, top, x + 1, bottom, BLANK, topC, bottomC, BLANK);
    }


    private void drawGradientRect(double left, double top, double right, double bottom, Color topRight, Color topLeft, Color bottomLeft, Color bottomRight)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, zLevel).color(topRight.r(), topRight.g(), topRight.b(), topRight.a()).endVertex();
        bufferbuilder.pos(left, top, zLevel).color(topLeft.r(), topLeft.g(), topLeft.b(), topLeft.a()).endVertex();
        bufferbuilder.pos(left, bottom, zLevel).color(bottomLeft.r(), bottomLeft.g(), bottomLeft.b(), bottomLeft.a()).endVertex();
        bufferbuilder.pos(right, bottom, zLevel).color(bottomRight.r(), bottomRight.g(), bottomRight.b(), bottomRight.a()).endVertex();
        tessellator.draw();
    }


    private void drawGradientBorder(double left, double top, double right, double bottom, double thickness, Color topRight, Color topLeft, Color bottomLeft, Color bottomRight, Color center)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, zLevel).color(topRight.r(), topRight.g(), topRight.b(), topRight.a()).endVertex();
        bufferbuilder.pos(left, top, zLevel).color(topLeft.r(), topLeft.g(), topLeft.b(), topLeft.a()).endVertex();
        bufferbuilder.pos(left, bottom, zLevel).color(bottomLeft.r(), bottomLeft.g(), bottomLeft.b(), bottomLeft.a()).endVertex();
        bufferbuilder.pos(right, bottom, zLevel).color(bottomRight.r(), bottomRight.g(), bottomRight.b(), bottomRight.a()).endVertex();
        tessellator.draw();
    }
}
