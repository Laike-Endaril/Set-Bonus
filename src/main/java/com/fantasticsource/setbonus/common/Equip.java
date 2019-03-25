package com.fantasticsource.setbonus.common;

import com.fantasticsource.mctools.items.ItemFilter;
import com.fantasticsource.setbonus.SetBonus;
import net.minecraft.client.resources.I18n;

public class Equip
{
    String parsedString, id;
    ItemFilter filter;

    private Equip()
    {
    }

    public static Equip getInstance(String parsableEquip)
    {
        Equip result = new Equip();

        String[] tokens = parsableEquip.split(",");
        if (tokens.length != 2)
        {
            System.err.println(I18n.format(SetBonus.MODID + ".error.equipArgCount", parsableEquip));
            return null;
        }

        result.id = tokens[0].trim();
        if (result.id.equals(""))
        {
            System.err.println(I18n.format(SetBonus.MODID + ".error.noEquipID", parsableEquip));
            return null;
        }

        result.filter = ItemFilter.getInstance(tokens[1]);
        if (result.filter == null) return null;

        result.parsedString = parsableEquip;
        return result;
    }
}
