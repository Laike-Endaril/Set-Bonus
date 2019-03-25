package com.fantasticsource.setbonus.common.bonuselements;

import com.fantasticsource.mctools.potions.Potions;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.Data;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;

public class PotionBonus extends BonusElement
{
    protected PotionBonus(String parsableBonusElement, Bonus bonus)
    {
        super(parsableBonusElement, bonus);
    }

    public static PotionBonus getInstance(String parsablePotionBonus)
    {
        String[] tokens = parsablePotionBonus.split(",");
        if (tokens.length < 2)
        {
            System.err.println(I18n.format(SetBonus.MODID + ".error.notEnoughPotionBonusArgs", parsablePotionBonus));
            return null;
        }

        Bonus bonus = Data.bonuses.get(tokens[0].trim());
        if (bonus == null)
        {
            System.err.println(I18n.format(SetBonus.MODID + ".error.potionBonusIDNotFound", tokens[0].trim(), parsablePotionBonus));
            return null;
        }

        ArrayList<PotionEffect> potions = Potions.parsePotions(Arrays.copyOfRange(tokens, 1, tokens.length), true);
        if (potions == null) return null;

        bonus.data.potions.addAll(potions);
        return new PotionBonus(parsablePotionBonus, bonus);
    }
}
