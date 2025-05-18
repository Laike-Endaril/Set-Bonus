package com.fantasticsource.setbonus.client.gui.set;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;

public class SetGUI extends GUIScreen
{
    public GUISet clickedElement;
    public Set set;

    public SetGUI(GUISet clickedElement)
    {
        this.clickedElement = clickedElement;
        set = clickedElement.set;


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
        return reformat(set.id) + " (" + reformat(set.name) + ")";
    }
}
