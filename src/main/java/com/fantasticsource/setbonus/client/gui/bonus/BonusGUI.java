package com.fantasticsource.setbonus.client.gui.bonus;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.setbonus.common.Bonus;

public class BonusGUI extends GUIScreen
{
    public GUIBonus clickedElement;
    public Bonus bonus;

    public BonusGUI(GUIBonus clickedElement)
    {
        this.clickedElement = clickedElement;
        bonus = clickedElement.bonus;


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
        return reformat(bonus.id) + " (" + reformat(bonus.name) + ")";
    }
}
