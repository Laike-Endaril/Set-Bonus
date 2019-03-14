package com.fantasticsource.setbonus.common;

import com.fantasticsource.mctools.attributes.AttributeMods;
import com.fantasticsource.mctools.items.ItemFilter;
import com.fantasticsource.mctools.potions.Potions;
import com.fantasticsource.setbonus.config.SyncedConfig;
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
        if (equipment != null)
        {
            //Not the first time we've loaded; release all bonuses before we re-initialize
            for (SetData data : sets.values()) data.dropAll();
        }

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


        //Initialize attribute modifiers
        for (String string : SyncedConfig.attributeMods)
        {
            String[] tokens = string.split(",");
            if (tokens.length < 3)
            {
                System.err.println("Not enough arguments for attribute bonus: " + string + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
                continue;
            }

            SetData set = sets.get(tokens[0].trim());
            if (set == null)
            {
                System.err.println("Set ID not found (" + tokens[0].trim() + ") for attribute bonus: " + string + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
                continue;
            }

            int numRequired;
            if (tokens[1].trim().toLowerCase().equals("all")) numRequired = set.getMaxNumber();
            else numRequired = Integer.parseInt(tokens[1].trim());

            if (numRequired < 1)
            {
                System.err.println("Invalid number of set items required (" + tokens[1].trim() + ") for attribute bonus: " + string + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
                continue;
            }

            BonusData data = set.bonuses.get(numRequired);
            if (data == null)
            {
                data = new BonusData();
                set.bonuses.put(numRequired, data);
            }

            for (AttributeModifier modifier : AttributeMods.parseMods(Arrays.copyOfRange(tokens, 2, tokens.length)))
            {
                data.modifiers.put(modifier.getName(), modifier.setSaved(false));
            }
        }


        //Initialize potions
        for (String string : SyncedConfig.potions)
        {
            String[] tokens = string.split(",");
            if (tokens.length < 3)
            {
                System.err.println("Not enough arguments for potion bonus: " + string + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
                continue;
            }

            SetData set = sets.get(tokens[0].trim());
            if (set == null)
            {
                System.err.println("Set ID not found (" + tokens[0].trim() + ") for potion bonus: " + string + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
                continue;
            }

            int numRequired;
            if (tokens[1].trim().toLowerCase().equals("all")) numRequired = set.getMaxNumber();
            else numRequired = Integer.parseInt(tokens[1].trim());

            if (numRequired < 1)
            {
                System.err.println("Invalid number of set items required (" + tokens[1].trim() + ") for potion bonus: " + string + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
                continue;
            }

            BonusData data = set.bonuses.get(numRequired);
            if (data == null)
            {
                data = new BonusData();
                set.bonuses.put(numRequired, data);
            }

            data.potions.addAll(Potions.parsePotions(Arrays.copyOfRange(tokens, 2, tokens.length), true));
        }
    }
}
