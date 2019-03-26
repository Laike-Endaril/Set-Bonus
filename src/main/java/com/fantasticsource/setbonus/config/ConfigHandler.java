package com.fantasticsource.setbonus.config;

import com.fantasticsource.setbonus.SetBonus;

import java.io.File;

public class ConfigHandler
{
    public static final String CONFIG_NAME = SetBonus.MODID + "/" + SetBonus.CONFIG_VERSION + "+";
    public static final String FULL_CONFIG_NAME = new File("config/" + CONFIG_NAME).getAbsolutePath();
}
