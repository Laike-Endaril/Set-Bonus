package com.fantasticsource.setbonus.server;

import com.fantasticsource.mctools.items.ItemFilter;

import java.util.ArrayList;

public class SlotData
{
    public ArrayList<Integer> slots; //Because some "slots" like "hotbar" are applicable to multiple inventory slots
    public ArrayList<ItemFilter> equipment;


    private SlotData()
    {
    }

    public static SlotData getInstance(String slotsAndEquipment)
    {
        SlotData result = new SlotData();
        return result;
    }
}
