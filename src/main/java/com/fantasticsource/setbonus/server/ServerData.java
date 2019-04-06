package com.fantasticsource.setbonus.server;

import com.fantasticsource.setbonus.common.bonuselements.ModifierBonus;
import com.fantasticsource.setbonus.common.bonuselements.PotionBonus;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import net.minecraftforge.fml.relauncher.Side;

import java.util.LinkedHashMap;

import static com.fantasticsource.setbonus.config.SetBonusConfig.serverSettings;

public class ServerData
{
    public static LinkedHashMap<String, Equip> equipment = null;
    public static LinkedHashMap<String, Set> sets;

    public static LinkedHashMap<String, ServerBonus> bonuses = new LinkedHashMap<>();


    public static void update()
    {
        //Clear any existing data
        ServerBonus.dropAll();
        equipment = new LinkedHashMap<>();
        sets = new LinkedHashMap<>();


        //Initialize equipment
        for (String equipString : serverSettings.equipment)
        {
            Equip equip = Equip.getInstance(equipString);
            if (equip != null) equipment.put(equip.id, equip);
        }


        //Initialize sets
        for (String setString : serverSettings.sets)
        {
            Set set = Set.getInstance(setString, Side.SERVER);
            if (set != null) sets.put(set.id, set);
        }


        //Initialize bonuses
        for (String bonusString : serverSettings.bonuses)
        {
            ServerBonus bonus = ServerBonus.getInstance(bonusString);
            if (bonus != null) bonuses.put(bonus.id, bonus);
        }


        //Initialize attribute modifiers
        for (String modifierString : serverSettings.attributeMods)
        {
            ModifierBonus.getInstance(modifierString, Side.SERVER);
        }


        //Initialize potions
        for (String potionString : serverSettings.potions)
        {
            PotionBonus.getInstance(potionString, Side.SERVER);
        }
    }
}
