package com.fantasticsource.setbonus.server;

import com.fantasticsource.mctools.items.ItemFilter;

import java.util.Arrays;
import java.util.LinkedHashMap;

import static com.fantasticsource.setbonus.config.SetBonusConfig.serverSettings;

public class Data
{
    public static LinkedHashMap<String, ItemFilter> equipment = new LinkedHashMap<>();
    public static LinkedHashMap<String, SetData> sets = new LinkedHashMap<>();

    static
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

            SetData data = SetData.getInstance(name, Arrays.copyOfRange(tokens, 2, tokens.length - 1));
            if (data != null) sets.put(id, data);
        }
    }


    public static void init()
    {
        //Indirectly initializes the class
    }
}
