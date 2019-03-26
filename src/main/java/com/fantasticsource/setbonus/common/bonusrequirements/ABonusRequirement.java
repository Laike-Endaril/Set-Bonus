package com.fantasticsource.setbonus.common.bonusrequirements;

import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SetRequirement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;

public abstract class ABonusRequirement
{
    protected ABonusRequirement()
    {
    }

    public static ABonusRequirement getInstance(String parseableAttributeRequirement) throws Exception
    {
        throw new IllegalAccessException("This method should not be called directly!  Please call the matching method of a subclass instead!");
    }

    public static ABonusRequirement parse(String parseableBonusRequirement)
    {
        ABonusRequirement result;

        try
        {
            result = SetRequirement.getInstance(parseableBonusRequirement);
            if (result != null) return result;
        }
        catch (Exception e)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.malformedSetReq", parseableBonusRequirement));
            return null;
        }

        try
        {
            result = AttributeRequirement.getInstance(parseableBonusRequirement);
            if (result != null) return result;
        }
        catch (Exception e)
        {
            //NOTE: THIS MUST ALWAYS BE THE LAST CHECK, BECAUSE THERE IS NO ATTRIBUTE REGISTRY!
        }

        System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.unknownBonusReq", parseableBonusRequirement));
        return null;
    }

    public abstract int active(EntityPlayer player);

    public abstract int required();
}
