package com.fantasticsource.setbonus.config.server;

import net.minecraftforge.common.config.Config;

public class ServerConfig
{
    @Config.Name("1. Equipment")
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

    @Config.Name("3. Attribute Modifiers")
    @Config.Comment(
            {
                    "Attribute modifiers you receive as set bonuses",
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
    @Config.Comment(
            {
                    "Constant potion effects received as a set bonus",
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
