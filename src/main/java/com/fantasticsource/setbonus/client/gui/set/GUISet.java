package com.fantasticsource.setbonus.client.gui.set;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.GUITextLabel;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.client.gui.ServerConfigGUI;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import com.fantasticsource.tools.datastructures.Color;

public class GUISet extends GUITextLabel
{
    public Set set;

    public GUISet(GUIScreen screen, SetBonusData data, Set set, double width)
    {
        this(screen, data, set, width, 1);
    }

    public GUISet(GUIScreen screen, SetBonusData data, Set set, double width, double scale)
    {
        super(screen, width, Color.AQUA, scale);
        set(set);

        addClickActions(() ->
        {
            if (internalText.activeColor == Color.PURPLE) new SetGUI(data, this);
            else if (screen instanceof ServerConfigGUI) ((ServerConfigGUI) screen).select(this);
        });
    }


    public void set(Set set)
    {
        this.set = set;
        internalText.setText(reformat(set.id));
    }
}
