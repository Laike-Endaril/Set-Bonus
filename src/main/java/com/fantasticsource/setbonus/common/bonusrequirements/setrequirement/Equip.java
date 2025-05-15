package com.fantasticsource.setbonus.common.bonusrequirements.setrequirement;

import com.fantasticsource.mctools.items.RegistryRegexItemFilter;
import com.fantasticsource.setbonus.SetBonus;
import net.minecraft.util.text.translation.I18n;

public class Equip
{
    public String parsedString, name;
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

        result.name = tokens[0].trim();
        if (result.name.equals(""))
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.noEquipID", parsableEquip));
            return null;
        }

        result.filter = RegistryRegexItemFilter.getInstance(tokens[1]);
        if (result.filter == null) return null;

        result.parsedString = parsableEquip;
        return result;
    }


    public void delete()
    {
        System.out.println("Delete equip: " + name);
        //TODO backup config file?
        //TODO remove from config file
        //TODO reload config file
    }
}
