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
        player.getAttributeMap().applyAttributeModifiers(modifiers);
    }

    public void deactivate(EntityPlayer player)
    {
        for (PotionEffect potion : potions) player.removePotionEffect(potion.getPotion());
        player.getAttributeMap().removeAttributeModifiers(modifiers);
    }
}
