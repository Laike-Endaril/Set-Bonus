package com.fantasticsource.setbonus.config.server;

import com.fantasticsource.setbonus.common.SetBonus;
import net.minecraftforge.common.config.Config;

public class ServerConfig
{
    @Config.Name("1. Equipment")
    @Config.LangKey(SetBonus.MODID + ".config.equipment")
    @Config.Comment(
            {
                    "Each item you want to include in a set needs to be defined here first",
                    "",
                    "The layout of each entry is...",
                    "Equipment id, domain:item:meta > nbtkey1 = nbtvalue1 & nbtkey2 = nbtvalue2...",
                    "The equipment id must be unique, and is only used to refer to the item in the equipment set config",
                    "",
                    "eg...",
                    "",
                    "SSword, stone_sword",
                    "DSword, diamond_sword",
                    "",
                    "DDagger, backstab:diamond_dagger",
                    "",
                    "TetraSickleSingle, tetra:duplex_tool_modular > duplex/sickle_left_material & duplex/butt_right_material",
                    "",
                    "WirtsLeggings, diamond_leggings > display:Name = \"Wirt's Leggings\" & ench: = minecraft:protection ; lvl:4s",
            })
    public String[] equipment = new String[]{};

    @Config.Name("2. Equipment Sets")
    @Config.LangKey(SetBonus.MODID + ".config.sets")
    @Config.Comment(
            {
                    "Each equipment set is defined here",
                    "",
                    "Each custom id must be defined in the Equipment list first",
                    "The layout of each set is...",
                    "Set id, set name, slot | slot | slot... = custom id | custom id | custom id..., slot... = custom id...",
                    "Vanilla slots: mainhand, offhand, head, chest, legs, feet, hotbar, inventory",
                    "Baubles slots: bauble_amulet, bauble_ring, bauble_belt, bauble_head, bauble_body, bauble_charm, bauble_trinket",
                    "You can also refer to slots by their slot number, eg. the top-left slot of the inventory is 9",
                    "",
                    "eg...",
                    "",
                    "SnD, Sword and Dagger, mainhand | offhand = WSword | SSword | ISword | GSword | DSword, mainhand | offhand = WDagger | SDagger | IDagger | GDagger | DDagger",
                    "",
                    "DArmor, Diamond Armor, head = DHelm, chest = DChest, legs = DLegs, feet = DBoots"
            })
    public String[] sets = new String[]{};

    @Config.Name("2f. Bonuses")
    @Config.LangKey(SetBonus.MODID + ".config.bonuses")
    @Config.Comment(
            {
                    "Each 'bonus' has a bonus id, a name, a discovery setting, and a set of requirements that need to be meet to activate it",
                    "",
                    "The bonus id is only used in other config settings, and the name is what appears in tooltips or other displays",
                    "",
                    "The discovery setting is 0, 1, or 2:",
                    "0 means 'Discoverable'; the tooltip will not show for a player until they've activated the bonus at least once",
                    "1 means 'Identifiable'; the tooltip will show, but be obfuscated for a player until they've activated the bonus at least once",
                    "2 means 'Globally Known; the tooltip will always show",
                    "",
                    "Lastly, any number of requirements can be defined which must be met for the bonus to activate, separated by commas.  This can include any combination of...",
                    "...equipment sets; just put in the set id.  For a partial set, add an equals sign to it with the number of set items required after the equals sign",
                    "...attribute totals; put in the attribute name followed by a sign and a number, eg. generic.armor < 5, generic.attackDamage > 10"
            })
    public String[] bonuses = new String[]{};

    @Config.Name("3. Attribute Modifiers")
    @Config.LangKey(SetBonus.MODID + ".config.attributeModifiers")
    @Config.Comment(
            {
                    "Attribute modifiers you receive as bonuses",
                    "",
                    "Layout is...",
                    "Set id, number of set items required (or the keyword 'all'), attribute = amount @ operation, attribute = amount @ operation",
                    "",
                    "eg...",
                    "",
                    "This should make the Sword and Dagger set give a 50% attack damage bonus",
                    "SnD, all, generic.attackDamage = 0.5 @ 1",
            })
    public String[] attributeMods = new String[]{};

    @Config.Name("4. Potion Effects")
    @Config.LangKey(SetBonus.MODID + ".config.potionEffects")
    @Config.Comment(
            {
                    "Constant potion effects received as a bonus",
                    "",
                    "Layout is...",
                    "Set id, number of set items required (or the keyword 'all'), potion.level, potion.level...",
                    "",
                    "eg...",
                    "",
                    "SnD, all, haste.2",
                    "",
                    "DArmor, 2, resistance",
                    "",
                    "DArmor, all, resistance.2"
            })
    public String[] potions = new String[]{};
}
