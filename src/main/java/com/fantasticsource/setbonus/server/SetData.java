package com.fantasticsource.setbonus.server;

import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SetData
{
    private String name;
    private ArrayList<SlotData> slotData = new ArrayList<>();

    private LinkedHashMap<EntityPlayer, Integer> numEquipped = new LinkedHashMap<>();
    private LinkedHashMap<Integer, BonusData> bonuses = new LinkedHashMap<>(); //The int is the number of set items required for the bonus


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
        int result = 0;
        ArrayList<Integer> blocked = new ArrayList<>();
        for (SlotData data : slotData)
        {
            int slot = data.equipped(player, blocked);
            if (slot != Integer.MIN_VALUE)
            {
                blocked.add(slot);
                result++;
            }
        }
        return result;
    }

    public int getMaxNumber()
    {
        return slotData.size();
    }

    public void updateBonuses(EntityPlayer player)
    {
        int currentNum = getNumberEquipped(player);
        int oldNum = 0;
        if (numEquipped.containsKey(player)) oldNum = numEquipped.get(player);

        if (currentNum > oldNum) //Equipped more pieces
        {
            for (Map.Entry<Integer, BonusData> entry : bonuses.entrySet())
            {
                if (currentNum < entry.getKey()) entry.getValue().deactivate(player);
            }
            numEquipped.put(player, currentNum);
        }
        else if (currentNum < oldNum) //Removed some pieces
        {
            for (Map.Entry<Integer, BonusData> entry : bonuses.entrySet())
            {
                if (currentNum >= entry.getKey()) entry.getValue().activate(player);
            }
            numEquipped.put(player, currentNum);
        }
    }
}
