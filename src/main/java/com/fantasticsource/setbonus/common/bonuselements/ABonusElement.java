package com.fantasticsource.setbonus.common.bonuselements;

import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.common.Bonus;
import net.minecraft.entity.player.EntityPlayer;

public abstract class ABonusElement
{
    public Bonus bonus;

    protected ABonusElement(Bonus bonus)
    {
        this.bonus = bonus;
        bonus.bonusElements.add(this);
    }

    public abstract void activate(EntityPlayer player);

    public abstract void deactivate(EntityPlayer player);

    public abstract void updateActive(EntityPlayer player);

    public abstract String[] tooltips();

    public abstract ABonusElement clone(SetBonusData data);

    public abstract String toString();
}
