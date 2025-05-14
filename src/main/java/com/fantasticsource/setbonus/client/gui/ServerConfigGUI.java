package com.fantasticsource.setbonus.client.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUILine;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextLabel;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.setbonus.client.gui.bonus.GUIBonus;
import com.fantasticsource.setbonus.client.gui.equip.GUIEquip;
import com.fantasticsource.setbonus.client.gui.set.GUISet;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementEnchantment;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SetRequirement;
import com.fantasticsource.setbonus.server.ServerData;
import com.fantasticsource.tools.datastructures.Color;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class ServerConfigGUI extends GUIScreen
{
    public static final Color[] LINE_COLORS = new Color[]{Color.WHITE.copy().setAF(0.3f), Color.PURPLE.copy().setAF(0.3f)};
    public static final double LINE_OFFSET = 0.125;

    public GUITextLabel equipsLabel, bonusesLabel, setsLabel, settingsLabel, detailsLabel;
    public GUIScrollView equips, bonuses, sets, settings, details;
    public ArrayList<GUILine> lines = new ArrayList<>();


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

        equipsLabel = new GUITextLabel(this, 1, Color.GREEN).setText(reformat(MODID + ".config.equipment"));
        equipsColumn.add(equipsLabel);
        equips = new GUIScrollView(this, (1d / 3 - 0.02) * 3, 1 - equipsLabel.height);
        GUIVerticalScrollbar equipsScrollbar = new GUIVerticalScrollbar(this, 1 - equips.width, equips.height, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, equips);
        equipsColumn.addAll(equips, equipsScrollbar);


        //Bonuses and sets
        GUIView bonusesAndSetsColumn = new GUIView(this, 0.25, 1 - navbar.height);
        root.add(bonusesAndSetsColumn);

        bonusesLabel = new GUITextLabel(this, 1, Color.GREEN).setText(reformat(MODID + ".config.bonuses"));
        bonusesAndSetsColumn.add(bonusesLabel);
        bonuses = new GUIScrollView(this, (1d / 3 - 0.02) * 3, 0.5 - bonusesLabel.height);
        GUIVerticalScrollbar bonusesScrollbar = new GUIVerticalScrollbar(this, 1 - bonuses.width, bonuses.height, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, bonuses);
        bonusesAndSetsColumn.addAll(bonuses, bonusesScrollbar);

        setsLabel = new GUITextLabel(this, 1, Color.GREEN).setText(reformat(MODID + ".config.sets"));
        bonusesAndSetsColumn.add(setsLabel);
        sets = new GUIScrollView(this, (1d / 3 - 0.02) * 3, 1 - setsLabel.y - setsLabel.height);
        GUIVerticalScrollbar setsScrollbar = new GUIVerticalScrollbar(this, 1 - sets.width, sets.height, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, sets);
        bonusesAndSetsColumn.addAll(sets, setsScrollbar);


        //Settings
        GUIView settingsColumn = new GUIView(this, 0.25, 1 - navbar.height);
        root.add(settingsColumn);

        settingsLabel = new GUITextLabel(this, 1, Color.GREEN);
        settingsColumn.add(settingsLabel);
        settings = new GUIScrollView(this, (1d / 3 - 0.02) * 3, 1 - settingsLabel.height);
        GUIVerticalScrollbar settingsScrollbar = new GUIVerticalScrollbar(this, 1 - settings.width, settings.height, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, settings);
        settingsColumn.addAll(settings, settingsScrollbar);


        //Details
        GUIView detailsColumn = new GUIView(this, 0.25, 1 - navbar.height);
        root.add(detailsColumn);

        detailsLabel = new GUITextLabel(this, 1, Color.GREEN); //TODO change label to entry selected from center column
        detailsColumn.add(detailsLabel);
        details = new GUIScrollView(this, (1d / 3 - 0.02) * 3, 1 - detailsLabel.height);
        GUIVerticalScrollbar detailsScrollbar = new GUIVerticalScrollbar(this, 1 - details.width, details.height, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, details);
        detailsColumn.addAll(details, detailsScrollbar);


        //Main recalcs
        navbar.addRecalcActions(() ->
        {
            equipsColumn.height = 1 - navbar.height;
            bonusesAndSetsColumn.height = 1 - navbar.height;
            settingsColumn.height = 1 - navbar.height;
            detailsColumn.height = 1 - navbar.height;
        });

        //Equips recalcs
        equipsLabel.addRecalcActions(() ->
        {
            equips.height = 1 - equipsLabel.height;
            equipsScrollbar.height = equips.height;
        });

        //Bonuses and sets recalcs
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

        //Settings recalcs
        settingsLabel.addRecalcActions(() ->
        {
            settings.height = 1 - settingsLabel.height;
            settingsScrollbar.height = settings.height;
        });

        //Details recalcs
        detailsLabel.addRecalcActions(() ->
        {
            details.height = 1 - detailsLabel.height;
            detailsScrollbar.height = details.height;
        });


        for (Equip equip : ServerData.equipment.values())
        {
            GUIEquip guiEquip = new GUIEquip(this, equip, 1, 0.5);
            equips.add(guiEquip.addClickActions(() -> settingsLabel.setText(guiEquip.internalText.getText())));
        }

        for (Set set : ServerData.sets.values())
        {
            GUISet guiSet = new GUISet(this, set, 1, 0.5);
            sets.add(guiSet.addClickActions(() -> settingsLabel.setText(guiSet.internalText.getText())));
        }

        for (Bonus bonus : ServerData.bonuses.values())
        {
            GUIBonus guiBonus = new GUIBonus(this, bonus, 1, 0.5);
            bonuses.add(guiBonus.addClickActions(() -> settingsLabel.setText(guiBonus.internalText.getText())));
        }
    }


    public void select(GUITextLabel selected)
    {
        for (GUIElement element : equips.children)
        {
            if (element instanceof GUIEquip) ((GUIEquip) element).setColor(Color.AQUA);
        }
        for (GUIElement element : bonuses.children)
        {
            if (element instanceof GUIBonus) ((GUIBonus) element).setColor(Color.AQUA);
        }
        for (GUIElement element : sets.children)
        {
            if (element instanceof GUISet) ((GUISet) element).setColor(Color.AQUA);
        }

        selected.setColor(Color.PURPLE);


        for (GUILine line : lines) root.remove(line);
        lines.clear();

        if (selected instanceof GUIEquip)
        {
            GUIEquip guiEquip = (GUIEquip) selected;

            LinkedHashMap<Set, GUISet> linkedSets = new LinkedHashMap<>();
            for (GUIElement element : sets.children)
            {
                if (element instanceof GUISet)
                {
                    //Primary connections
                    GUISet other = (GUISet) element;
                    if (other.set.involvedEquips.containsKey(guiEquip.equip.name))
                    {
                        GUILine line = new GUILine(this, selected.absoluteX() + selected.absoluteWidth() * (1 - LINE_OFFSET), selected.absoluteY() + selected.absoluteHeight() * 0.5, other.absoluteX() + other.absoluteWidth() * LINE_OFFSET, other.absoluteY() + other.absoluteHeight() * 0.5, LINE_COLORS[0], LINE_COLORS[1], 3);
                        line.addRecalcActions(() -> line.set(selected.absoluteX() + selected.absoluteWidth() * (1 - LINE_OFFSET), selected.absoluteY() + selected.absoluteHeight() * 0.5, other.absoluteX() + other.absoluteWidth() * LINE_OFFSET, other.absoluteY() + other.absoluteHeight() * 0.5));
                        lines.add(line);
                        root.add(line);
                        linkedSets.put(other.set, other);
                    }
                }
            }

            for (GUIElement element : bonuses.children)
            {
                if (element instanceof GUIBonus)
                {
                    //Primary connections
                    GUIBonus other = (GUIBonus) element;
                    for (ABonusElement bonusElement : other.bonus.bonusElements)
                    {
                        if (bonusElement instanceof BonusElementEnchantment)
                        {
                            if (((BonusElementEnchantment) bonusElement).slotDataToEnchant.involvedEquips.contains(guiEquip.equip))
                            {
                                GUILine line = new GUILine(this, selected.absoluteX() + selected.absoluteWidth() * (1 - LINE_OFFSET), selected.absoluteY() + selected.absoluteHeight() * 0.5, other.absoluteX() + other.absoluteWidth() * LINE_OFFSET, other.absoluteY() + other.absoluteHeight() * 0.5, LINE_COLORS[0], LINE_COLORS[1], 3);
                                line.addRecalcActions(() -> line.set(selected.absoluteX() + selected.absoluteWidth() * (1 - LINE_OFFSET), selected.absoluteY() + selected.absoluteHeight() * 0.5, other.absoluteX() + other.absoluteWidth() * LINE_OFFSET, other.absoluteY() + other.absoluteHeight() * 0.5));
                                lines.add(line);
                                root.add(line);
                                break;
                            }
                        }
                    }

                    //Secondary connections
                    for (ABonusRequirement requirement : other.bonus.bonusRequirements)
                    {
                        if (requirement instanceof SetRequirement)
                        {
                            GUISet guiSet = linkedSets.get(((SetRequirement) requirement).set);
                            if (guiSet != null)
                            {
                                GUILine line = new GUILine(this, guiSet.absoluteX() + guiSet.absoluteWidth() * (1 - LINE_OFFSET), guiSet.absoluteY() + guiSet.absoluteHeight() * 0.5, other.absoluteX() + other.absoluteWidth() * LINE_OFFSET, other.absoluteY() + other.absoluteHeight() * 0.5, LINE_COLORS[0], LINE_COLORS[1], 3);
                                line.addRecalcActions(() -> line.set(guiSet.absoluteX() + guiSet.absoluteWidth() * (1 - LINE_OFFSET), guiSet.absoluteY() + guiSet.absoluteHeight() * 0.5, other.absoluteX() + other.absoluteWidth() * LINE_OFFSET, other.absoluteY() + other.absoluteHeight() * 0.5));
                                lines.add(line);
                                root.add(line);
                            }
                        }
                    }
                }
            }
        }
        else if (selected instanceof GUIBonus)
        {

        }
        else if (selected instanceof GUISet)
        {

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
