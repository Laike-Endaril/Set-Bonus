package com.fantasticsource.setbonus.client;

import com.fantasticsource.mctools.gui.GUIScreen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class SetBonusGUI extends GUIScreen
{
    public static SetBonusGUI setBonusGUI = new SetBonusGUI();
    private static long debounce = System.currentTimeMillis();

    public ItemStack stack = null;

    @SubscribeEvent
    public static void tooltip(RenderTooltipEvent.PostText event)
    {
//        ItemStack stack = event.getStack();
//        if (stack != ItemStack.EMPTY && BONUS_SCREEN_KEY.getKeyConflictContext().isActive() && BONUS_SCREEN_KEY.getKeyModifier().isActive() && Keyboard.isKeyDown(BONUS_SCREEN_KEY.getKeyCode()))
//        {
//            long time = System.currentTimeMillis();
//            if (time - debounce > 200)
//            {
//                debounce = time;
//                setBonusGUI.stack = stack;
//                Minecraft.getMinecraft().displayGuiScreen(setBonusGUI);
//            }
//        }
    }

    @SubscribeEvent
    public static void keyPress(InputEvent.KeyInputEvent event)
    {
//        if (BONUS_SCREEN_KEY.isPressed() && BONUS_SCREEN_KEY.getKeyConflictContext().isActive())
//        {
//            setBonusGUI.stack = null;
//            Minecraft.getMinecraft().displayGuiScreen(setBonusGUI);
//        }
    }

    @Override
    public String title()
    {
        return "Bonus Screen";
    }

    @Override
    protected void init()
    {
    }
}
