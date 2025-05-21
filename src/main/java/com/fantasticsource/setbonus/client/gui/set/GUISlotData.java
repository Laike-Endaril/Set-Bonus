package com.fantasticsource.setbonus.client.gui.set;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.GUITextLabel;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.client.gui.ServerConfigGUI;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SlotData;
import com.fantasticsource.tools.datastructures.Color;

public class GUISlotData extends GUITextLabel
{
    public SlotData slotData;

    public GUISlotData(GUIScreen screen, SetBonusData data, SlotData slotData, double width)
    {
        this(screen, data, slotData, width, 1);
    }

    public GUISlotData(GUIScreen screen, SetBonusData data, SlotData slotData, double width, double scale)
    {
        super(screen, width, Color.AQUA, scale);
        set(slotData);

        addClickActions(() ->
        {
            if (internalText.activeColor == Color.PURPLE) new SlotDataGUI(data, this);
            else if (screen instanceof ServerConfigGUI) ((ServerConfigGUI) screen).select(this);
        });
    }


    public void set(SlotData slotData)
    {
        this.slotData = slotData;
        if (slotData != null) internalText.setText(slotData.toString());
    }
}
