package com.fantasticsource.setbonus.common;

import com.fantasticsource.setbonus.SetBonus;
import net.minecraft.client.resources.I18n;

import java.util.Arrays;

public class Set
{
    public String parsedString, id, name;
    public SetData data;

    private Set()
    {
    }

    public static Set getInstance(String parsableSet)
    {
        Set result = new Set();

        String[] tokens = parsableSet.split(",");
        if (tokens.length < 3)
        {
            System.err.println(I18n.format(SetBonus.MODID + ".error.notEnoughSetArgs", parsableSet));
            return null;
        }

        result.id = tokens[0].trim();
        if (result.id.equals(""))
        {
            System.err.println(I18n.format(SetBonus.MODID + ".error.noSetID", parsableSet));
            return null;
        }

        result.name = tokens[1].trim();

        result.data = SetData.getInstance(result.name, Arrays.copyOfRange(tokens, 2, tokens.length));
        if (result.data == null) return null;

        result.parsedString = parsableSet;
        return result;
    }
}
