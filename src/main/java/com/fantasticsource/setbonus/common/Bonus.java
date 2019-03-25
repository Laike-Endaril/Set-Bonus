package com.fantasticsource.setbonus.common;

import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.common.bonusrequirements.DoubleRequirement;
import com.fantasticsource.setbonus.common.bonusrequirements.Set;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.client.resources.I18n;

import java.util.Arrays;

public class Bonus
{
    public static int
            MODE_DISCOVERABLE = 0,
            MODE_IDENTIFIABLE = 1,
            MODE_GLOBALLY_KNOWN = 2;


    public String parsedString, id, name;
    public int discoveryMode;
    public BonusData data;

    private Bonus()
    {
    }

    public static Bonus getInstance(String parsableBonus)
    {
        Bonus result = new Bonus();

        String[] tokens = parsableBonus.split(",");
        if (tokens.length < 3)
        {
            System.err.println(I18n.format(SetBonus.MODID + ".error.notEnoughBonusArgs", parsableBonus));
            return null;
        }

        result.id = tokens[0].trim();
        if (result.id.equals(""))
        {
            System.err.println(I18n.format(SetBonus.MODID + ".error.noBonusID", parsableBonus));
            return null;
        }

        result.name = tokens[1].trim();

        try
        {
            result.discoveryMode = Integer.parseInt(tokens[2].trim());
        }
        catch (NumberFormatException e)
        {
            System.err.println(I18n.format(SetBonus.MODID + ".error.bonusDiscoveryMode", parsableBonus));
            return null;
        }
        if (result.discoveryMode < 0 || result.discoveryMode > 3)
        {
            System.err.println(I18n.format(SetBonus.MODID + ".error.bonusDiscoveryMode", parsableBonus));
            return null;
        }

        result.data = new BonusData();
        for (String s : Arrays.copyOfRange(tokens, 3, tokens.length))
        {
            String[] tokens2 = s.split("\\.");
            Set set = Data.sets.get(tokens2[0].trim());
            if (set != null)
            {
                //It's a set
                if (tokens2.length == 1)
                {
                    //Full set
                    result.data.setRequirements.put(set.data, set.data.getMaxNumber());
                }
                else
                {
                    //Partial set
                    try
                    {
                        int num = Integer.parseInt(tokens2[1].trim());
                        if (num > 0) result.data.setRequirements.put(set.data, num);
                    }
                    catch (NumberFormatException e)
                    {
                        System.err.println(I18n.format(SetBonus.MODID + ".error.malformedSetReq", parsableBonus));
                        return null;
                    }
                }
                continue;
            }

            //Try for a DoubleRequirement
            Pair<String, DoubleRequirement> pair = DoubleRequirement.parse(s);
            if (pair != null)
            {
                //It's a DoubleRequirement

                //For now, attributes are the only type of DoubleRequirement, so use this as one
                //Attributes don't have a registry, and can't really be checked ahead of time, so any malformed strings here will end up as "valid" attribute checks; beware!
                result.data.attributeRequirements.put(pair.getKey(), pair.getValue());

                continue;
            }

            //Error!
            System.err.println(I18n.format(SetBonus.MODID + ".error.unknownBonusReq", parsableBonus));
            return null;
        }

        result.parsedString = parsableBonus;
        return result;
    }
}
