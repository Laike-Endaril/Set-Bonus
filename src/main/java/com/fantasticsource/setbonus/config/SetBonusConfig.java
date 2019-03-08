package com.fantasticsource.setbonus.config;

import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.config.client.ClientConfig;
import com.fantasticsource.setbonus.config.server.ServerConfig;
import net.minecraftforge.common.config.Config;

@Config(modid = SetBonus.MODID)
public class SetBonusConfig
{
    @Config.Name("Client Settings")
    public static ClientConfig clientSettings = new ClientConfig();

    @Config.Name("Server Settings")
    public static ServerConfig serverSettings = new ServerConfig();
}
