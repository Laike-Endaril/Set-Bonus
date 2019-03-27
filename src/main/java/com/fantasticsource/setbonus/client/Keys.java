package com.fantasticsource.setbonus.client;

import com.fantasticsource.setbonus.SetBonus;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class Keys
{
    public static final KeyBinding BONUS_SCREEN_KEY = new KeyBinding(SetBonus.MODID + ".key.bonusScreen", KeyConflictContext.UNIVERSAL, KeyModifier.SHIFT, Keyboard.KEY_B, SetBonus.MODID + ".keyCategory");

    public static void init(FMLPreInitializationEvent event)
    {
        ClientRegistry.registerKeyBinding(BONUS_SCREEN_KEY);
    }
}
