package com.fantasticsource.setbonus.server;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;

public class BonusData
{
    Multimap<String, AttributeModifier> modifiers = ArrayListMultimap.create();
    ArrayList<PotionEffect> potions = new ArrayList<>();

    public void activate(EntityPlayer player)
    {
        for (PotionEffect potion : potions) player.addPotionEffect(potion);
    }

    public void deactivate(EntityPlayer player)
    {
        for (PotionEffect potion : potions) player.removePotionEffect(potion.getPotion());
    }

    public void tickModifiers(EntityPlayer player, boolean active)
    {
        if (!active) player.getAttributeMap().removeAttributeModifiers(modifiers);
        if (active)
        {
            player.getAttributeMap().applyAttributeModifiers(modifiers);
        }
    }
}
