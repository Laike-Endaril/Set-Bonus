package com.fantasticsource.setbonus.common.bonuselements;

import com.fantasticsource.setbonus.common.Bonus;

public class BonusElement
{
    public String parsedString;
    public Bonus bonus;

    protected BonusElement(String parsableBonusElement, Bonus bonus)
    {
        parsedString = parsableBonusElement;
        this.bonus = bonus;
    }
}
