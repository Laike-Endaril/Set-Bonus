package com.fantasticsource.setbonus.client.gui;

import com.fantasticsource.setbonus.client.gui.guielements.GradientBorder;
import com.fantasticsource.setbonus.client.gui.guielements.GradientRect;
import com.fantasticsource.setbonus.client.gui.guielements.IGUIElement;
import com.fantasticsource.setbonus.client.gui.guielements.VerticalScrollbar;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

import static com.fantasticsource.setbonus.client.Keys.BONUS_SCREEN_KEY;

@SideOnly(Side.CLIENT)
public class BonusScreen extends GuiScreen
{
    private static final Color BLANK = new Color(0), BLACK = new Color(0xCC), AQUA = new Color(0x3366CC), WHITE = new Color(0xFFFFFF33), WHITE_2 = new Color(0xFFFFFF77), WHITE_3 = new Color(0xFFFFFFAA);
    private static long debounce = System.currentTimeMillis();
    private static ArrayList<IGUIElement> guiElements = new ArrayList<>();


    static
    {
        init();
    }


    public BonusScreen()
    {
        Minecraft.getMinecraft().displayGuiScreen(this);
    }

    public BonusScreen(ItemStack stack)
    {
        Minecraft.getMinecraft().displayGuiScreen(this);
    }


    public static void init()
    {
        //Background
        guiElements.add(new GradientRect(0, 0, 1, 1, BLACK, BLACK, AQUA, AQUA));

        //Left
        guiElements.add(new GradientBorder(0, 0, 19d / 60, 1, 1d / 15, WHITE, BLANK));
        guiElements.add(new VerticalScrollbar(19d / 60, 0, 1d / 3, 1, WHITE_2, BLANK, WHITE_2, BLANK));

        //Separator
        guiElements.add(new GradientRect(1d / 3, 0, 41d / 120, 1, WHITE_2, WHITE_3, WHITE_3, WHITE_2));

        //Center
        guiElements.add(new GradientBorder(41d / 120, 0, 2d / 3, 1d / 10, 1d / 50, WHITE_2, BLANK));
        guiElements.add(new GradientBorder(41d / 120, 1d / 10, 39d / 60, 1, 1d / 15, WHITE, BLANK));
        guiElements.add(new VerticalScrollbar(39d / 60, 1d / 10, 2d / 3, 1, WHITE_2, BLANK, WHITE_2, BLANK));

        //Separator
        guiElements.add(new GradientRect(2d / 3, 0, 81d / 120, 1, WHITE_2, WHITE_3, WHITE_3, WHITE_2));

        //Right
        guiElements.add(new GradientBorder(81d / 120, 0, 59d / 60, 1, 1d / 15, WHITE, BLANK));
        guiElements.add(new VerticalScrollbar(59d / 60, 0, 1, 1, WHITE_2, BLANK, WHITE_2, BLANK));
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

        GlStateManager.pushMatrix();
        GlStateManager.scale(width, height, 1);

        for (IGUIElement element : guiElements) element.draw(width, height);

        GlStateManager.popMatrix();

        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}
