package com.fantasticsource.setbonus.common;

import com.fantasticsource.mctools.attributes.AttributeMods;
import com.fantasticsource.setbonus.SetBonus;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.Arrays;

public class ModifierBonus
{
    public String parsedString;
    public Bonus bonus;

    private ModifierBonus()
    {
    }

    public static ModifierBonus getInstance(String parsableModifierBonus)
    {
        ModifierBonus result = new ModifierBonus();

        String[] tokens = parsableModifierBonus.split(",");
        if (tokens.length < 2)
        {
            System.err.println(I18n.format(SetBonus.MODID + ".error.notEnoughAttribBonusArgs", parsableModifierBonus));
            return null;
        }

        result.bonus = Data.bonuses.get(tokens[0].trim());
        if (result.bonus == null)
        {
            System.err.println(I18n.format(SetBonus.MODID + ".error.attribBonusIDNotFound", tokens[0].trim(), parsableModifierBonus));
            return null;
        }

        ArrayList<AttributeModifier> modifiers = AttributeMods.parseMods(Arrays.copyOfRange(tokens, 1, tokens.length));
        if (modifiers == null) return null;

        for (AttributeModifier modifier : modifiers)
        {
            result.bonus.data.modifiers.put(modifier.getName(), modifier.setSaved(false));
        }

        result.parsedString = parsableModifierBonus;
        return result;
    }
}
