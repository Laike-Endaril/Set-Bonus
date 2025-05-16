package com.fantasticsource.setbonus.server;

import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementAttributeModifier;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementEnchantment;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementPotionEffect;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;

import java.util.LinkedHashMap;

import static com.fantasticsource.setbonus.config.SetBonusConfig.serverSettings;

public class ServerData extends SetBonusData
{
    public static final ServerData SERVER_DATA = new ServerData();


    protected ServerData()
    {
    }


    public void update()
    {
        //Clear any existing data
        ServerBonus.dropAll();
        equipment = new LinkedHashMap<>();
        sets = new LinkedHashMap<>();


        //Initialize equipment
        for (String equipString : serverSettings.getEquipment())
        {
            Equip equip = Equip.getInstance(equipString);
            if (equip != null) equipment.put(equip.name, equip);
        }


        //Initialize sets
        for (String setString : serverSettings.getSets())
        {
            Set set = Set.getInstance(setString, this);
            if (set != null) sets.put(set.id, set);
        }


        //Initialize bonuses
        for (String bonusString : serverSettings.getBonuses())
        {
            ServerBonus bonus = (ServerBonus) Bonus.getInstance(bonusString, this);
            if (bonus != null) bonuses.put(bonus.id, bonus);
        }


        //Initialize attribute modifiers
        for (String modifierString : serverSettings.getAttributeMods())
        {
            BonusElementAttributeModifier.getInstance(modifierString, this);
        }


        //Initialize potions
        for (String potionString : serverSettings.getPotions())
        {
            BonusElementPotionEffect.getInstance(potionString, this);
        }


        //Initialize enchantments
        for (String enchantString : serverSettings.getEnchantments())
        {
            BonusElementEnchantment.getInstance(enchantString, this);
        }
    }
}
