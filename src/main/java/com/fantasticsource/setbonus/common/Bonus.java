package com.fantasticsource.setbonus.common;

import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.client.ClientBonus;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import com.fantasticsource.setbonus.server.ServerBonus;
import net.minecraft.util.text.translation.I18n;

import java.util.ArrayList;
import java.util.Arrays;

public class Bonus
{
    public static final int
            MODE_DISCOVERABLE = 0,
            MODE_GLOBALLY_KNOWN = 1,
            MODE_GLOBALLY_HIDDEN = 2;


    public String id, name;
    public int discoveryMode;

    public ArrayList<ABonusRequirement> requirements = new ArrayList<>();
    public ArrayList<ABonusElement> bonusElements = new ArrayList<>();


    protected Bonus()
    {
    }

    public static Bonus getInstance(String parsableBonus, SetBonusData data)
    {
        Bonus bonus = data == SetBonusData.SERVER_DATA ? new ServerBonus() : data == SetBonusData.CLIENT_DATA ? new ClientBonus() : new Bonus();

        String[] tokens = parsableBonus.split(",");
        if (tokens.length < 3)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.notEnoughBonusArgs", parsableBonus));
            return null;
        }

        bonus.id = tokens[0].trim();
        if (bonus.id.equals(""))
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.noBonusID", parsableBonus));
            return null;
        }

        bonus.name = tokens[1].trim();

        try
        {
            bonus.discoveryMode = Integer.parseInt(tokens[2].trim());
        }
        catch (NumberFormatException e)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.bonusDiscoveryMode", parsableBonus));
            return null;
        }
        if (bonus.discoveryMode < 0 || bonus.discoveryMode > 2)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.bonusDiscoveryMode", parsableBonus));
            return null;
        }

        for (String requirementString : Arrays.copyOfRange(tokens, 3, tokens.length))
        {
            ABonusRequirement requirement = ABonusRequirement.parse(requirementString, data.sets);

            if (requirement == null)
            {
                System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.unknownBonusReq", parsableBonus));
                return null;
            }

            bonus.requirements.add(requirement);
        }

        return bonus;
    }


    public Bonus clone(SetBonusData data)
    {
        Bonus other = new Bonus();

        other.id = id;
        other.name = name;

        other.discoveryMode = discoveryMode;


        //Need to do this here or it will throw getInstance() errors from other classes
        data.bonuses.add(other);


        for (ABonusRequirement requirement : requirements) other.requirements.add(requirement.clone(data));

        for (ABonusElement element : bonusElements) element.clone(data);

        return other;
    }

    @Override
    public String toString()
    {
        String result = id + ", " + name + ", " + discoveryMode;

        for (ABonusRequirement requirement : requirements) result += ", " + requirement;

        return result;
    }
}
