package com.fantasticsource.setbonus.common.bonusrequirements.setrequirement;

import com.fantasticsource.mctools.items.RegistryRegexItemFilter;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class Set
{
    public String parsedString, id, name;
    public LinkedHashMap<String, RegistryRegexItemFilter> involvedEquips = new LinkedHashMap<>();
    public ArrayList<SlotData> slotData = new ArrayList<>();

    private LinkedHashMap<EntityPlayer, Pair<Integer, Long>> numEquipped = new LinkedHashMap<>();

    private Set()
    {
    }

    public static Set getInstance(String parsableSet, SetBonusData data)
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
            SlotData slotData = SlotData.getInstance(string, result.involvedEquips, data);
            if (slotData == null) return null;

            result.slotData.add(slotData);
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
        long tick = player.world.getTotalWorldTime();

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
            int slot = data.equipped(player, blocked, true, true);
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


    public Set clone()
    {
        Set other = new Set();

        other.parsedString = parsedString;
        other.id = id;
        other.name = name;

        for (Map.Entry<String, RegistryRegexItemFilter> entry : involvedEquips.entrySet()) other.involvedEquips.put(entry.getKey(), entry.getValue().clone());

        for (SlotData data : slotData) other.slotData.add(data.clone());

        return other;
    }
}
