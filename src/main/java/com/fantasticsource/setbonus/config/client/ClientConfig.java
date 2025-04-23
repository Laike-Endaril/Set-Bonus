package com.fantasticsource.setbonus.config.client;

import com.fantasticsource.setbonus.SetBonus;
import net.minecraftforge.common.config.Config;

public class ClientConfig
{
    @Config.Name("1. Enable Tooltips")
    @Config.LangKey(SetBonus.MODID + ".config.enableTooltips")
    @Config.Comment("Whether or not to show set bonus information in discovered set item tooltips")
    public boolean enableTooltips = true;

    @Config.Name("2. Dynamic Tooltip Searching (JEI/HEI)")
    @Config.LangKey(SetBonus.MODID + ".config.dynamicTooltipSearch")
    @Config.Comment(
            {
                    "FILLSCREEN When to reload the tooltip cache for JEI/HEI",
                    "",
                    "0 - Never",
                    "This is the default JEI / HEI behavior; searchable parts of tooltips generally won't update, so if you discover a new bonus, you might not be able to search for it until restart",
                    "No performance impact",
                    "",
                    "1 - On new bonus discovery",
                    "This will reload the tooltip cache when you discover a new bonus.  You should get accurate results when searching bonus names",
                    "Potential performance impact, but only when discovering new bonuses",
                    "",
                    "2 - On set item equip / unequip",
                    "This will reload the tooltip cache when you equip or unequip a set item.  You should get accurate results when searching bonus names or bonus requirements",
                    "Highest potential performance impact",
                    " "
            })
    @Config.RangeInt(min = 0, max = 2)
    public int dynamicTooltipSearch = 1;
}
