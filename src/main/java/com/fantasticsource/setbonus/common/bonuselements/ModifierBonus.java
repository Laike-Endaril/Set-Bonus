package com.fantasticsource.setbonus.common.bonuselements;

import com.fantasticsource.mctools.attributes.AttributeMods;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.Data;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.Arrays;

public class ModifierBonus extends BonusElement
{
    protected ModifierBonus(String parsableBonusElement, Bonus bonus)
    {
        super(parsableBonusElement, bonus);
    }

    public static ModifierBonus getInstance(String parsableModifierBonus)
    {
        String[] tokens = parsableModifierBonus.split(",");
        if (tokens.length < 2)
        {
            System.err.println(I18n.format(SetBonus.MODID + ".error.notEnoughAttribBonusArgs", parsableModifierBonus));
            return null;
        }

        Bonus bonus = Data.bonuses.get(tokens[0].trim());
        if (bonus == null)
        {
            System.err.println(I18n.format(SetBonus.MODID + ".error.attribBonusIDNotFound", tokens[0].trim(), parsableModifierBonus));
            return null;
        }

        ArrayList<AttributeModifier> modifiers = AttributeMods.parseMods(Arrays.copyOfRange(tokens, 1, tokens.length));
        if (modifiers == null) return null;

        for (AttributeModifier modifier : modifiers)
        {
            bonus.data.modifiers.put(modifier.getName(), modifier.setSaved(false));
        }

        return new ModifierBonus(parsableModifierBonus, bonus);
    }
}
