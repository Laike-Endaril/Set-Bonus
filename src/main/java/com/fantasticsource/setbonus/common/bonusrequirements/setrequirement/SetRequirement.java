package com.fantasticsource.setbonus.common.bonusrequirements.setrequirement;

import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import com.fantasticsource.setbonus.server.ServerData;
import net.minecraft.entity.player.EntityPlayer;

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

    public static SetRequirement getInstance(String parseableSetRequirement) throws Exception
    {
        String[] tokens2 = parseableSetRequirement.split("\\.");
        Set set = ServerData.sets.get(tokens2[0].trim());
        if (set == null) return null;

        //Full set?
        if (tokens2.length == 1) return new SetRequirement(set, set.getMaxNumber());

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
}
