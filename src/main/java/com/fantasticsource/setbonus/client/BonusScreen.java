package com.fantasticsource.setbonus.client;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.guielements.rect.GUIGradientBorder;
import com.fantasticsource.mctools.gui.guielements.rect.GUIGradientRect;
import com.fantasticsource.mctools.gui.guielements.rect.GUIRectElement;
import com.fantasticsource.mctools.gui.guielements.rect.GUITextRect;
import com.fantasticsource.mctools.gui.guielements.rect.view.GUIRectScrollView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

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
            guiElements.add(new GUIGradientRect(this, 0, 0, 1, 1, BLACK, BLACK, AQUA, AQUA));

            //Left
            GUIRectElement element = new GUIGradientBorder(this, 0, 0, 19d / 60, 1, 1d / 15, WHITE, BLANK);
            GUIRectElement[] elements = new GUIRectElement[]
                    {
                            new GUITextRect(this, 0, 0, "Test1", WHITE, AQUA, WHITE_3)
                    };
            GUIRectScrollView scrollView = new GUIRectScrollView(this, element.x, element.y, element.width, element.height, elements);
            guiElements.add(scrollView);
            guiElements.add(element);
            guiElements.add(new GUIVerticalScrollbar(this, 19d / 60, 0, 1d / 60, 1, WHITE_2, BLANK, WHITE_2, BLANK, scrollView));

            //Separator
            guiElements.add(new GUIGradientRect(this, 1d / 3, 0, 1d / 120, 1, WHITE_2, WHITE_3, WHITE_3, WHITE_2));

            //Center
            guiElements.add(new GUIGradientBorder(this, 41d / 120, 0, 39d / 120, 1d / 10, 1d / 50, WHITE_2, BLANK));
            element = new GUIGradientBorder(this, 41d / 120, 1d / 10, 37d / 120, 9d / 10, 1d / 15, WHITE, BLANK);
            scrollView = new GUIRectScrollView(this, element.x, element.y, element.width, element.height);
            guiElements.add(scrollView);
            guiElements.add(element);
            guiElements.add(new GUIVerticalScrollbar(this, 39d / 60, 1d / 10, 1d / 60, 1, WHITE_2, BLANK, WHITE_2, BLANK, scrollView));

            //Separator
            guiElements.add(new GUIGradientRect(this, 2d / 3, 0, 1d / 120, 1, WHITE_2, WHITE_3, WHITE_3, WHITE_2));

            //Right
            element = new GUIGradientBorder(this, 81d / 120, 0, 37d / 120, 1, 1d / 15, WHITE, BLANK);
            scrollView = new GUIRectScrollView(this, element.x, element.y, element.width, element.height);
            guiElements.add(scrollView);
            guiElements.add(element);
            guiElements.add(new GUIVerticalScrollbar(this, 59d / 60, 0, 1d / 60, 1, WHITE_2, BLANK, WHITE_2, BLANK, scrollView));
        }
    }
}
