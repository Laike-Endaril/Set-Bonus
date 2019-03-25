package com.fantasticsource.setbonus.common;

import com.fantasticsource.mctools.attributes.AttributeMods;
import com.fantasticsource.mctools.potions.Potions;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.config.SyncedConfig;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class Data
{
    public static LinkedHashMap<String, Equip> equipment = null;
    public static LinkedHashMap<String, Set> sets;
    public static LinkedHashMap<String, Bonus> bonuses = new LinkedHashMap<>();

    public static ArrayList<EntityPlayer> players = new ArrayList<>();


    public static void update()
    {
        //Clear any existing data
        BonusData.dropAll();
        equipment = new LinkedHashMap<>();
        sets = new LinkedHashMap<>();
        players.clear();


        //Initialize equipment
        for (String equipString : SyncedConfig.equipment)
        {
            Equip equip = Equip.getInstance(equipString);
            if (equip != null) equipment.put(equip.id, equip);
        }


        //Initialize sets
        for (String setString : SyncedConfig.sets)
        {
            Set set = Set.getInstance(setString);
            if (set != null) sets.put(set.id, set);
        }


        //Initialize bonuses
        for (String bonusString : SyncedConfig.bonuses)
        {
            Bonus bonus = Bonus.getInstance(bonusString);
            if (bonus != null) Data.bonuses.put(bonus.id, bonus);
        }


        //Initialize attribute modifiers
        for (String attribString : SyncedConfig.attributeMods)
        {
            String[] tokens = attribString.split(",");
            if (tokens.length < 2)
            {
                System.err.println(I18n.format(SetBonus.MODID + ".error.notEnoughAttribBonusArgs", attribString));
                continue;
            }

            Bonus bonus = Data.bonuses.get(tokens[0].trim());
            if (bonus == null)
            {
                System.err.println(I18n.format(SetBonus.MODID + ".error.attribBonusIDNotFound", tokens[0].trim(), attribString));
                continue;
            }

            ArrayList<AttributeModifier> modifiers = AttributeMods.parseMods(Arrays.copyOfRange(tokens, 1, tokens.length));
            if (modifiers == null) continue;

            for (AttributeModifier modifier : modifiers)
            {
                bonus.data.modifiers.put(modifier.getName(), modifier.setSaved(false));
            }
        }


        //Initialize potions
        for (String potionString : SyncedConfig.potions)
        {
            String[] tokens = potionString.split(",");
            if (tokens.length < 2)
            {
                System.err.println(I18n.format(SetBonus.MODID + ".error.notEnoughPotionBonusArgs", potionString));
                continue;
            }

            Bonus bonus = Data.bonuses.get(tokens[0].trim());
            if (bonus == null)
            {
                System.err.println(I18n.format(SetBonus.MODID + ".error.potionBonusIDNotFound", tokens[0].trim(), potionString));
                continue;
            }

            ArrayList<PotionEffect> potions = Potions.parsePotions(Arrays.copyOfRange(tokens, 1, tokens.length), true);
            if (potions == null) continue;

            bonus.data.potions.addAll(potions);
        }
    }
}
