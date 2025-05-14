package com.fantasticsource.setbonus.client.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextLabel;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.setbonus.client.gui.bonus.GUIBonus;
import com.fantasticsource.setbonus.client.gui.set.GUISet;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import com.fantasticsource.setbonus.server.ServerData;
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
        root.add(navbar);


        //Equips
        GUIView equipsColumn = new GUIView(this, 0.25, 1 - navbar.height);
        root.add(equipsColumn);

        GUITextLabel equipsLabel = new GUITextLabel(this, 1, Color.GREEN).setText(reformat(MODID + ".config.equipment"));
        equipsColumn.add(equipsLabel);
        GUIScrollView equips = new GUIScrollView(this, (1d / 3 - 0.02) * 3, 1 - equipsLabel.height);
        GUIVerticalScrollbar equipsScrollbar = new GUIVerticalScrollbar(this, 1 - equips.width, equips.height, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, equips);
        equipsColumn.addAll(equips, equipsScrollbar);


        //Bonuses and sets
        GUIView bonusesAndSetsColumn = new GUIView(this, 0.25, 1 - navbar.height);
        root.add(bonusesAndSetsColumn);

        GUITextLabel bonusesLabel = new GUITextLabel(this, 1, Color.GREEN).setText(reformat(MODID + ".config.bonuses"));
        bonusesAndSetsColumn.add(bonusesLabel);
        GUIScrollView bonuses = new GUIScrollView(this, (1d / 3 - 0.02) * 3, 0.5 - bonusesLabel.height);
        GUIVerticalScrollbar bonusesScrollbar = new GUIVerticalScrollbar(this, 1 - bonuses.width, bonuses.height, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, bonuses);
        bonusesAndSetsColumn.addAll(bonuses, bonusesScrollbar);

        GUITextLabel setsLabel = new GUITextLabel(this, 1, Color.GREEN).setText(reformat(MODID + ".config.sets"));
        bonusesAndSetsColumn.add(setsLabel);
        GUIScrollView sets = new GUIScrollView(this, (1d / 3 - 0.02) * 3, 1 - setsLabel.y - setsLabel.height);
        GUIVerticalScrollbar setsScrollbar = new GUIVerticalScrollbar(this, 1 - sets.width, sets.height, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, sets);
        bonusesAndSetsColumn.addAll(sets, setsScrollbar);


        //Settings
        GUIView settingsColumn = new GUIView(this, 0.25, 1 - navbar.height);
        root.add(settingsColumn);

        GUITextLabel settingsLabel = new GUITextLabel(this, 1, Color.GREEN);
        settingsColumn.add(settingsLabel);
        GUIScrollView settings = new GUIScrollView(this, (1d / 3 - 0.02) * 3, 1 - settingsLabel.height);
        GUIVerticalScrollbar settingsScrollbar = new GUIVerticalScrollbar(this, 1 - settings.width, settings.height, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, settings);
        settingsColumn.addAll(settings, settingsScrollbar);


        //Details
        GUIView detailsColumn = new GUIView(this, 0.25, 1 - navbar.height);
        root.add(detailsColumn);

        GUITextLabel detailsLabel = new GUITextLabel(this, 1, Color.GREEN); //TODO change label to entry selected from center column
        detailsColumn.add(detailsLabel);
        GUIScrollView details = new GUIScrollView(this, (1d / 3 - 0.02) * 3, 1 - detailsLabel.height);
        GUIVerticalScrollbar detailsScrollbar = new GUIVerticalScrollbar(this, 1 - details.width, details.height, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, details);
        detailsColumn.addAll(details, detailsScrollbar);


        //Main recalcs
        navbar.addRecalcActions(() ->
        {
            bonusesAndSetsColumn.height = 1 - navbar.height;
            settingsColumn.height = 1 - navbar.height;
            detailsColumn.height = 1 - navbar.height;
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
        detailsLabel.addRecalcActions(() ->
        {
            details.height = 1 - detailsLabel.height;
            detailsScrollbar.height = details.height;
        });


        for (Set set : ServerData.sets.values())
        {
            GUISet guiSet = new GUISet(this, set, 1, 0.5);
            sets.add(guiSet.addClickActions(() ->
            {
                settingsLabel.setText(guiSet.internalText.getText());

                for (GUIElement element : bonuses.children)
                {
                    if (element instanceof GUIBonus) ((GUIBonus) element).setColor(Color.AQUA);
                }
            }));
        }

        for (Bonus bonus : ServerData.bonuses.values())
        {
            GUIBonus guiBonus = new GUIBonus(this, bonus, 1, 0.5);
            bonuses.add(guiBonus.addClickActions(() ->
            {
                settingsLabel.setText(guiBonus.internalText.getText());

                for (GUIElement element : sets.children)
                {
                    if (element instanceof GUISet) ((GUISet) element).setColor(Color.AQUA);
                }
            }));
        }
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
