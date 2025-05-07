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
                    "Neither JEI nor HEI normally reload tooltips at runtime, but when forced to do so, JEI is actually faster at it",
                    "That being said, I highly recommend mode 1 here, and not worrying about switching away from whatever your preference is between the two; they should both be fine in mode 1",
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

    @Config.Name("3. Enable Attribute Modifier Tooltips")
    @Config.LangKey(SetBonus.MODID + ".config.enableAttributeModifierTooltips")
    @Config.Comment("Whether or not to show attribute modifier information in discovered set item tooltips")
    public boolean enableAttributeModifierTooltips = true;

    @Config.Name("4. Enable Potion Effect Tooltips")
    @Config.LangKey(SetBonus.MODID + ".config.enablePotionEffectTooltips")
    @Config.Comment("Whether or not to show potion effect information in discovered set item tooltips")
    public boolean enablePotionEffectTooltips = true;

    @Config.Name("5. Enable Enchantment Tooltips")
    @Config.LangKey(SetBonus.MODID + ".config.enableEnchantmentTooltips")
    @Config.Comment("Whether or not to show enchantment information in discovered set item tooltips")
    public boolean enableEnchantmentTooltips = true;
}
