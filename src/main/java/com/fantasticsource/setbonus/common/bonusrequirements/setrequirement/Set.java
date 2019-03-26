package com.fantasticsource.setbonus.common.bonusrequirements.setrequirement;

import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.mctools.items.ItemFilter;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class Set
{
    public String parsedString, id, name;
    public LinkedHashMap<String, ItemFilter> involvedEquips = new LinkedHashMap<>();

    private ArrayList<SlotData> slotData = new ArrayList<>();
    private LinkedHashMap<EntityPlayer, Pair<Integer, Long>> numEquipped = new LinkedHashMap<>();

    private Set()
    {
    }

    public static Set getInstance(String parsableSet)
    {
        Set result = new Set();

        String[] tokens = parsableSet.split(",");
        if (tokens.length < 3)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.notEnoughSetArgs", parsableSet));
            return null;
        }

        result.id = tokens[0].trim();
        if (result.id.equals(""))
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.noSetID", parsableSet));
            return null;
        }

        result.name = tokens[1].trim();

        for (String string : Arrays.copyOfRange(tokens, 2, tokens.length))
        {
            SlotData data = SlotData.getInstance(string, result.involvedEquips);
            if (data == null) return null;

            result.slotData.add(data);
        }
        if (result.slotData.size() == 0)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.noSetReqs", parsableSet));
            return null;
        }

        result.parsedString = parsableSet;
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
