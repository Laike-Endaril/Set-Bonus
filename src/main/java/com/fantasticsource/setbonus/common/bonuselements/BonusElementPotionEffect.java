package com.fantasticsource.setbonus.common.bonuselements;

import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.mctools.potions.FantasticPotionEffect;
import com.fantasticsource.mctools.potions.Potions;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.Network;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.Arrays;

public class BonusElementPotionEffect extends ABonusElement
{
    public ArrayList<FantasticPotionEffect> potions;

    protected BonusElementPotionEffect(Bonus bonus, ArrayList<FantasticPotionEffect> potions)
    {
        super(bonus);
        this.potions = potions;
    }

    public static BonusElementPotionEffect getInstance(String parsablePotionBonus, SetBonusData data)
    {
        String[] tokens = parsablePotionBonus.split(",");
        if (tokens.length < 2)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.notEnoughPotionBonusArgs", parsablePotionBonus));
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
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.potionBonusIDNotFound", tokens[0].trim(), parsablePotionBonus));
            return null;
        }

        //Error messages handled in library
        ArrayList<FantasticPotionEffect> potions = Potions.parsePotions(Arrays.copyOfRange(tokens, 1, tokens.length), true);
        if (potions.size() == 0) return null;

        return new BonusElementPotionEffect(bonus, potions);
    }

    @Override
    public void activate(EntityPlayer player)
    {
        long tick = ServerTickTimer.currentTick();

        for (FantasticPotionEffect potion : potions)
        {
            if (potion.interval == 0 || tick % potion.interval == 0)
            {
                player.addPotionEffect(new FantasticPotionEffect(potion));

                if (potion.getDuration() >= FantasticPotionEffect.MAX_DURATION_THRESHOLD)
                {
                    PotionEffect active = player.getActivePotionEffect(potion.getPotion());
                    if (active != null && active.getAmplifier() <= potion.getAmplifier()) Network.WRAPPER.sendTo(new Network.PotionFixPacket(potion.getPotion()), (EntityPlayerMP) player);
                }
            }
        }
    }

    @Override
    public void deactivate(EntityPlayer player)
    {
        for (FantasticPotionEffect potion : potions)
        {
            PotionEffect active = player.getActivePotionEffect(potion.getPotion());
            if (active != null && active.getAmplifier() == potion.getAmplifier() && active.getDuration() <= potion.getDuration()) player.removePotionEffect(potion.getPotion());
        }
    }

    @Override
    public void updateActive(EntityPlayer player)
    {
        long tick = ServerTickTimer.currentTick();

        for (FantasticPotionEffect potion : potions)
        {
            if (potion.interval == 0 || tick % potion.interval == 0)
            {
                boolean needFix = player.getActivePotionEffect(potion.getPotion()) != null;
                player.addPotionEffect(new PotionEffect(potion));
                if (needFix) Network.WRAPPER.sendTo(new Network.PotionFixPacket(potion.getPotion()), (EntityPlayerMP) player);
            }

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            {
                PotionEffect active = player.getActivePotionEffect(potion.getPotion());
                if (active != null && active.getDuration() < FantasticPotionEffect.MAX_DURATION_THRESHOLD) active.setPotionDurationMax(false);
            }
        }
    }


    @Override
    public String[] tooltips()
    {
        String[] result = new String[potions.size()];
        int i = 0;
        for (FantasticPotionEffect potion : potions) result[i++] = potion.toString(false);
        return result;
    }


    public BonusElementPotionEffect clone(SetBonusData data)
    {
        return getInstance(toString(), data);
    }

    @Override
    public String toString()
    {
        String result = bonus.id;
        int argCount, amp, duration, interval;
        for (FantasticPotionEffect potionEffect : potions)
        {
            result += ", " + potionEffect.getPotion().getRegistryName();

            amp = potionEffect.getAmplifier();
            duration = potionEffect.getDuration();
            interval = potionEffect.interval;
            argCount = 3;
            if (interval == 0)
            {
                argCount--;
                if (duration == Integer.MAX_VALUE)
                {
                    argCount--;
                    if (amp == 0) argCount--;
                }
            }

            if (argCount == 3) result += "." + amp + "." + duration + "." + interval;
            else if (argCount == 2) result += "." + amp + "." + duration;
            else if (argCount == 1) result += "." + amp;
        }
        return result;
    }
}
