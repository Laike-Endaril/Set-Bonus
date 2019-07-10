package com.fantasticsource.setbonus.client;

import com.fantasticsource.setbonus.client.gui.GUIScreen;
import com.fantasticsource.setbonus.client.gui.guielements.VerticalScrollbar;
import com.fantasticsource.setbonus.client.gui.guielements.rect.*;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

import static com.fantasticsource.setbonus.client.Keys.BONUS_SCREEN_KEY;

public class BonusScreen extends GUIScreen
{
    private static final Color BLANK = new Color(0), BLACK = new Color(0xCC), AQUA = new Color(0x3366CC), WHITE = new Color(0xFFFFFF33), WHITE_2 = new Color(0xFFFFFF77), WHITE_3 = new Color(0xFFFFFFAA);
    public static BonusScreen bonusScreen = new BonusScreen();
    private static long debounce = System.currentTimeMillis();
    private static boolean ready = false;

    public ItemStack stack = null;

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
                bonusScreen.stack = stack;
                Minecraft.getMinecraft().displayGuiScreen(bonusScreen);
            }
        }
    }

    @SubscribeEvent
    public static void keyPress(InputEvent.KeyInputEvent event)
    {
        if (BONUS_SCREEN_KEY.isPressed() && BONUS_SCREEN_KEY.getKeyConflictContext().isActive())
        {
            bonusScreen.stack = null;
            Minecraft.getMinecraft().displayGuiScreen(bonusScreen);
        }
    }

    @Override
    public void initGui()
    {
        if (!ready)
        {
            ready = true;

            //Background
            guiElements.add(new GradientRect(0, 0, 1, 1, BLACK, BLACK, AQUA, AQUA));

            //Left
            ArrayList<GUIRectElement> subElements = new ArrayList<>();
            //TODO enable this and see how it goes beyond the boundary of its container...need to clip that somehow
//            subElements.add(new GradientRect(0, 0, 0.5, 1.5, new Color(0xFFFFFFFF), new Color(0xFF), new Color(0xFFFFFFFF), new Color(0xFF)));
//            subElements.add(new GradientRect(0.1, 0, 0.3, 1.5, new Color(0xFFFFFFFF), new Color(0xFF), new Color(0xFFFFFFFF), new Color(0xFF)));
            for (double y = 0.01; y < 1.5; y += 0.1)
            {
                subElements.add(new GUITextRect(0.1, y, 1, "Test", WHITE_3));
            }

            GUIRectElement element = new GradientBorder(0, 0, 19d / 60, 1, 1d / 15, WHITE, BLANK);
            GUIRectScrollView scrollView = new GUIRectScrollView(element, width, height, subElements.toArray(new GUIRectElement[0]));
            guiElements.add(scrollView);
            guiElements.add(new VerticalScrollbar(19d / 60, 0, 1d / 3, 1, WHITE_2, BLANK, WHITE_2, BLANK, scrollView));

            //Separator
            guiElements.add(new GradientRect(1d / 3, 0, 41d / 120, 1, WHITE_2, WHITE_3, WHITE_3, WHITE_2));

            //Center
            guiElements.add(new GradientBorder(41d / 120, 0, 2d / 3, 1d / 10, 1d / 50, WHITE_2, BLANK));
            element = new GradientBorder(41d / 120, 1d / 10, 39d / 60, 1, 1d / 15, WHITE, BLANK);
            scrollView = new GUIRectScrollView(element, width, height);
            guiElements.add(scrollView);
            guiElements.add(new VerticalScrollbar(39d / 60, 1d / 10, 2d / 3, 1, WHITE_2, BLANK, WHITE_2, BLANK, scrollView));

            //Separator
            guiElements.add(new GradientRect(2d / 3, 0, 81d / 120, 1, WHITE_2, WHITE_3, WHITE_3, WHITE_2));

            //Right
            element = new GradientBorder(81d / 120, 0, 59d / 60, 1, 1d / 15, WHITE, BLANK);
            scrollView = new GUIRectScrollView(element, width, height);
            guiElements.add(scrollView);
            guiElements.add(new VerticalScrollbar(59d / 60, 0, 1, 1, WHITE_2, BLANK, WHITE_2, BLANK, scrollView));
        }
    }
}
