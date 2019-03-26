package com.fantasticsource.setbonus.common.bonuselements;

import com.fantasticsource.mctools.attributes.AttributeMods;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.Data;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;

import java.util.ArrayList;
import java.util.Arrays;

public class ModifierBonus extends ABonusElement
{
    private Multimap<String, AttributeModifier> modifiers = ArrayListMultimap.create();

    protected ModifierBonus(String parsableBonusElement, Bonus bonus, ArrayList<AttributeModifier> modifiers)
    {
        super(parsableBonusElement, bonus);

        for (AttributeModifier modifier : modifiers)
        {
            this.modifiers.put(modifier.getName(), modifier.setSaved(false));
        }
    }

    public static ModifierBonus getInstance(String parsableModifierBonus)
    {
        String[] tokens = parsableModifierBonus.split(",");
        if (tokens.length < 2)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.notEnoughAttribBonusArgs", parsableModifierBonus));
            return null;
        }

        Bonus bonus = Data.bonuses.get(tokens[0].trim());
        if (bonus == null)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.attribBonusIDNotFound", tokens[0].trim(), parsableModifierBonus));
            return null;
        }

        ArrayList<AttributeModifier> modifiers = AttributeMods.parseMods(Arrays.copyOfRange(tokens, 1, tokens.length));
        if (modifiers == null) return null;

        return new ModifierBonus(parsableModifierBonus, bonus, modifiers);
    }

    @Override
    public void activate(EntityPlayer player)
    {
        player.getAttributeMap().applyAttributeModifiers(modifiers);
    }

    @Override
    public void deactivate(EntityPlayer player)
    {
        player.getAttributeMap().removeAttributeModifiers(modifiers);
    }

    @Override
    public void updateActive(EntityPlayer player)
    {
    }
}
