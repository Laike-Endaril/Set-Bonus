package com.fantasticsource.setbonus.config;

import com.fantasticsource.setbonus.common.Data;
import com.fantasticsource.setbonus.common.Network;
import net.minecraft.entity.player.EntityPlayerMP;

import static com.fantasticsource.setbonus.config.SetBonusConfig.serverSettings;

public class SyncedConfig
{
    public static String[] equipment = serverSettings.equipment.clone();
    public static String[] sets = serverSettings.sets.clone();

    public static String[] bonuses = serverSettings.bonuses.clone();

    public static String[] attributeMods = serverSettings.attributeMods.clone();
    public static String[] potions = serverSettings.potions.clone();


    public static void sendConfig(EntityPlayerMP player)
    {
        Network.WRAPPER.sendTo(new Network.ConfigPacket(player), player);
    }

    public static void reloadFromConfig()
    {
        equipment = serverSettings.equipment.clone();
        sets = serverSettings.sets.clone();

        bonuses = serverSettings.bonuses.clone();

        attributeMods = serverSettings.attributeMods.clone();
        potions = serverSettings.potions.clone();

        Data.update();
    }
}
