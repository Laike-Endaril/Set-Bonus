package com.fantasticsource.setbonus.client;

import com.fantasticsource.setbonus.common.Network;
import com.fantasticsource.setbonus.common.bonuselements.ModifierBonus;
import com.fantasticsource.setbonus.common.bonuselements.PotionBonus;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;

import java.util.LinkedHashMap;

public class ClientData
{
    public static LinkedHashMap<String, Equip> equipment = new LinkedHashMap<>();
    public static LinkedHashMap<String, Set> sets = new LinkedHashMap<>();

    public static LinkedHashMap<String, ClientBonus> bonuses = new LinkedHashMap<>();


    public static void clear()
    {
        ClientBonus.dropAll();
        equipment.clear();
        sets.clear();
    }


    public static void update(Network.ConfigPacket packet)
    {
        //Clear any existing data
        ClientBonus.dropAll();
        equipment.clear();
        sets.clear();


        //Initialize equipment
        for (String equipString : packet.equipment)
        {
            Equip equip = Equip.getInstance(equipString);
            if (equip != null) equipment.put(equip.id, equip);
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
            ModifierBonus.getInstance(modifierString, Side.CLIENT);
        }


        //Initialize potions
        for (String potionString : packet.potions)
        {
            PotionBonus.getInstance(potionString, Side.CLIENT);
        }
    }


    public static void update(Network.DiscoverBonusPacket packet)
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
            Set set = Set.getInstance(setString, Side.CLIENT);
            if (set != null) sets.put(set.id, set);
        }


        //Initialize bonus
        ClientBonus bonus = ClientBonus.getInstance(packet.bonusString);
        if (bonus != null) bonuses.put(bonus.id, bonus);


        //Initialize attribute modifiers
        for (String modifierString : packet.attributeMods)
        {
            ModifierBonus.getInstance(modifierString, Side.CLIENT);
        }


        //Initialize potions
        for (String potionString : packet.potions)
        {
            PotionBonus.getInstance(potionString, Side.CLIENT);
        }
    }
}
