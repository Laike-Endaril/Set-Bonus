package com.fantasticsource.setbonus.common;

import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
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
        for (String requirementString : Arrays.copyOfRange(tokens, 3, tokens.length))
        {
            ABonusRequirement requirement = ABonusRequirement.parse(requirementString);

            if (requirement == null)
            {
                System.err.println(I18n.format(SetBonus.MODID + ".error.unknownBonusReq", parsableBonus));
                return null;
            }

            result.data.bonusRequirements.add(requirement);
        }

        result.parsedString = parsableBonus;
        return result;
    }
}
