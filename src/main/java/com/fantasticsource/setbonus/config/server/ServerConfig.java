package com.fantasticsource.setbonus.config.server;

import com.fantasticsource.setbonus.common.SetBonus;
import net.minecraftforge.common.config.Config;

public class ServerConfig
{
    @Config.Name("1. Equipment")
    @Config.LangKey(SetBonus.MODID + ".config.equipment")
    @Config.Comment(
            {
                    "FILLSCREEN Each item you want to include in a set needs to be defined here first",
                    "",
                    "Layout is...",
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
                    " ",
            })
    public String[] equipment = new String[]{};

    @Config.Name("2. Equipment Sets")
    @Config.LangKey(SetBonus.MODID + ".config.sets")
    @Config.Comment(
            {
                    "FILLSCREEN Each equipment set is defined here",
                    "Each custom id must be defined in the Equipment list first",
                    "",
                    "Layout is...",
                    "Set id, set name, slot | slot = custom id | custom id, slot | slot = custom id | custom id, etc.",
                    "",
                    "- Vanilla slots: mainhand, offhand, head, chest, legs, feet, hotbar, inventory",
                    "- Baubles slots: bauble_amulet, bauble_ring, bauble_belt, bauble_head, bauble_body, bauble_charm, bauble_trinket",
                    "- You can also refer to slots by their slot number, eg. the top-left slot of the inventory is 9",
                    "",
                    "eg...",
                    "",
                    "SnD, Sword and Dagger, mainhand | offhand = WSword | SSword | ISword | GSword | DSword, mainhand | offhand = WDagger | SDagger | IDagger | GDagger | DDagger",
                    "",
                    "DArmor, Diamond Armor, head = DHelm, chest = DChest, legs = DLegs, feet = DBoots",
                    " ",
            })
    public String[] sets = new String[]{};

    @Config.Name("2f. Bonuses")
    @Config.LangKey(SetBonus.MODID + ".config.bonuses")
    @Config.Comment(
            {
                    "FILLSCREEN Each bonus is defined here",
                    "",
                    "Layout is...",
                    "Bonus id, bonus name, discovery mode, requirement, requirement, requirement, etc.",
                    "",
                    "Discovery mode is a number:",
                    "0 = hidden until the player has activated the bonus at least once",
                    "1 = obfuscated until the player has activated the bonus at least once",
                    "2 = always visible",
                    "",
                    "Each requirement is one of these:",
                    "A set; just put in the set id.  For a partial set, add a period and the number of set items required",
                    "an attribute total; put in the attribute name followed by a sign and a number, eg.",
                    "generic.armor > 7",
                    "",
                    "eg...",
                    "",
                    "DualWieldSnD, Dual Wield, 0, SnD, generic.armor <= 5",
                    " ",
            })
    public String[] bonuses = new String[]{};

    @Config.Name("3. Attribute Modifiers")
    @Config.LangKey(SetBonus.MODID + ".config.attributeModifiers")
    @Config.Comment(
            {
                    "FILLSCREEN Attribute modifiers you receive as part of a bonus",
                    "",
                    "Layout is...",
                    "Bonus id, attribute = amount @ operation, attribute = amount @ operation, etc.",
                    "",
                    "eg...",
                    "",
                    "This makes the Dual Wield bonus from the bonus example give a 50% attack damage bonus",
                    "DualWieldSnD, generic.attackDamage = 0.5 @ 1",
                    " ",
            })
    public String[] attributeMods = new String[]{};

    @Config.Name("4. Potion Effects")
    @Config.LangKey(SetBonus.MODID + ".config.potionEffects")
    @Config.Comment(
            {
                    "FILLSCREEN Constant potion effects received as part of a bonus",
                    "",
                    "Layout is...",
                    "Bonus id, potion.level, potion.level, etc.",
                    "",
                    "eg...",
                    "",
                    "This makes the Dual Wield bonus from the bonus example give haste 2",
                    "DualWieldSnD, haste.2",
                    " ",
            })
    public String[] potions = new String[]{};
}
