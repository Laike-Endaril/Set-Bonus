package com.fantasticsource.setbonus.client.gui.bonus.element;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.GUITextLabel;
import com.fantasticsource.mctools.potions.FantasticPotionEffect;
import com.fantasticsource.tools.datastructures.Color;

public class GUIPotionEffect extends GUITextLabel
{
    public FantasticPotionEffect potionEffect;

    public GUIPotionEffect(GUIScreen screen, FantasticPotionEffect potionEffect, double width)
    {
        this(screen, potionEffect, width, 1);
    }

    public GUIPotionEffect(GUIScreen screen, FantasticPotionEffect potionEffect, double width, double scale)
    {
        super(screen, width, Color.AQUA, scale);
        set(potionEffect);

        addClickActions(() -> new PotionEffectGUI(this));
    }


    public void set(FantasticPotionEffect potionEffect)
    {
        this.potionEffect = potionEffect;
        if (potionEffect == null) internalText.setText("");
        else internalText.setText(potionEffect.toString());
        runEditActions();
    }
}
