package com.fantasticsource.setbonus;

import com.fantasticsource.mctools.MCTools;
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
import com.fantasticsource.setbonus.config.SetBonusConfig;
import com.fantasticsource.setbonus.server.ServerBonus;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.LinkedHashSet;

import static com.fantasticsource.setbonus.SetBonus.MODID;
import static com.fantasticsource.setbonus.config.SetBonusConfig.serverSettings;

public class SetBonusData
{
    public static boolean setServerFromConfigCalled = false;
    public static SetBonusData SERVER_DATA = new SetBonusData(), CLIENT_DATA = new SetBonusData();


    public LinkedHashSet<Equip> equipment = new LinkedHashSet<>();
    public LinkedHashSet<Set> sets = new LinkedHashSet<>();

    public LinkedHashSet<Bonus> bonuses = new LinkedHashSet<>();


    public void clear()
    {
        if (SERVER_DATA == this) ServerBonus.dropAll();
        if (CLIENT_DATA == this) ClientBonus.dropAll();

        equipment.clear();
        sets.clear();
        bonuses.clear();
    }


    public static void setServerFromConfig()
    {
        setServerFromConfigCalled = true;
        SERVER_DATA.setFromConfig();

        if (MCTools.hosting())
        {
            for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
            {
                Network.updateConfig(player);
            }
        }
    }


    public static void setClientFromPacket(Network.AllDiscoveredBonusesPacket packet)
    {
        CLIENT_DATA.setFromPacket(packet);
        if (SetBonusConfig.clientSettings.dynamicTooltipSearch > 0) Compat.refreshTooltips();
    }


    public void setFromConfig()
    {
        clear();


        for (String equipString : serverSettings.getEquipment())
        {
            Equip equip = Equip.getInstance(equipString);
            if (equip != null) equipment.add(equip);
        }

        for (String setString : serverSettings.getSets())
        {
            Set set = Set.getInstance(setString, this);
            if (set != null) sets.add(set);
        }

        for (String bonusString : serverSettings.getBonuses())
        {
            ServerBonus bonus = (ServerBonus) Bonus.getInstance(bonusString, this);
            if (bonus != null) bonuses.add(bonus);
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

        for (Equip equip : equipment) equips.add(equip.toString());
        for (Set set : this.sets) sets.add(set.toString());
        for (Bonus bonus : this.bonuses)
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


        ConfigManager.sync(MODID, Config.Type.INSTANCE);


        SERVER_DATA.clear();
        setServerFromConfig();
    }


    public void setFromPacket(Network.AllDiscoveredBonusesPacket packet)
    {
        clear();


        for (String equipString : packet.equipment)
        {
            Equip equip = Equip.getInstance(equipString);
            if (equip != null) equipment.add(equip);
        }

        for (String setString : packet.sets)
        {
            Set set = Set.getInstance(setString, this);
            if (set != null) sets.add(set);
        }

        for (String bonusString : packet.bonuses)
        {
            ClientBonus bonus = (ClientBonus) Bonus.getInstance(bonusString, this);
            if (bonus != null) bonuses.add(bonus);
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
            if (equip != null) equipment.add(equip);
        }

        //Initialize sets
        for (String setString : packet.sets)
        {
            Set set = Set.getInstance(setString, this);
            if (set != null) sets.add(set);
        }


        //Initialize bonus
        ClientBonus bonus = (ClientBonus) Bonus.getInstance(packet.bonusString, this);
        if (bonus != null) bonuses.add(bonus);


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
        sets.removeIf(set ->
        {
            set.slotData.removeIf(slotData -> slotData.involvedEquips.remove(equip) && slotData.involvedEquips.size() == 0);
            return set.slotData.size() == 0;
        });
        for (Bonus bonus : bonuses)
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
        equipment.remove(equip);
    }

    public void delete(Set set)
    {
        for (Bonus bonus : bonuses)
        {
            bonus.requirements.removeIf(requirement -> requirement instanceof SetRequirement && ((SetRequirement) requirement).set == set);
        }
        sets.remove(set);
    }

    public void delete(Bonus bonus)
    {
        bonuses.remove(bonus);
    }


    public SetBonusData clone()
    {
        SetBonusData other = new SetBonusData();

        for (Equip equip : equipment) other.equipment.add(equip.clone());

        for (Set set : sets) other.sets.add(set.clone(other));

        for (Bonus bonus : bonuses) other.bonuses.add(bonus.clone(other));

        return other;
    }
}
