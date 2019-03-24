package com.fantasticsource.setbonus.common;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Bonus
{
    public static int
            MODE_DISCOVERABLE = 0,
            MODE_IDENTIFIABLE = 1,
            MODE_GLOBALLY_KNOWN = 2;
    public static LinkedHashMap<String, Bonus> bonusMap = new LinkedHashMap<>();
    public String name;
    public int mode;
    public LinkedHashMap<SetData, Integer> setRequirements = new LinkedHashMap<>();
    public LinkedHashMap<String, DoubleRequirement> attributeRequirements = new LinkedHashMap<>();
    public Multimap<String, AttributeModifier> modifiers = ArrayListMultimap.create();
    public ArrayList<PotionEffect> potions = new ArrayList<>();

    public LinkedHashMap<EntityPlayer, BonusData> bonusData = new LinkedHashMap<>();


    public static void dropAll()
    {
        //Needs to be done right before new configs are applied, to remove any eg. potion effects (because they might not be part of the bonus anymore)
        for (Bonus bonus : bonusMap.values())
        {
            for (BonusData data : bonus.bonusData.values()) data.update(false);
        }
        bonusMap.clear();
    }

    public static void deactivateBonuses(EntityPlayer player)
    {
        for (Bonus bonus : bonusMap.values())
        {
            BonusData data = bonus.bonusData.get(player);
            if (data != null) data.update(false);
        }
    }

    public static void updateBonuses(EntityPlayer player)
    {
        //Happens once per second on player tick event
        for (Bonus bonus : bonusMap.values()) bonus.update(player);
    }


    public void update(EntityPlayer player)
    {
        BonusData data = bonusData.computeIfAbsent(player, k -> new BonusData(player));

        for (Map.Entry<SetData, Integer> entry : setRequirements.entrySet())
        {
            if (entry.getKey().getNumberEquipped(player) < entry.getValue())
            {
                data.update(false);
                return;
            }
        }

        for (Map.Entry<String, DoubleRequirement> entry : attributeRequirements.entrySet())
        {
            IAttributeInstance attributeInstance = player.getAttributeMap().getAttributeInstanceByName(entry.getKey());
            if (attributeInstance == null)
            {
                data.update(false);
                return;
            }
            if (!entry.getValue().check(attributeInstance.getAttributeValue()))
            {
                data.update(false);
                return;
            }
        }

        data.update(true);
    }


    public class BonusData
    {
        public boolean active, identified;
        EntityPlayer player;


        public BonusData(EntityPlayer player)
        {
            this.player = player;

            active = true;
            if (attributeRequirements.size() > 0) active = false;
            else
            {
                for (Map.Entry<SetData, Integer> entry : setRequirements.entrySet())
                {
                    boolean check = entry.getValue() <= 0;
                    active &= check;
                }
            }

            identified = active;
        }


        private void update(boolean activate)
        {
            if (activate)
            {
                if (!active)
                {
                    //Activating
                    active = true;
                    identified = true;
                    for (PotionEffect potion : potions) player.addPotionEffect(potion);
                    player.getAttributeMap().applyAttributeModifiers(modifiers);
                }
                else
                {
                    //Remaining active
                    for (PotionEffect potion : potions)
                    {
                        PotionEffect potionEffect = player.getActivePotionEffect(potion.getPotion());
                        if (potionEffect == null) player.addPotionEffect(potion);
                    }
                }
            }
            else
            {
                if (active)
                {
                    //Deactivating
                    active = false;
                    for (PotionEffect potion : potions) player.removePotionEffect(potion.getPotion());
                    player.getAttributeMap().removeAttributeModifiers(modifiers);
                }
            }
        }
    }
}
