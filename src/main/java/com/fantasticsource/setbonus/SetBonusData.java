package com.fantasticsource.setbonus;

import com.fantasticsource.setbonus.client.ClientBonus;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.Network;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementAttributeModifier;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementEnchantment;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementPotionEffect;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SetRequirement;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SlotData;
import com.fantasticsource.setbonus.config.SetBonusConfig;
import com.fantasticsource.setbonus.server.ServerBonus;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static com.fantasticsource.setbonus.SetBonus.MODID;
import static com.fantasticsource.setbonus.config.SetBonusConfig.serverSettings;

public class SetBonusData
{
    public static SetBonusData SERVER_DATA = new SetBonusData(), CLIENT_DATA = new SetBonusData();

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
            if (equip != null) equipment.put(equip.id, equip);
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


    public void applyToConfig()
    {
        LinkedHashSet<String>
                equips = new LinkedHashSet<>(),
                sets = new LinkedHashSet<>(),
                bonuses = new LinkedHashSet<>(),
                attributeModifiers = new LinkedHashSet<>(),
                potions = new LinkedHashSet<>(),
                enchantments = new LinkedHashSet<>();

        for (Equip equip : equipment.values()) equips.add(equip.toString());
        for (Set set : this.sets.values()) sets.add(set.toString());
        for (Bonus bonus : this.bonuses.values())
        {
            bonuses.add(bonus.toString());

            for (ABonusElement bonusElement : bonus.bonusElements)
            {
                if (bonusElement instanceof BonusElementAttributeModifier) attributeModifiers.add(bonusElement.toString());
                else if (bonusElement instanceof BonusElementPotionEffect) potions.add(bonusElement.toString());
                else if (bonusElement instanceof BonusElementEnchantment) enchantments.add(bonusElement.toString());
            }
        }


        serverSettings.equipment = equips.toArray(new String[0]);
        serverSettings.sets = sets.toArray(new String[0]);
        serverSettings.bonuses = bonuses.toArray(new String[0]);
        serverSettings.attributeMods = attributeModifiers.toArray(new String[0]);
        serverSettings.potions = potions.toArray(new String[0]);
        serverSettings.enchantments = enchantments.toArray(new String[0]);

        SERVER_DATA = this;


        ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }


    public void setFromPacket(Network.AllDiscoveredBonusesPacket packet)
    {
        clear();


        for (String equipString : packet.equipment)
        {
            Equip equip = Equip.getInstance(equipString);
            if (equip != null) equipment.put(equip.id, equip);
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
            if (equip != null) equipment.put(equip.id, equip);
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
        for (Set set : sets.values().toArray(new Set[0]))
        {
            for (SlotData slotData : set.slotData) slotData.involvedEquips.remove(equip);
        }
        for (Bonus bonus : bonuses.values())
        {
            for (ABonusElement element : bonus.bonusElements)
            {
                if (element instanceof BonusElementEnchantment)
                {
                    BonusElementEnchantment bEE = (BonusElementEnchantment) element;
                    bEE.slotDataToEnchant.involvedEquips.remove(equip);
                }
            }
        }
        equipment.remove(equip.id);
    }

    public void delete(Set set)
    {
        for (Bonus bonus : bonuses.values())
        {
            bonus.bonusRequirements.removeIf(requirement -> requirement instanceof SetRequirement && ((SetRequirement) requirement).set == set);
        }
        sets.remove(set.id);
    }

    public void delete(Bonus bonus)
    {
        bonuses.remove(bonus.id);
    }


    public SetBonusData clone()
    {
        SetBonusData other = new SetBonusData();

        for (Map.Entry<String, Equip> entry : equipment.entrySet()) other.equipment.put(entry.getKey(), entry.getValue().clone());
        for (Map.Entry<String, Set> entry : sets.entrySet()) other.sets.put(entry.getKey(), entry.getValue().clone(other));

        for (Map.Entry<String, Bonus> entry : bonuses.entrySet()) entry.getValue().clone(other);

        return other;
    }
}
