package com.fantasticsource.setbonus;

import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;

import java.util.LinkedHashMap;
import java.util.Map;

public class SetBonusData
{
    public static final SetBonusData SERVER_DATA = new SetBonusData(), CLIENT_DATA = new SetBonusData();

    public LinkedHashMap<String, Equip> equipment = new LinkedHashMap<>();
    public LinkedHashMap<String, Set> sets = new LinkedHashMap<>();

    public LinkedHashMap<String, Bonus> bonuses = new LinkedHashMap<>();


    public SetBonusData clone()
    {
        SetBonusData other = new SetBonusData();

        for (Map.Entry<String, Equip> entry : equipment.entrySet()) other.equipment.put(entry.getKey(), entry.getValue().clone());
        for (Map.Entry<String, Set> entry : sets.entrySet()) other.sets.put(entry.getKey(), entry.getValue().clone());

        for (Map.Entry<String, Bonus> entry : bonuses.entrySet()) other.bonuses.put(entry.getKey(), entry.getValue().clone(other));

        return other;
    }
}
