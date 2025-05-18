package com.fantasticsource.setbonus.common.bonusrequirements.setrequirement;

import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import net.minecraft.entity.player.EntityPlayer;

import java.util.LinkedHashMap;

public class SetRequirement extends ABonusRequirement
{
    public Set set;
    public int num;

    protected SetRequirement(Set set, int num)
    {
        super();
        this.set = set;
        this.num = num;
    }

    public static SetRequirement getInstance(String parseableSetRequirement, LinkedHashMap<String, Set> sets) throws Exception
    {
        String[] tokens2 = parseableSetRequirement.split("\\.");
        Set set = sets.get(tokens2[0].trim());
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
        return num;
    }


    public SetRequirement clone(SetBonusData data)
    {
        return new SetRequirement(data.sets.get(set.id), num);
    }

    @Override
    public String toString()
    {
        return set.id + (num == -1 ? "" : "." + num);
    }
}
