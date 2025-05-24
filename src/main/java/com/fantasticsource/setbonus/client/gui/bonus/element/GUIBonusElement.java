package com.fantasticsource.setbonus.client.gui.bonus.element;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.GUITextLabel;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.tools.datastructures.Color;

public class GUIBonusElement extends GUITextLabel
{
    public ABonusElement bonusElement;

    public GUIBonusElement(GUIScreen screen, SetBonusData data, ABonusElement bonusElement, double width)
    {
        this(screen, data, bonusElement, width, 1);
    }

    public GUIBonusElement(GUIScreen screen, SetBonusData data, ABonusElement bonusElement, double width, double scale)
    {
        super(screen, width, Color.AQUA, scale);
        set(bonusElement);

        addClickActions(() -> new BonusElementGUI(data, this));
    }


    public void set(ABonusElement element)
    {
        this.bonusElement = element;
        if (element == null) internalText.setText("");
        else
        {
            String result = "";
            for (String tooltip : element.tooltips())
            {
                if (result.isEmpty()) result += tooltip;
                else result += ", " + tooltip;
            }
            internalText.setText(result);
        }
        runEditActions();
    }
}
