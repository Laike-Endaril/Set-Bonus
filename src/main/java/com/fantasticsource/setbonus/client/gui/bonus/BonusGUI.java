package com.fantasticsource.setbonus.client.gui.bonus;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;

public class BonusGUI extends GUIScreen
{
    public GUIBonus clickedElement;

    public BonusGUI(GUIBonus clickedElement)
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
