package com.fantasticsource.setbonus.client.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextLabel;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.tools.datastructures.Color;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class ServerConfigGUI extends GUIScreen
{
    public ServerConfigGUI()
    {
        show();


        //Root
        root.add(new GUIDarkenedBackground(this));
        GUINavbar navbar = new GUINavbar(this);
        navbar.maxParentsDisplayed = 1;
        root.add(navbar);


        //Left column; bonuses and sets
        GUIView left = new GUIView(this, 1d / 3, 1 - navbar.height);
        root.add(left);

        GUITextLabel bonusesLabel = new GUITextLabel(this, 1, Color.GREEN).setText(reformat("setbonus.config.bonuses"));
        left.add(bonusesLabel);
        GUIScrollView bonuses = new GUIScrollView(this, (1d / 3 - 0.02) * 3, 0.5 - bonusesLabel.height);
        GUIVerticalScrollbar bonusesScrollbar = new GUIVerticalScrollbar(this, 1 - bonuses.width, bonuses.height, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, bonuses);
        left.addAll(bonuses, bonusesScrollbar);

        GUITextLabel setsLabel = new GUITextLabel(this, 1, Color.GREEN).setText(reformat("setbonus.config.sets"));
        left.add(setsLabel);
        GUIScrollView sets = new GUIScrollView(this, (1d / 3 - 0.02) * 3, 1 - setsLabel.y - setsLabel.height);
        GUIVerticalScrollbar setsScrollbar = new GUIVerticalScrollbar(this, 1 - sets.width, sets.height, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, sets);
        left.addAll(sets, setsScrollbar);


        //Center column
        GUIView center = new GUIView(this, 1d / 3, 1 - navbar.height);
        root.add(center);

        GUITextLabel settingsLabel = new GUITextLabel(this, 1, Color.GREEN); //TODO change label text to currently selected bonus or set
        center.add(settingsLabel);
        GUIScrollView settings = new GUIScrollView(this, (1d / 3 - 0.02) * 3, 1 - settingsLabel.height);
        GUIVerticalScrollbar settingsScrollbar = new GUIVerticalScrollbar(this, 1 - settings.width, settings.height, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, settings);
        center.addAll(settings, settingsScrollbar);


        //Right column
        GUIView right = new GUIView(this, 1d / 3, 1 - navbar.height);
        root.add(right);

        GUITextLabel settings2Label = new GUITextLabel(this, 1, Color.GREEN); //TODO change label to entry selected from center column
        right.add(settings2Label);
        GUIScrollView settings2 = new GUIScrollView(this, (1d / 3 - 0.02) * 3, 1 - settings2Label.height);
        GUIVerticalScrollbar settingsScrollbar2 = new GUIVerticalScrollbar(this, 1 - settings2.width, settings2.height, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, settings2);
        right.addAll(settings2, settingsScrollbar2);


        //Main recalcs
        navbar.addRecalcActions(() ->
        {
            left.height = 1 - navbar.height;
            center.height = 1 - navbar.height;
            right.height = 1 - navbar.height;
        });

        //Left column recalcs
        bonusesLabel.addRecalcActions(() ->
        {
            bonuses.height = 0.5 - bonusesLabel.height;
            bonusesScrollbar.height = bonuses.height;

            setsLabel.recalc(0);
        });
        setsLabel.addRecalcActions(() ->
        {
            sets.height = 1 - setsLabel.y - setsLabel.height;
            setsScrollbar.height = sets.height;
        });

        //Center column recalcs
        settingsLabel.addRecalcActions(() ->
        {
            settings.height = 1 - settingsLabel.height;
            settingsScrollbar.height = settings.height;
        });

        //Right column recalcs
        settings2Label.addRecalcActions(() ->
        {
            settings2.height = 1 - settings2Label.height;
            settingsScrollbar2.height = settings2.height;
        });
    }


    @Override
    public void show()
    {
        showStacked();
    }

    @Override
    public String title()
    {
        return reformat(MODID + ".config.serverSettings");
    }
}
