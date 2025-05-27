package com.fantasticsource.setbonus.common.bonusrequirements.setrequirement;

import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class Set
{
    public String id, name;
    public ArrayList<SlotData> slotData = new ArrayList<>();


    protected LinkedHashMap<EntityPlayer, Pair<Integer, Long>> numEquipped = new LinkedHashMap<>();
    protected ArrayList<Integer> blocked = new ArrayList<>();


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
            SlotData slotData = SlotData.getInstance(string, data);
            if (slotData == null) return null;

            result.slotData.add(slotData);
        }
        if (result.slotData.size() == 0)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.noSetReqs", parsableSet));
            return null;
        }

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
        blocked.clear();
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


    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (!(obj instanceof Set)) return false;
        return ((Set) obj).id.equals(id);
    }


    public Set clone(SetBonusData data)
    {
        Set other = new Set();

        other.id = id;
        other.name = name;

        for (SlotData slotData : slotData) other.slotData.add(slotData.clone(data));

        return other;
    }

    @Override
    public String toString()
    {
        String result = id + ", " + name;
        for (SlotData slotData : slotData) result += ", " + slotData;
        return result;
    }
}
