package com.fantasticsource.setbonus.config.client;

import com.fantasticsource.setbonus.SetBonus;
import net.minecraftforge.common.config.Config;

public class ClientConfig
{
    @Config.Name("1. Enable Tooltips")
    @Config.LangKey(SetBonus.MODID + ".config.enableTooltips")
    @Config.Comment("Whether or not to show set bonus information in discovered set item tooltips")
    public boolean enableTooltips = true;
}
