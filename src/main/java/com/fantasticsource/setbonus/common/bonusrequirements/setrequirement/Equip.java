package com.fantasticsource.setbonus.common.bonusrequirements.setrequirement;

import com.fantasticsource.mctools.items.AdvancedItemFilter;
import com.fantasticsource.setbonus.SetBonus;
import net.minecraft.util.text.translation.I18n;

public class Equip
{
    public String id;
    public AdvancedItemFilter filter;


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

        result.filter = AdvancedItemFilter.getInstance(tokens[1]);
        if (result.filter == null) return null;

        return result;
    }


    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (!(obj instanceof Equip)) return false;
        return ((Equip) obj).id.equals(id);
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
