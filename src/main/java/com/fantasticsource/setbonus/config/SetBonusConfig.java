package com.fantasticsource.setbonus.config;

import com.fantasticsource.setbonus.config.client.ClientConfig;
import com.fantasticsource.setbonus.config.server.ServerConfig;
import net.minecraftforge.common.config.Config;

public class SetBonusConfig
{
    @Config.Name("Client Settings")
    public static ClientConfig clientSettings = new ClientConfig();

    @Config.Name("Server Settings")
    public static ServerConfig serverSettings = new ServerConfig();
}
