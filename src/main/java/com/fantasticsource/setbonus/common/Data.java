package com.fantasticsource.setbonus.common;

import com.fantasticsource.mctools.attributes.AttributeMods;
import com.fantasticsource.mctools.potions.Potions;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.config.SyncedConfig;
import com.fantasticsource.tools.datastructures.Pair;
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
    public static LinkedHashMap<String, BonusData> bonuses = new LinkedHashMap<>();

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
            String[] tokens = bonusString.split(",");
            if (tokens.length < 3)
            {
                System.err.println(I18n.format(SetBonus.MODID + ".error.notEnoughBonusArgs", bonusString));
                continue;
            }

            String id = tokens[0].trim();
            if (id.equals(""))
            {
                System.err.println(I18n.format(SetBonus.MODID + ".error.noBonusID", bonusString));
                continue;
            }

            BonusData bonusData = new BonusData();
            bonusData.name = tokens[1].trim();

            try
            {
                bonusData.discoveryMode = Integer.parseInt(tokens[2].trim());
            }
            catch (NumberFormatException e)
            {
                System.err.println(I18n.format(SetBonus.MODID + ".error.bonusDiscoveryMode", bonusString));
                continue;
            }
            if (bonusData.discoveryMode < 0 || bonusData.discoveryMode > 3)
            {
                System.err.println(I18n.format(SetBonus.MODID + ".error.bonusDiscoveryMode", bonusString));
                continue;
            }

            boolean success = true;
            for (String s : Arrays.copyOfRange(tokens, 3, tokens.length))
            {
                String[] tokens2 = s.split("\\.");
                Set set = sets.get(tokens2[0].trim());
                if (set != null)
                {
                    //It's a set
                    if (tokens2.length == 1)
                    {
                        //Full set
                        bonusData.setRequirements.put(set.data, set.data.getMaxNumber());
                    }
                    else
                    {
                        //Partial set
                        try
                        {
                            int num = Integer.parseInt(tokens2[1].trim());
                            if (num > 0) bonusData.setRequirements.put(set.data, num);
                        }
                        catch (NumberFormatException e)
                        {
                            System.err.println(I18n.format(SetBonus.MODID + ".error.malformedSetReq", bonusString));
                            success = false;
                            break;
                        }
                    }
                    continue;
                }

                //Try for a DoubleRequirement
                Pair<String, DoubleRequirement> pair = DoubleRequirement.parse(s);
                if (pair != null)
                {
                    //It's a DoubleRequirement

                    //For now, attributes are the only type of DoubleRequirement, so use this as one
                    //Attributes don't have a registry, and can't really be checked ahead of time, so any malformed strings here will end up as "valid" attribute checks; beware!
                    bonusData.attributeRequirements.put(pair.getKey(), pair.getValue());

                    continue;
                }

                //Error!
                System.err.println(I18n.format(SetBonus.MODID + ".error.unknownBonusReq", bonusString));
                success = false;
                break;
            }

            if (success) Data.bonuses.put(tokens[0].trim(), bonusData);
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

            BonusData bonusData = Data.bonuses.get(tokens[0].trim());
            if (bonusData == null)
            {
                System.err.println(I18n.format(SetBonus.MODID + ".error.attribBonusIDNotFound", tokens[0].trim(), attribString));
                continue;
            }

            ArrayList<AttributeModifier> modifiers = AttributeMods.parseMods(Arrays.copyOfRange(tokens, 1, tokens.length));
            if (modifiers == null) continue;

            for (AttributeModifier modifier : modifiers)
            {
                bonusData.modifiers.put(modifier.getName(), modifier.setSaved(false));
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

            BonusData bonusData = Data.bonuses.get(tokens[0].trim());
            if (bonusData == null)
            {
                System.err.println(I18n.format(SetBonus.MODID + ".error.potionBonusIDNotFound", tokens[0].trim(), potionString));
                continue;
            }

            ArrayList<PotionEffect> potions = Potions.parsePotions(Arrays.copyOfRange(tokens, 1, tokens.length), true);
            if (potions == null) continue;

            bonusData.potions.addAll(potions);
        }
    }
}
