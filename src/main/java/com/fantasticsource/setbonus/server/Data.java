package com.fantasticsource.setbonus.server;

import com.fantasticsource.mctools.items.ItemFilter;
import com.fantasticsource.mctools.potions.Potions;

import java.util.Arrays;
import java.util.LinkedHashMap;

import static com.fantasticsource.setbonus.config.SetBonusConfig.serverSettings;

public class Data
{
    public static LinkedHashMap<String, ItemFilter> equipment = new LinkedHashMap<>();
    public static LinkedHashMap<String, SetData> sets = new LinkedHashMap<>();

    public static void init()
    {
        //Initialize equipment
        for (String string : serverSettings.equipment)
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
        for (String string : serverSettings.sets)
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
        for (String string : serverSettings.attributeMods)
        {
            //TODO
        }


        //Initialize potions
        for (String string : serverSettings.potions)
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
                System.err.println("Set ID not found (" + tokens[0].trim() + ") for set bonus: " + string + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
                continue;
            }

            int numRequired;
            if (tokens[1].trim().toLowerCase().equals("all")) numRequired = set.getMaxNumber();
            else numRequired = Integer.parseInt(tokens[1].trim());

            if (numRequired < 1)
            {
                System.err.println("Invalid number of set items required (" + tokens[1].trim() + ") for set bonus: " + string + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
                continue;
            }

            BonusData data = set.bonuses.get(numRequired);
            if (data == null)
            {
                data = new BonusData();
                set.bonuses.put(numRequired, data);
            }
            data.potions = Potions.parsePotions(Arrays.copyOfRange(tokens, 2, tokens.length), true);
        }
    }
}
