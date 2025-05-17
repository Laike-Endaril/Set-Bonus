package com.fantasticsource.setbonus.client.gui.bonus;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.GUITextLabel;
import com.fantasticsource.setbonus.client.gui.ServerConfigGUI;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.tools.datastructures.Color;

public class GUIBonus extends GUITextLabel
{
    public Bonus bonus;

    public GUIBonus(GUIScreen screen, Bonus bonus, double width)
    {
        this(screen, bonus, width, 1);
    }

    public GUIBonus(GUIScreen screen, Bonus bonus, double width, double scale)
    {
        super(screen, width, Color.AQUA, scale);
        set(bonus);

        addClickActions(() ->
        {
            if (internalText.activeColor == Color.PURPLE) new BonusGUI(this);
            else if (screen instanceof ServerConfigGUI) ((ServerConfigGUI) screen).select(this);
        });
    }


    public void set(Bonus bonus)
    {
        this.bonus = bonus;
        internalText.setText(reformat(bonus.name));
    }
}
