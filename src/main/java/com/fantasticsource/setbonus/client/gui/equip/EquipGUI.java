package com.fantasticsource.setbonus.client.gui.equip;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;

public class EquipGUI extends GUIScreen
{
    public GUIEquip clickedElement;
    public Equip equip;

    public EquipGUI(GUIEquip clickedElement)
    {
        this.clickedElement = clickedElement;
        equip = clickedElement.equip;


        show();


        //Root
        root.add(new GUIDarkenedBackground(this));
        GUINavbar navbar = new GUINavbar(this);
        root.add(navbar);


        //TODO
    }

    @Override
    public String title()
    {
        return reformat(equip.id);
    }
}
