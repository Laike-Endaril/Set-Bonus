package com.fantasticsource.setbonus.common.bonuselements;

import com.fantasticsource.setbonus.common.Bonus;
import net.minecraft.entity.player.EntityPlayer;

public abstract class ABonusElement
{
    public String parsedString;
    public Bonus bonus;

    protected ABonusElement(String parsableBonusElement, Bonus bonus)
    {
        parsedString = parsableBonusElement;
        this.bonus = bonus;
        bonus.bonusElements.add(this);
    }

    public abstract void activate(EntityPlayer player);

    public abstract void deactivate(EntityPlayer player);

    public abstract void updateActive(EntityPlayer player);
}
