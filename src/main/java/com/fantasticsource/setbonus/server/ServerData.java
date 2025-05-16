package com.fantasticsource.setbonus.server;

import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementAttributeModifier;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementEnchantment;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementPotionEffect;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import net.minecraftforge.fml.relauncher.Side;

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
            Set set = Set.getInstance(setString, Side.SERVER);
            if (set != null) sets.put(set.id, set);
        }


        //Initialize bonuses
        for (String bonusString : serverSettings.getBonuses())
        {
            ServerBonus bonus = ServerBonus.getInstance(bonusString);
            if (bonus != null) bonuses.put(bonus.id, bonus);
        }


        //Initialize attribute modifiers
        for (String modifierString : serverSettings.getAttributeMods())
        {
            BonusElementAttributeModifier.getInstance(modifierString, Side.SERVER);
        }


        //Initialize potions
        for (String potionString : serverSettings.getPotions())
        {
            BonusElementPotionEffect.getInstance(potionString, Side.SERVER);
        }


        //Initialize enchantments
        for (String enchantString : serverSettings.getEnchantments())
        {
            BonusElementEnchantment.getInstance(enchantString, Side.SERVER);
        }
    }
}
