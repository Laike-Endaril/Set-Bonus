package com.fantasticsource.setbonus.client.gui.equip;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.GUITextLabel;
import com.fantasticsource.setbonus.client.gui.ServerConfigGUI;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.tools.datastructures.Color;

public class GUIEquip extends GUITextLabel
{
    public Equip equip;

    public GUIEquip(GUIScreen screen, Equip equip, double width)
    {
        this(screen, equip, width, 1);
    }

    public GUIEquip(GUIScreen screen, Equip equip, double width, double scale)
    {
        super(screen, width, Color.AQUA, Color.BLANK, scale);
        set(equip);

        addClickActions(() ->
        {
            if (internalText.activeColor == Color.PURPLE) new EquipGUI(this);
            else if (screen instanceof ServerConfigGUI) ((ServerConfigGUI) screen).select(this);
        });
    }


    public void set(Equip equip)
    {
        this.equip = equip;
        internalText.setText(reformat(equip.name));
    }


    @Override
    public void draw()
    {
        super.draw();
    }
}
