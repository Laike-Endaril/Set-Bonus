package com.fantasticsource.setbonus.common.bonuselements;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.attributes.AttributeMods;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.common.Bonus;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class BonusElementAttributeModifier extends ABonusElement
{
    public Multimap<String, AttributeModifier> modifiers = ArrayListMultimap.create();


    public BonusElementAttributeModifier()
    {
    }

    protected BonusElementAttributeModifier(Bonus bonus, ArrayList<AttributeModifier> modifiers)
    {
        super(bonus);

        for (AttributeModifier modifier : modifiers)
        {
            this.modifiers.put(modifier.getName(), modifier.setSaved(false));
        }
    }

    public static BonusElementAttributeModifier getInstance(String parsableModifierBonus, SetBonusData data)
    {
        String[] tokens = parsableModifierBonus.split(",");
        if (tokens.length < 2)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.notEnoughAttribBonusArgs", parsableModifierBonus));
            return null;
        }

        String id = tokens[0].trim();
        Bonus bonus = null;
        for (Bonus bonus2 : data.bonuses)
        {
            if (bonus2.id.equals(id))
            {
                bonus = bonus2;
                break;
            }
        }
        if (bonus == null)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.attribBonusIDNotFound", tokens[0].trim(), parsableModifierBonus));
            return null;
        }

        //Error messages handled in library
        ArrayList<AttributeModifier> modifiers = AttributeMods.parseMods(Arrays.copyOfRange(tokens, 1, tokens.length));
        if (modifiers == null) return null;

        return new BonusElementAttributeModifier(bonus, modifiers);
    }

    @Override
    public void activate(EntityPlayer player)
    {
        float hpRatio = player.getHealth() / player.getMaxHealth();

        player.getAttributeMap().applyAttributeModifiers(modifiers);

        //This might help with edge cases
        if (hpRatio <= Float.MIN_VALUE) hpRatio = 0;
        else if (hpRatio >= 1 - Float.MIN_VALUE) hpRatio = 1;

        player.setHealth(hpRatio * player.getMaxHealth());
    }

    @Override
    public void deactivate(EntityPlayer player)
    {
        float hpRatio = player.getHealth() / player.getMaxHealth();

        player.getAttributeMap().removeAttributeModifiers(modifiers);

        //This might help with edge cases
        if (hpRatio <= Float.MIN_VALUE) hpRatio = 0;
        else if (hpRatio >= 1 - Float.MIN_VALUE) hpRatio = 1;

        player.setHealth(hpRatio * player.getMaxHealth());
    }

    @Override
    public void updateActive(EntityPlayer player)
    {
    }


    @Override
    public String[] tooltips()
    {
        String[] result = new String[modifiers.size()];
        int i = 0;
        for (AttributeModifier modifier : modifiers.values()) result[i++] = MCTools.getAttributeModString(modifier);
        return result;
    }


    public BonusElementAttributeModifier clone()
    {
        BonusElementAttributeModifier other = new BonusElementAttributeModifier();

        AttributeModifier modifier;
        for (Map.Entry<String, AttributeModifier> entry : modifiers.entries())
        {
            modifier = entry.getValue();
            other.modifiers.put(entry.getKey(), new AttributeModifier(modifier.getName(), modifier.getAmount(), modifier.getOperation()));
        }

        return other;
    }

    public BonusElementAttributeModifier clone(SetBonusData data)
    {
        return getInstance(toString(), data);
    }

    @Override
    public String toString()
    {
        String result = bonus.id;

        int mode;
        for (Map.Entry<String, AttributeModifier> entry : modifiers.entries())
        {
            result += ", " + entry.getValue().getName() + " = " + entry.getValue().getAmount();
            mode = entry.getValue().getOperation();
            if (mode != 0) result += " @ " + mode;
        }

        return result;
    }
}
