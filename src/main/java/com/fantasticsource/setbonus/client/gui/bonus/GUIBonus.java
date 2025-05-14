package com.fantasticsource.setbonus.client.gui.bonus;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.text.GUITextLabel;
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
        super(screen, width, Color.AQUA, Color.BLANK, scale);
        set(bonus);
        setColor(Color.AQUA);

        addClickActions(() ->
        {
            if (internalText.activeColor == Color.PURPLE) new BonusGUI(this);
            else
            {
                setColor(Color.PURPLE);
                if (parent != null)
                {
                    for (GUIElement element : parent.children)
                    {
                        if (element instanceof GUIBonus && element != this) ((GUIBonus) element).setColor(Color.AQUA);
                    }
                }
            }
        });
    }


    public void set(Bonus bonus)
    {
        this.bonus = bonus;
        internalText.setText(reformat(bonus.name));
    }
}
