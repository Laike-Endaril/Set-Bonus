package com.fantasticsource.setbonus.client;

import com.fantasticsource.setbonus.Compat;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.Network;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementAttributeModifier;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementEnchantment;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementPotionEffect;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import com.fantasticsource.setbonus.config.SetBonusConfig;

public class ClientData
{
    public static void clear()
    {
        ClientBonus.dropAll();
        SetBonusData.CLIENT_DATA.equipment.clear();
        SetBonusData.CLIENT_DATA.sets.clear();
    }


    public static void update(Network.ConfigPacket packet)
    {
        SetBonusData data = SetBonusData.CLIENT_DATA;


        //Clear any existing data
        ClientBonus.dropAll();
        data.equipment.clear();
        data.sets.clear();


        //Initialize equipment
        for (String equipString : packet.equipment)
        {
            Equip equip = Equip.getInstance(equipString);
            if (equip != null) data.equipment.put(equip.name, equip);
        }


        //Initialize sets
        for (String setString : packet.sets)
        {
            Set set = Set.getInstance(setString, data);
            if (set != null) data.sets.put(set.id, set);
        }


        //Initialize bonuses
        for (String bonusString : packet.bonuses)
        {
            ClientBonus bonus = (ClientBonus) Bonus.getInstance(bonusString, data);
            if (bonus != null) data.bonuses.put(bonus.id, bonus);
        }


        //Initialize attribute modifiers
        for (String modifierString : packet.attributeMods)
        {
            BonusElementAttributeModifier.getInstance(modifierString, data);
        }


        //Initialize potions
        for (String potionString : packet.potions)
        {
            BonusElementPotionEffect.getInstance(potionString, data);
        }
    }


    public static void update(Network.DiscoverBonusPacket packet)
    {
        SetBonusData data = SetBonusData.CLIENT_DATA;


        //Initialize equipment
        for (String equipString : packet.equipment)
        {
            Equip equip = Equip.getInstance(equipString);
            if (equip != null) data.equipment.put(equip.name, equip);
        }

        //Initialize sets
        for (String setString : packet.sets)
        {
            Set set = Set.getInstance(setString, data);
            if (set != null) data.sets.put(set.id, set);
        }


        //Initialize bonus
        ClientBonus bonus = (ClientBonus) Bonus.getInstance(packet.bonusString, data);
        if (bonus != null) data.bonuses.put(bonus.id, bonus);


        //Initialize attribute modifiers
        for (String modifierString : packet.attributeMods)
        {
            BonusElementAttributeModifier.getInstance(modifierString, data);
        }

        //Initialize potions
        for (String potionString : packet.potions)
        {
            BonusElementPotionEffect.getInstance(potionString, data);
        }

        //Initialize enchantments
        for (String enchantString : packet.enchants)
        {
            BonusElementEnchantment.getInstance(enchantString, data);
        }


        //Reload JEI/HEI tooltips depending on config
        if (SetBonusConfig.clientSettings.dynamicTooltipSearch > 0) Compat.refreshTooltips();
    }
}
