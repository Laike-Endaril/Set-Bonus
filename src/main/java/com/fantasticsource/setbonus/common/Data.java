package com.fantasticsource.setbonus.common;

import com.fantasticsource.mctools.attributes.AttributeMods;
import com.fantasticsource.mctools.items.ItemFilter;
import com.fantasticsource.mctools.potions.Potions;
import com.fantasticsource.setbonus.config.SyncedConfig;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;

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
        //Clear any existing data
        Bonus.dropAll();
        equipment = new LinkedHashMap<>();
        sets = new LinkedHashMap<>();
        players.clear();


        //Initialize equipment
        for (String string : SyncedConfig.equipment)
        {
            String[] tokens = string.split(",");
            if (tokens.length != 2)
            {
                System.err.println("Wrong number of arguments for equipment: " + string + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
                continue;
            }

            String id = tokens[0].trim();
            if (id.equals(""))
            {
                System.err.println("No equipment id specified for equipment: " + string + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
                continue;
            }

            ItemFilter filter = ItemFilter.getInstance(tokens[1]);
            if (filter != null) equipment.put(id, filter);
        }


        //Initialize sets
        for (String string : SyncedConfig.sets)
        {
            String[] tokens = string.split(",");
            if (tokens.length < 3)
            {
                System.err.println("Not enough arguments for set: " + string + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
                continue;
            }

            String id = tokens[0].trim();
            if (id.equals(""))
            {
                System.err.println("No set id specified for set: " + string + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
                continue;
            }

            String name = tokens[1].trim();

            SetData data = SetData.getInstance(name, Arrays.copyOfRange(tokens, 2, tokens.length));
            if (data != null) sets.put(id, data);
        }


        //Initialize bonuses
        for (String string : SyncedConfig.bonuses)
        {
            String[] tokens = string.split(",");
            if (tokens.length < 3)
            {
                System.err.println("Not enough arguments for bonus: " + string + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
                continue;
            }

            Bonus bonus = new Bonus();
            bonus.name = tokens[1].trim();
            try
            {
                bonus.mode = Integer.parseInt(tokens[2].trim());
            }
            catch (NumberFormatException e)
            {
                System.err.println("Third argument must be a number: " + string + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
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
                        int num = Integer.parseInt(tokens2[1].trim());
                        if (num > 0) bonus.setRequirements.put(set, num);
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
                System.err.println("Unrecognized bonus requirement: " + string + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
                success = false;
                break;
            }

            if (success) Bonus.bonusMap.put(tokens[0].trim(), bonus);
        }


        //Initialize attribute modifiers
        for (String string : SyncedConfig.attributeMods)
        {
            String[] tokens = string.split(",");
            if (tokens.length < 2)
            {
                System.err.println("Not enough arguments for attribute bonus: " + string + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
                continue;
            }

            Bonus bonus = Bonus.bonusMap.get(tokens[0].trim());
            if (bonus == null)
            {
                System.err.println("Bonus ID not found (" + tokens[0].trim() + ") for potion bonus: " + string + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
                continue;
            }

            for (AttributeModifier modifier : AttributeMods.parseMods(Arrays.copyOfRange(tokens, 1, tokens.length)))
            {
                bonus.modifiers.put(modifier.getName(), modifier.setSaved(false));
            }
        }


        //Initialize potions
        for (String string : SyncedConfig.potions)
        {
            String[] tokens = string.split(",");
            if (tokens.length < 2)
            {
                System.err.println("Not enough arguments for potion bonus: " + string + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
                continue;
            }

            Bonus bonus = Bonus.bonusMap.get(tokens[0].trim());
            if (bonus == null)
            {
                System.err.println("Bonus ID not found (" + tokens[0].trim() + ") for potion bonus: " + string + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
                continue;
            }

            bonus.potions.addAll(Potions.parsePotions(Arrays.copyOfRange(tokens, 1, tokens.length), true));
        }
    }
}
