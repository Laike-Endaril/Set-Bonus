package com.fantasticsource.setbonus.common.bonusrequirements.setrequirement;

import com.fantasticsource.mctools.items.RegistryRegexItemFilter;
import com.fantasticsource.setbonus.SetBonus;
import net.minecraft.util.text.translation.I18n;

public class Equip
{
    public String id;
    public RegistryRegexItemFilter filter;

    private Equip()
    {
    }

    public static Equip getInstance(String parsableEquip)
    {
        Equip result = new Equip();

        String[] tokens = parsableEquip.split(",");
        if (tokens.length != 2)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.equipArgCount", parsableEquip));
            return null;
        }

        result.id = tokens[0].trim();
        if (result.id.equals(""))
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.noEquipID", parsableEquip));
            return null;
        }

        result.filter = RegistryRegexItemFilter.getInstance(tokens[1]);
        if (result.filter == null) return null;

        return result;
    }


    public Equip clone()
    {
        Equip other = new Equip();

        other.id = id;
        other.filter = filter.clone();

        return other;
    }

    @Override
    public String toString()
    {
        return id + ", " + filter;
    }
}
