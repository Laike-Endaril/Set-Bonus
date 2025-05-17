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

public class ServerData
{
    public static void update()
    {
        SetBonusData data = SetBonusData.SERVER_DATA;


        //Clear any existing data
        ServerBonus.dropAll();
        data.equipment = new LinkedHashMap<>();
        data.sets = new LinkedHashMap<>();


        //Initialize equipment
        for (String equipString : serverSettings.getEquipment())
        {
            Equip equip = Equip.getInstance(equipString);
            if (equip != null) data.equipment.put(equip.name, equip);
        }


        //Initialize sets
        for (String setString : serverSettings.getSets())
        {
            Set set = Set.getInstance(setString, data);
            if (set != null) data.sets.put(set.id, set);
        }


        //Initialize bonuses
        for (String bonusString : serverSettings.getBonuses())
        {
            ServerBonus bonus = (ServerBonus) Bonus.getInstance(bonusString, data);
            if (bonus != null) data.bonuses.put(bonus.id, bonus);
        }


        //Initialize attribute modifiers
        for (String modifierString : serverSettings.getAttributeMods())
        {
            BonusElementAttributeModifier.getInstance(modifierString, data);
        }


        //Initialize potions
        for (String potionString : serverSettings.getPotions())
        {
            BonusElementPotionEffect.getInstance(potionString, data);
        }


        //Initialize enchantments
        for (String enchantString : serverSettings.getEnchantments())
        {
            BonusElementEnchantment.getInstance(enchantString, data);
        }
    }
}
