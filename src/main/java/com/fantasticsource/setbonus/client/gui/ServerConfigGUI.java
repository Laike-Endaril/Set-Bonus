package com.fantasticsource.setbonus.client.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.tools.datastructures.Color;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class ServerConfigGUI extends GUIScreen
{
    public ServerConfigGUI()
    {
        show();


        //Root
        root.setSubElementAutoplaceMethod(GUIElement.AP_CENTERED_H_TOP_TO_BOTTOM);

        root.add(new GUIDarkenedBackground(this));
        GUINavbar navbar = new GUINavbar(this);
        navbar.maxParentsDisplayed = 1;
        root.add(navbar);


        //Scrollviews
        double x = 0, y = navbar.height, w = 1d / 3 - 0.02, h = 1 - navbar.height;
        GUIScrollView categories = new GUIScrollView(this, x, y, w, h);
        root.add(categories);
        root.add(new GUIVerticalScrollbar(this, categories.x + categories.width, categories.y, 0.02, categories.height, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, categories));

        x = 1d / 3;
        GUIScrollView settings = new GUIScrollView(this, x, y, w, h);
        root.add(settings);
        root.add(new GUIVerticalScrollbar(this, settings.x + settings.width, settings.y, 0.02, settings.height, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, settings));

        x = 2d / 3;
        GUIScrollView settingEntries = new GUIScrollView(this, x, y, w, h);
        root.add(settingEntries);
        root.add(new GUIVerticalScrollbar(this, settingEntries.x + settingEntries.width, settingEntries.y, 0.02, settingEntries.height, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, settingEntries));


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
        return reformat(MODID + ".config.clientSettings");
    }
}
