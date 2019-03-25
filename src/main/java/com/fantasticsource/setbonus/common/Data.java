package com.fantasticsource.setbonus.common;

import com.fantasticsource.setbonus.common.bonuselements.ModifierBonus;
import com.fantasticsource.setbonus.common.bonuselements.PotionBonus;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import com.fantasticsource.setbonus.config.SyncedConfig;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Data
{
    public static LinkedHashMap<String, Equip> equipment = null;
    public static LinkedHashMap<String, Set> sets;

    public static LinkedHashMap<String, Bonus> bonuses = new LinkedHashMap<>();


    public static ArrayList<EntityPlayer> players = new ArrayList<>();


    public static void update()
    {
        //Clear any existing data
        BonusData.dropAll();
        equipment = new LinkedHashMap<>();
        sets = new LinkedHashMap<>();
        players.clear();


        //Initialize equipment
        for (String equipString : SyncedConfig.equipment)
        {
            Equip equip = Equip.getInstance(equipString);
            if (equip != null) equipment.put(equip.id, equip);
        }


        //Initialize sets
        for (String setString : SyncedConfig.sets)
        {
            Set set = Set.getInstance(setString);
            if (set != null) sets.put(set.id, set);
        }


        //Initialize bonuses
        for (String bonusString : SyncedConfig.bonuses)
        {
            Bonus bonus = Bonus.getInstance(bonusString);
            if (bonus != null) Data.bonuses.put(bonus.id, bonus);
        }


        //Initialize attribute modifiers
        for (String modifierString : SyncedConfig.attributeMods)
        {
            ModifierBonus.getInstance(modifierString);
        }


        //Initialize potions
        for (String potionString : SyncedConfig.potions)
        {
            PotionBonus.getInstance(potionString);
        }
    }
}
