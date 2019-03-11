package com.fantasticsource.setbonus.server;

import com.fantasticsource.mctools.items.ItemFilter;

import java.util.ArrayList;

public class SlotData
{
    public ArrayList<Integer> slots = new ArrayList<>(); //Because multiple slot options can be defined
    public ArrayList<ItemFilter> equipment = new ArrayList<>();


    private SlotData()
    {
    }

    public static SlotData getInstance(String slotsAndEquipment)
    {
        SlotData result = new SlotData();

        String[] tokens = slotsAndEquipment.split("=");
        if (tokens.length != 2)
        {
            System.err.println("Wrong number of arguments for slot/equipment definition: " + slotsAndEquipment + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
            return null;
        }

        for (String slotString : tokens[0].split("[|]"))
        {
            slotString = slotString.trim().toLowerCase();

            if (slotString.equals("mainhand")) result.slots.add(-1); //Mainhand is not a specific slot #
            else if (slotString.equals("hotbar"))
            {
                for (int i = 0; i < 9; i++) result.slots.add(i);
            }
            else if (slotString.equals("inventory"))
            {
                for (int i = 9; i < 36; i++) result.slots.add(i);
            }
            else if (slotString.equals("feet")) result.slots.add(36);
            else if (slotString.equals("legs")) result.slots.add(37);
            else if (slotString.equals("chest")) result.slots.add(38);
            else if (slotString.equals("head")) result.slots.add(39);
            else if (slotString.equals("offhand")) result.slots.add(40);

                //TODO bauble slots here

            else
            {
                try
                {
                    result.slots.add(Integer.parseInt(slotString));
                }
                catch (NumberFormatException e)
                {
                    System.err.println("Unknown slot (" + slotString + ") in slot/equipment definition: " + slotsAndEquipment + "\r\nPlease see the examples by hovering the mouse over the config option in the mod config menu");
                    return null;
                }
            }
        }


        return result;
    }
}
