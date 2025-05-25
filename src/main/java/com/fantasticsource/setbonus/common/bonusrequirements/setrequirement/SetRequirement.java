package com.fantasticsource.setbonus.common.bonusrequirements.setrequirement;

import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import net.minecraft.entity.player.EntityPlayer;

import java.util.LinkedHashSet;

public class SetRequirement extends ABonusRequirement
{
    public Set set;
    public int num;

    public SetRequirement(Set set, int num)
    {
        super();
        this.set = set;
        this.num = num;
    }

    public static SetRequirement getInstance(String parseableSetRequirement, LinkedHashSet<Set> sets) throws Exception
    {
        String[] tokens2 = parseableSetRequirement.split("\\.");
        Set set = null;
        String id = tokens2[0].trim();
        for (Set set2 : sets)
        {
            if (set2.id.equals(id))
            {
                set = set2;
                break;
            }
        }
        if (set == null) return null;

        //Full set?
        if (tokens2.length == 1) return new SetRequirement(set, -1);

        //Partial set?
        int num = Integer.parseInt(tokens2[1].trim());
        if (num > 0) return new SetRequirement(set, num);

        //Neither
        throw new Exception();
    }

    @Override
    public int active(EntityPlayer player)
    {
        return set.getNumberEquipped(player);
    }

    @Override
    public int required()
    {
        return num == -1 ? set.slotData.size() : num;
    }


    public SetRequirement clone(SetBonusData data)
    {
        for (Set set2 : data.sets)
        {
            if (set2.id.equals(set.id)) return new SetRequirement(set2, num);
        }
        throw new IllegalStateException("Failed to clone SetRequirement");
    }

    @Override
    public String toString()
    {
        return set.id + (num == -1 ? "" : "." + num);
    }
}
