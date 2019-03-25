package com.fantasticsource.setbonus.common;

import com.fantasticsource.mctools.attributes.AttributeMods;
import com.fantasticsource.mctools.items.ItemFilter;
import com.fantasticsource.mctools.potions.Potions;
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
    public static LinkedHashMap<String, ItemFilter> equipment = null;
    public static LinkedHashMap<String, SetData> sets;

    public static ArrayList<EntityPlayer> players = new ArrayList<>();


    public static void update()
    {
        System.out.println("=========================================================================================");
        //Clear any existing data
        Bonus.dropAll();
        equipment = new LinkedHashMap<>();
        sets = new LinkedHashMap<>();
        players.clear();


        //Initialize equipment
        for (String equipString : SyncedConfig.equipment)
        {
            String[] tokens = equipString.split(",");
            if (tokens.length != 2)
            {
                System.err.println(I18n.format(SetBonus.MODID + ".error.equipArgCount", equipString));
                continue;
            }

            String id = tokens[0].trim();
            if (id.equals(""))
            {
                System.err.println(I18n.format(SetBonus.MODID + ".error.noEquipID", equipString));
                continue;
            }

            ItemFilter filter = ItemFilter.getInstance(tokens[1]);
            if (filter != null) equipment.put(id, filter);
        }


        //Initialize sets
        for (String setString : SyncedConfig.sets)
        {
            String[] tokens = setString.split(",");
            if (tokens.length < 3)
            {
                System.err.println(I18n.format(SetBonus.MODID + ".error.notEnoughSetArgs", setString));
                continue;
            }

            String id = tokens[0].trim();
            if (id.equals(""))
            {
                System.err.println(I18n.format(SetBonus.MODID + ".error.noSetID", setString));
                continue;
            }

            String name = tokens[1].trim();

            SetData data = SetData.getInstance(name, Arrays.copyOfRange(tokens, 2, tokens.length));
            if (data != null) sets.put(id, data);
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

            Bonus bonus = new Bonus();
            bonus.name = tokens[1].trim();

            try
            {
                bonus.discoveryMode = Integer.parseInt(tokens[2].trim());
            }
            catch (NumberFormatException e)
            {
                System.err.println(I18n.format(SetBonus.MODID + ".error.bonusDiscoveryMode", bonusString));
                continue;
            }
            if (bonus.discoveryMode < 0 || bonus.discoveryMode > 3)
            {
                System.err.println(I18n.format(SetBonus.MODID + ".error.bonusDiscoveryMode", bonusString));
                continue;
            }

            boolean success = true;
            for (String s : Arrays.copyOfRange(tokens, 3, tokens.length))
            {
                String[] tokens2 = s.split("\\.");
                SetData set = sets.get(tokens2[0].trim());
                if (set != null)
                {
                    //It's a set
                    if (tokens2.length == 1)
                    {
                        //Full set
                        bonus.setRequirements.put(set, set.getMaxNumber());
                    }
                    else
                    {
                        //Partial set
                        try
                        {
                            int num = Integer.parseInt(tokens2[1].trim());
                            if (num > 0) bonus.setRequirements.put(set, num);
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
                    bonus.attributeRequirements.put(pair.getKey(), pair.getValue());

                    continue;
                }

                //Error!
                System.err.println(I18n.format(SetBonus.MODID + ".error.unknownBonusReq", bonusString));
                success = false;
                break;
            }

            if (success) Bonus.bonusMap.put(tokens[0].trim(), bonus);
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

            Bonus bonus = Bonus.bonusMap.get(tokens[0].trim());
            if (bonus == null)
            {
                System.err.println(I18n.format(SetBonus.MODID + ".error.attribBonusIDNotFound", tokens[0].trim(), attribString));
                continue;
            }

            ArrayList<AttributeModifier> modifiers = AttributeMods.parseMods(Arrays.copyOfRange(tokens, 1, tokens.length));
            if (modifiers == null) continue;

            for (AttributeModifier modifier : modifiers)
            {
                bonus.modifiers.put(modifier.getName(), modifier.setSaved(false));
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

            Bonus bonus = Bonus.bonusMap.get(tokens[0].trim());
            if (bonus == null)
            {
                System.err.println(I18n.format(SetBonus.MODID + ".error.potionBonusIDNotFound", tokens[0].trim(), potionString));
                continue;
            }

            ArrayList<PotionEffect> potions = Potions.parsePotions(Arrays.copyOfRange(tokens, 1, tokens.length), true);
            if (potions == null) continue;

            bonus.potions.addAll(potions);
        }
    }
}
