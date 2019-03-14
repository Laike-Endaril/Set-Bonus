package com.fantasticsource.setbonus.config;

import com.fantasticsource.setbonus.common.Network;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import static com.fantasticsource.setbonus.common.SetBonus.MODID;
import static com.fantasticsource.setbonus.config.SetBonusConfig.serverSettings;

public class SyncedConfig
{
    public static int configChanged = 0;


    public static String[] equipment = serverSettings.equipment.clone();
    public static String[] sets = serverSettings.sets.clone();
    public static String[] attributeMods = serverSettings.attributeMods.clone();
    public static String[] potions = serverSettings.potions.clone();


    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
        configChanged = 2;
    }

    @SubscribeEvent
    public static void playerJoin(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayerMP && !entity.world.isRemote)
        {
            sendConfig((EntityPlayerMP) entity);
        }
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event)
    {
        if (configChanged > 0) configChanged--;
    }

    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent event)
    {
        World world = event.world;
        if (configChanged > 0 && !world.isRemote)
        {
            reset();
            for (EntityPlayer player : world.playerEntities)
            {
                sendConfig((EntityPlayerMP) player);
            }
        }
    }

    @SubscribeEvent
    public static void disconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        reset();
    }

    private static void sendConfig(EntityPlayerMP player)
    {
        Network.WRAPPER.sendTo(new Network.ConfigPacket(), player);
    }

    private static void reset()
    {
        equipment = serverSettings.equipment.clone();
        sets = serverSettings.sets.clone();
        attributeMods = serverSettings.attributeMods.clone();
        potions = serverSettings.potions.clone();
    }
}
