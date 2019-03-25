package com.fantasticsource.setbonus.common.bonuselements;

import com.fantasticsource.mctools.potions.Potions;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.Data;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;

public class PotionBonus extends ABonusElement
{
    public ArrayList<PotionEffect> potions;

    protected PotionBonus(String parsableBonusElement, Bonus bonus, ArrayList<PotionEffect> potions)
    {
        super(parsableBonusElement, bonus);
        this.potions = potions;
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

        return new PotionBonus(parsablePotionBonus, bonus, potions);
    }

    @Override
    public void activate(EntityPlayer player)
    {
        for (PotionEffect potion : potions) player.addPotionEffect(potion);
    }

    @Override
    public void deactivate(EntityPlayer player)
    {
        for (PotionEffect potion : potions) player.removePotionEffect(potion.getPotion());
    }

    @Override
    public void updateActive(EntityPlayer player)
    {
        for (PotionEffect potion : potions)
        {
            PotionEffect potionEffect = player.getActivePotionEffect(potion.getPotion());
            if (potionEffect == null) player.addPotionEffect(potion);
        }
    }
}
