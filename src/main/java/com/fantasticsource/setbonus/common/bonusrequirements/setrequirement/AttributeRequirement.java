package com.fantasticsource.setbonus.common.bonusrequirements.setrequirement;

import com.fantasticsource.mctools.DoubleRequirement;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.player.EntityPlayer;

public class AttributeRequirement extends ABonusRequirement
{
    public String attributeName;
    public DoubleRequirement requirement;

    protected AttributeRequirement(Pair<String, DoubleRequirement> pair)
    {
        attributeName = pair.getKey();
        requirement = pair.getValue();
    }

    public static AttributeRequirement getInstance(String parseableAttributeRequirement) throws Exception
    {
        Pair<String, DoubleRequirement> pair = DoubleRequirement.parse(parseableAttributeRequirement);
        if (pair != null) return new AttributeRequirement(pair);

        throw new Exception();
    }

    @Override
    public int active(EntityPlayer player)
    {
        try
        {
            return requirement.check(player.getAttributeMap().getAttributeInstanceByName(attributeName).getAttributeValue()) ? 1 : 0;
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    @Override
    public int required()
    {
        return 1;
    }
}
