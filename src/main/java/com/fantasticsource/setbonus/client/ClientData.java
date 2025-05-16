package com.fantasticsource.setbonus.client;

import com.fantasticsource.setbonus.Compat;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.common.Network;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementAttributeModifier;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementEnchantment;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementPotionEffect;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import com.fantasticsource.setbonus.config.SetBonusConfig;
import net.minecraftforge.fml.relauncher.Side;

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
            Set set = Set.getInstance(setString, Side.CLIENT);
            if (set != null) sets.put(set.id, set);
        }


        //Initialize bonuses
        for (String bonusString : packet.bonuses)
        {
            ClientBonus bonus = ClientBonus.getInstance(bonusString);
            if (bonus != null) bonuses.put(bonus.id, bonus);
        }


        //Initialize attribute modifiers
        for (String modifierString : packet.attributeMods)
        {
            BonusElementAttributeModifier.getInstance(modifierString, Side.CLIENT);
        }


        //Initialize potions
        for (String potionString : packet.potions)
        {
            BonusElementPotionEffect.getInstance(potionString, Side.CLIENT);
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
            Set set = Set.getInstance(setString, Side.CLIENT);
            if (set != null) sets.put(set.id, set);
        }


        //Initialize bonus
        ClientBonus bonus = ClientBonus.getInstance(packet.bonusString);
        if (bonus != null) bonuses.put(bonus.id, bonus);


        //Initialize attribute modifiers
        for (String modifierString : packet.attributeMods)
        {
            BonusElementAttributeModifier.getInstance(modifierString, Side.CLIENT);
        }

        //Initialize potions
        for (String potionString : packet.potions)
        {
            BonusElementPotionEffect.getInstance(potionString, Side.CLIENT);
        }

        //Initialize enchantments
        for (String enchantString : packet.enchants)
        {
            BonusElementEnchantment.getInstance(enchantString, Side.CLIENT);
        }


        //Reload JEI/HEI tooltips depending on config
        if (SetBonusConfig.clientSettings.dynamicTooltipSearch > 0) Compat.refreshTooltips();
    }
}
