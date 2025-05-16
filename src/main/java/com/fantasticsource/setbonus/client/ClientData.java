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

public class ClientData extends SetBonusData
{
    public static final ClientData CLIENT_DATA = new ClientData();


    protected ClientData()
    {
    }


    public void clear()
    {
        ClientBonus.dropAll();
        equipment.clear();
        sets.clear();
    }


    public void update(Network.ConfigPacket packet)
    {
        //Clear any existing data
        ClientBonus.dropAll();
        equipment.clear();
        sets.clear();


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


        //Initialize bonuses
        for (String bonusString : packet.bonuses)
        {
            ClientBonus bonus = (ClientBonus) Bonus.getInstance(bonusString, this);
            if (bonus != null) bonuses.put(bonus.id, bonus);
        }


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
    }


    public void update(Network.DiscoverBonusPacket packet)
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


        //Reload JEI/HEI tooltips depending on config
        if (SetBonusConfig.clientSettings.dynamicTooltipSearch > 0) Compat.refreshTooltips();
    }
}
