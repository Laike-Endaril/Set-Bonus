package com.fantasticsource.setbonus;

import com.fantasticsource.setbonus.server.Data;
import com.fantasticsource.setbonus.server.SetData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.LinkedHashMap;

@Mod(modid = SetBonus.MODID, name = SetBonus.NAME, version = SetBonus.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.003,)", acceptableRemoteVersions = "*")
public class SetBonus
{
    public static final String MODID = "setbonus";
    public static final String NAME = "Set Bonus";
    public static final String VERSION = "1.12.2.000";

    private static LinkedHashMap<EntityPlayer, Integer> playerTimers = new LinkedHashMap<>();

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

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.END)
        {
            EntityPlayer player = event.player;

            int time = 20;
            if (!playerTimers.containsKey(player)) updateBonuses(player);
            else
            {
                time = playerTimers.get(player) - 1;
                if (time <= 0)
                {
                    updateBonuses(player);
                    time = 20;
                }
            }
            playerTimers.put(player, time);
        }
    }

    private static void updateBonuses(EntityPlayer player)
    {
        for (SetData data : Data.sets.values())
        {
            data.updateBonuses(player);
        }
    }
}
