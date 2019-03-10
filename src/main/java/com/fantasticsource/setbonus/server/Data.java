package com.fantasticsource.setbonus.server;

import com.fantasticsource.mctools.items.ItemFilter;

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
                System.err.println("No name specified for equipment: " + string + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
                continue;
            }

            ItemFilter filter = ItemFilter.getInstance(tokens[1]);
            if (filter != null) equipment.put(id, filter);
        }


        //Initialize sets
        for (String string : serverSettings.sets)
        {
            
        }
    }


    public static void init()
    {
        //Indirectly initializes the class
    }
}
