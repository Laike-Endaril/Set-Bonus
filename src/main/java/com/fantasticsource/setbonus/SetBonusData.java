package com.fantasticsource.setbonus;

import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;

import java.util.LinkedHashMap;

public class SetBonusData
{
    public LinkedHashMap<String, Equip> equipment = new LinkedHashMap<>();
    public LinkedHashMap<String, Set> sets = new LinkedHashMap<>();

    public LinkedHashMap<String, Bonus> bonuses = new LinkedHashMap<>();
}
