package com.fantasticsource.setbonus.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
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
    static long debounce = System.currentTimeMillis();


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
        drawDefaultBackground();
    }
}
