package com.fantasticsource.setbonus.common;

import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.client.ClientBonus;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import com.fantasticsource.setbonus.server.ServerBonus;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Bonus
{
    public static int
            MODE_DISCOVERABLE = 0,
            MODE_GLOBALLY_KNOWN = 1,
            MODE_GLOBALLY_HIDDEN = 2;


    public String parsedString, id, name;
    public int discoveryMode;


    public ArrayList<ABonusRequirement> bonusRequirements = new ArrayList<>();
    public ArrayList<ABonusElement> bonusElements = new ArrayList<>();


    protected Bonus()
    {
    }

    public static Bonus getInstance(String parsableBonus, Side side)
    {
        Bonus bonus = side == Side.SERVER ? new ServerBonus() : new ClientBonus();

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
            ABonusRequirement requirement = ABonusRequirement.parse(requirementString, side);

            if (requirement == null)
            {
                System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.unknownBonusReq", parsableBonus));
                return null;
            }

            bonus.bonusRequirements.add(requirement);
        }

        bonus.parsedString = parsableBonus;
        return bonus;
    }
}
