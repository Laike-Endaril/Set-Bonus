package com.fantasticsource.setbonus.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;

public class BonusData
{
    ArrayList<PotionEffect> potions = new ArrayList<>();

    private BonusData()
    {
    }


    public static BonusData getInstance(ArrayList<PotionEffect> potions)
    {
        BonusData result = new BonusData();

        result.potions = (ArrayList<PotionEffect>) potions.clone();

        return result;
    }

    public void activate(EntityPlayer player)
    {

    }

    public void deactivate(EntityPlayer player)
    {

    }
}
