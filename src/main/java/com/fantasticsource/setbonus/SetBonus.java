package com.fantasticsource.setbonus;

import com.fantasticsource.setbonus.server.Data;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = SetBonus.MODID, name = SetBonus.NAME, version = SetBonus.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.003,)", acceptableRemoteVersions = "*")
public class SetBonus
{
    public static final String MODID = "setbonus";
    public static final String NAME = "Set Bonus";
    public static final String VERSION = "1.12.2.000";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(SetBonus.class);
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event)
    {
        Data.init();
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

//    @SubscribeEvent
//    public static void test(PlayerInteractEvent.RightClickBlock event)
//    {
//        for (SetData data : Data.sets.values())
//        {
//            System.out.println(data.getName() + " ============================================");
//            System.out.println("Number: " + data.getNumberEquipped(event.getEntityPlayer()) + " / " + data.getMaxNumber());
//        }
//    }
}
