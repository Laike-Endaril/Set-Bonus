package com.fantasticsource.setbonus.server;

import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

public class SetData
{
    private String name;
    private ArrayList<SlotData> slotData = new ArrayList<>();


    private SetData()
    {
    }


    public static SetData getInstance(String name, String... equipment)
    {
        SetData result = new SetData();
        result.name = name;

        for (String string : equipment)
        {
            SlotData data = SlotData.getInstance(string);
            if (data != null) result.slotData.add(data);
        }
        if (result.slotData.size() == 0) return null;

        return result;
    }

    public String getName()
    {
        return name;
    }

    public int getNumberEquipped(EntityPlayer player)
    {
        //TODO
        return 0;
    }

    public int getMaxNumber()
    {
        return slotData.size();
    }
}
