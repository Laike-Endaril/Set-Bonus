package com.fantasticsource.setbonus.client.gui.equip;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;

public class EquipGUI extends GUIScreen
{
    public GUIEquip clickedElement;

    public EquipGUI(GUIEquip clickedElement)
    {
        this.clickedElement = clickedElement;


        show();


        //Root
        root.add(new GUIDarkenedBackground(this));
        GUINavbar navbar = new GUINavbar(this);
        root.add(navbar);
    }

    @Override
    public String title()
    {
        return clickedElement.internalText.getText();
    }
}
