package com.fantasticsource.setbonus.config;

import com.fantasticsource.setbonus.common.SetBonus;
import com.fantasticsource.setbonus.config.client.ClientConfig;
import com.fantasticsource.setbonus.config.server.ServerConfig;
import net.minecraftforge.common.config.Config;

@Config(modid = SetBonus.MODID, name = ConfigHandler.CONFIG_NAME)
public class SetBonusConfig
{
    @Config.Name("Client Settings")
    @Config.LangKey(SetBonus.MODID + ".config.clientSettings")
    public static ClientConfig clientSettings = new ClientConfig();

    @Config.Name("Server Settings")
    @Config.LangKey(SetBonus.MODID + ".config.serverSettings")
    public static ServerConfig serverSettings = new ServerConfig();
}
