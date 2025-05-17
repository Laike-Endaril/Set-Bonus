package com.fantasticsource.setbonus;

import com.fantasticsource.setbonus.client.ClientBonus;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.Network;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementAttributeModifier;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementEnchantment;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementPotionEffect;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import com.fantasticsource.setbonus.config.SetBonusConfig;
import com.fantasticsource.setbonus.server.ServerBonus;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.setbonus.config.SetBonusConfig.serverSettings;

public class SetBonusData
{
    public static final SetBonusData SERVER_DATA = new SetBonusData(), CLIENT_DATA = new SetBonusData();

    public LinkedHashMap<String, Equip> equipment = new LinkedHashMap<>();
    public LinkedHashMap<String, Set> sets = new LinkedHashMap<>();

    public LinkedHashMap<String, Bonus> bonuses = new LinkedHashMap<>();


    public void clear()
    {
        equipment.clear();
        sets.clear();
        bonuses.clear();
    }


    public static void setServerFromConfig()
    {
        ServerBonus.dropAll();
        SERVER_DATA.setFromConfig();
    }


    public static void setClientFromPacket(Network.AllDiscoveredBonusesPacket packet)
    {
        ClientBonus.dropAll();
        CLIENT_DATA.setFromPacket(packet);
        if (SetBonusConfig.clientSettings.dynamicTooltipSearch > 0) Compat.refreshTooltips();
    }


    public void setFromConfig()
    {
        clear();


        for (String equipString : serverSettings.getEquipment())
        {
            Equip equip = Equip.getInstance(equipString);
            if (equip != null) equipment.put(equip.name, equip);
        }

        for (String setString : serverSettings.getSets())
        {
            Set set = Set.getInstance(setString, this);
            if (set != null) sets.put(set.id, set);
        }

        for (String bonusString : serverSettings.getBonuses())
        {
            ServerBonus bonus = (ServerBonus) Bonus.getInstance(bonusString, this);
            if (bonus != null) bonuses.put(bonus.id, bonus);
        }

        for (String modifierString : serverSettings.getAttributeMods()) BonusElementAttributeModifier.getInstance(modifierString, this);
        for (String potionString : serverSettings.getPotions()) BonusElementPotionEffect.getInstance(potionString, this);
        for (String enchantString : serverSettings.getEnchantments()) BonusElementEnchantment.getInstance(enchantString, this);
    }


    public void setFromPacket(Network.AllDiscoveredBonusesPacket packet)
    {
        clear();


        for (String equipString : packet.equipment)
        {
            Equip equip = Equip.getInstance(equipString);
            if (equip != null) equipment.put(equip.name, equip);
        }

        for (String setString : packet.sets)
        {
            Set set = Set.getInstance(setString, this);
            if (set != null) sets.put(set.id, set);
        }

        for (String bonusString : packet.bonuses)
        {
            ClientBonus bonus = (ClientBonus) Bonus.getInstance(bonusString, this);
            if (bonus != null) bonuses.put(bonus.id, bonus);
        }

        for (String modifierString : packet.attributeMods) BonusElementAttributeModifier.getInstance(modifierString, this);
        for (String potionString : packet.potions) BonusElementPotionEffect.getInstance(potionString, this);
        for (String enchantString : packet.enchantments) BonusElementEnchantment.getInstance(enchantString, this);
    }

    public void addFromPacket(Network.DiscoverBonusPacket packet)
    {
        //Initialize equipment
        for (String equipString : packet.equipment)
        {
            Equip equip = Equip.getInstance(equipString);
            if (equip != null) equipment.put(equip.name, equip);
        }

        //Initialize sets
        for (String setString : packet.sets)
        {
            Set set = Set.getInstance(setString, this);
            if (set != null) sets.put(set.id, set);
        }


        //Initialize bonus
        ClientBonus bonus = (ClientBonus) Bonus.getInstance(packet.bonusString, this);
        if (bonus != null) bonuses.put(bonus.id, bonus);


        //Initialize attribute modifiers
        for (String modifierString : packet.attributeMods)
        {
            BonusElementAttributeModifier.getInstance(modifierString, this);
        }

        //Initialize potions
        for (String potionString : packet.potions)
        {
            BonusElementPotionEffect.getInstance(potionString, this);
        }

        //Initialize enchantments
        for (String enchantString : packet.enchants)
        {
            BonusElementEnchantment.getInstance(enchantString, this);
        }
    }


    public void delete(Equip equip)
    {
        //TODO
    }

    public void delete(Set set)
    {
        //TODO
    }

    public void delete(Bonus bonus)
    {
        //TODO
    }


    public SetBonusData clone()
    {
        SetBonusData other = new SetBonusData();

        for (Map.Entry<String, Equip> entry : equipment.entrySet()) other.equipment.put(entry.getKey(), entry.getValue().clone());
        for (Map.Entry<String, Set> entry : sets.entrySet()) other.sets.put(entry.getKey(), entry.getValue().clone());

        for (Map.Entry<String, Bonus> entry : bonuses.entrySet()) entry.getValue().clone(other);

        return other;
    }
}
