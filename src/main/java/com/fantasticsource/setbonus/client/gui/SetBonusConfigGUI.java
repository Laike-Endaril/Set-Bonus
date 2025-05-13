package com.fantasticsource.setbonus.client.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.tools.datastructures.Color;

public class SetBonusConfigGUI extends GUIScreen
{
    public SetBonusConfigGUI()
    {
        show();


        //Root
        root.add(new GUIDarkenedBackground(this));
        GUIElement element = new GUINavbar(this);
        ((GUINavbar) element).maxParentsDisplayed = 0;
        root.add(element);


        //Scrollview
        double x = 0, y = element.height, w = 0.98, h = 1 - element.height;
        GUIScrollView view = new GUIScrollView(this, x, y, w, h);
        root.add(view);
        root.add(new GUIVerticalScrollbar(this, view.x + view.width, view.y, 0.02, view.height, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, view));


        //TODO Equipment
        //TODO Sets
        //TODO Set Bonuses
        //TODO Set Bonus Elements
        //TODO Remember to do lang support in the GUI
        //TODO Remember to do explanations, also with lang support
    }


    @Override
    public void show()
    {
        showStacked();
    }

    @Override
    public String title()
    {
        return "Set Bonus";
    }
}
