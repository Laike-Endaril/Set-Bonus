package com.fantasticsource.setbonus.common;

import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.mctools.items.ItemFilter;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SetData
{
    public LinkedHashMap<String, ItemFilter> involvedEquips = new LinkedHashMap<>();
    public String name;
    private ArrayList<SlotData> slotData = new ArrayList<>();
    private LinkedHashMap<EntityPlayer, Pair<Integer, Long>> numEquipped = new LinkedHashMap<>();


    private SetData()
    {
    }


    public static SetData getInstance(String name, String... equipment)
    {
        SetData result = new SetData();
        result.name = name;

        for (String string : equipment)
        {
            SlotData data = SlotData.getInstance(string, result.involvedEquips);
            if (data != null) result.slotData.add(data);
        }
        if (result.slotData.size() == 0) return null;

        return result;
    }

    public int getNumberEquipped(EntityPlayer player)
    {
        int result;
        long tick = ServerTickTimer.currentTick();

        Pair<Integer, Long> pair = numEquipped.computeIfAbsent(player, k -> new Pair<>(0, tick - 1));
        if (pair.getValue() != tick)
        {
            result = getNumberEquippedInternal(player);
            pair.set(result, tick);
            return result;
        }

        return pair.getKey();
    }

    private int getNumberEquippedInternal(EntityPlayer player)
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
}
