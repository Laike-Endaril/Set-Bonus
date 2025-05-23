package com.fantasticsource.setbonus.client.gui.bonus;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.GUITextLabel;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.client.gui.ServerConfigGUI;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.tools.datastructures.Color;

public class GUIBonusElement extends GUITextLabel
{
    public ABonusElement element;

    public GUIBonusElement(GUIScreen screen, SetBonusData data, ABonusElement element, double width)
    {
        this(screen, data, element, width, 1);
    }

    public GUIBonusElement(GUIScreen screen, SetBonusData data, ABonusElement element, double width, double scale)
    {
        super(screen, width, Color.AQUA, scale);
        set(element);

        addClickActions(() ->
        {
            if (internalText.activeColor == Color.PURPLE) new BonusElementGUI(data, this);
            else if (screen instanceof ServerConfigGUI) ((ServerConfigGUI) screen).select(this);
        });
    }


    public void set(ABonusElement element)
    {
        this.element = element;
        internalText.setText(element == null ? "" : element.toString());
        runEditActions();
    }
}
