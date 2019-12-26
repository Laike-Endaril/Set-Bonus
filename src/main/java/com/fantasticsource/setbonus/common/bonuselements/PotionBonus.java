package com.fantasticsource.setbonus.common.bonuselements;

import com.fantasticsource.mctools.potions.Potions;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.client.ClientData;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.server.ServerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;

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

    public static PotionBonus getInstance(String parsablePotionBonus, Side side)
    {
        String[] tokens = parsablePotionBonus.split(",");
        if (tokens.length < 2)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.notEnoughPotionBonusArgs", parsablePotionBonus));
            return null;
        }

        Bonus bonus = side == Side.SERVER ? ServerData.bonuses.get(tokens[0].trim()) : ClientData.bonuses.get(tokens[0].trim());
        if (bonus == null)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.potionBonusIDNotFound", tokens[0].trim(), parsablePotionBonus));
            return null;
        }

        ArrayList<PotionEffect> potions = Potions.parsePotions(Arrays.copyOfRange(tokens, 1, tokens.length), true);
        if (potions.size() == 0) return null;

        return new PotionBonus(parsablePotionBonus, bonus, potions);
    }

    @Override
    public void activate(EntityPlayer player)
    {
        for (PotionEffect potion : potions) player.addPotionEffect(new PotionEffect(potion));
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
            if (potionEffect == null || potionEffect.getAmplifier() < potion.getAmplifier()) player.addPotionEffect(new PotionEffect(potion));
        }
    }
}
