package com.fantasticsource.setbonus.config;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.setbonus.SetBonus;

import java.io.File;

public class ConfigHandler
{
    public static final String CONFIG_NAME = SetBonus.MODID + "/" + SetBonus.CONFIG_VERSION + "+";
    public static final String FULL_CONFIG_NAME = new File(MCTools.getConfigDir() + CONFIG_NAME).getAbsolutePath();


    public static void init()
    {
        File file = new File(MCTools.getConfigDir() + "setbonus.cfg");
        if (file.exists()) file.renameTo(new File(MCTools.getConfigDir() + "/setbonus/setbonus (old).cfg"));
    }
}
