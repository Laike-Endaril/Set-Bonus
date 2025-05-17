package com.fantasticsource.setbonus.client.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUILine;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextLabel;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.mctools.gui.screen.YesNoGUI;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.client.gui.bonus.GUIBonus;
import com.fantasticsource.setbonus.client.gui.equip.GUIEquip;
import com.fantasticsource.setbonus.client.gui.set.GUISet;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SetRequirement;
import com.fantasticsource.tools.datastructures.Color;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class ServerConfigGUI extends GUIScreen
{
    public static final int COLUMN_COUNT = 5;
    public static final double COLUMN_WIDTH = 1d / COLUMN_COUNT, SCROLLBAR_WIDTH = 0.015, ELEMENT_SCALE = 0.5;

    public static double lineOffset = 0.125;
    public static Color[] lineColors = new Color[]{Color.YELLOW.copy().setAF(0.3f), Color.YELLOW.copy().setAF(0.3f)};

    public SetBonusData data;
    public GUITextLabel selected = null,
            mainLabel, equipsLabel, bonusesLabel, setsLabel, settingsLabel,
            linesLabel, loadLocalLabel, saveLocalLabel, loadRemoteLabel, saveRemoteLabel, loadLocalTemplateLabel, saveLocalTemplateLabel, loadRemoteTemplateLabel, saveRemoteTemplateLabel;
    public GUIScrollView main, equips, bonuses, sets, settings;
    public boolean showLines = true;
    public ArrayList<GUILine> lines = new ArrayList<>();


    public ServerConfigGUI(SetBonusData data)
    {
        this.data = data;
        show();


        //Root
        root.add(new GUIDarkenedBackground(this));
        GUINavbar navbar = new GUINavbar(this);
        root.add(navbar);


        //Main
        GUIView mainColumn = new GUIView(this, COLUMN_WIDTH, 1 - navbar.height);
        root.add(mainColumn);

        mainLabel = new GUITextLabel(this, 1, Color.GREEN).setText(reformat(MODID + ".config.main"));
        mainColumn.add(mainLabel);
        main = new GUIScrollView(this, (COLUMN_WIDTH - SCROLLBAR_WIDTH) * COLUMN_COUNT, 1 - mainLabel.height);
        GUIVerticalScrollbar mainScrollbar = new GUIVerticalScrollbar(this, 1 - main.width, main.height, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, main);
        mainColumn.addAll(main, mainScrollbar);


        //Equips
        GUIView equipsColumn = new GUIView(this, COLUMN_WIDTH, 1 - navbar.height);
        root.add(equipsColumn);

        equipsLabel = new GUITextLabel(this, 1, Color.GREEN).setText(reformat(MODID + ".config.equipment"));
        equipsColumn.add(equipsLabel);
        equips = new GUIScrollView(this, (COLUMN_WIDTH - SCROLLBAR_WIDTH) * COLUMN_COUNT, 1 - equipsLabel.height);
        GUIVerticalScrollbar equipsScrollbar = new GUIVerticalScrollbar(this, 1 - equips.width, equips.height, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, equips);
        equipsColumn.addAll(equips, equipsScrollbar);


        //Sets
        GUIView setsColumn = new GUIView(this, COLUMN_WIDTH, 1 - navbar.height);
        root.add(setsColumn);

        setsLabel = new GUITextLabel(this, 1, Color.GREEN).setText(reformat(MODID + ".config.sets"));
        setsColumn.add(setsLabel);
        sets = new GUIScrollView(this, (COLUMN_WIDTH - SCROLLBAR_WIDTH) * COLUMN_COUNT, 1 - setsLabel.height);
        GUIVerticalScrollbar setsScrollbar = new GUIVerticalScrollbar(this, 1 - sets.width, sets.height, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, sets);
        setsColumn.addAll(sets, setsScrollbar);


        //Bonuses
        GUIView bonusesColumn = new GUIView(this, COLUMN_WIDTH, 1 - navbar.height);
        root.add(bonusesColumn);

        bonusesLabel = new GUITextLabel(this, 1, Color.GREEN).setText(reformat(MODID + ".config.bonuses"));
        bonusesColumn.add(bonusesLabel);
        bonuses = new GUIScrollView(this, (COLUMN_WIDTH - SCROLLBAR_WIDTH) * COLUMN_COUNT, 1 - bonusesLabel.height);
        GUIVerticalScrollbar bonusesScrollbar = new GUIVerticalScrollbar(this, 1 - bonuses.width, bonuses.height, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, bonuses);
        bonusesColumn.addAll(bonuses, bonusesScrollbar);


        //Settings
        GUIView settingsColumn = new GUIView(this, COLUMN_WIDTH, 1 - navbar.height);
        root.add(settingsColumn);

        settingsLabel = new GUITextLabel(this, 1, Color.GREEN);
        settingsColumn.add(settingsLabel);
        settings = new GUIScrollView(this, (COLUMN_WIDTH - SCROLLBAR_WIDTH) * COLUMN_COUNT, 1 - settingsLabel.height);
        GUIVerticalScrollbar settingsScrollbar = new GUIVerticalScrollbar(this, 1 - settings.width, settings.height, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, settings);
        settingsColumn.addAll(settings, settingsScrollbar);


        //Root recalcs
        navbar.addRecalcActions(() ->
        {
            mainColumn.height = 1 - navbar.height;
            equipsColumn.height = 1 - navbar.height;
            setsColumn.height = 1 - navbar.height;
            bonusesColumn.height = 1 - navbar.height;
            settingsColumn.height = 1 - navbar.height;
        });

        //Main recalcs
        mainLabel.addRecalcActions(() ->
        {
            main.height = 1 - mainLabel.height;
            mainScrollbar.height = main.height;
        });

        //Equips recalcs
        equipsLabel.addRecalcActions(() ->
        {
            equips.height = 1 - equipsLabel.height;
            equipsScrollbar.height = equips.height;
        });

        //Sets recalcs
        setsLabel.addRecalcActions(() ->
        {
            sets.height = 1 - setsLabel.height;
            setsScrollbar.height = sets.height;
        });

        //Bonuses recalcs
        bonusesLabel.addRecalcActions(() ->
        {
            bonuses.height = 1 - bonusesLabel.height;
            bonusesScrollbar.height = bonuses.height;
        });

        //Settings recalcs
        settingsLabel.addRecalcActions(() ->
        {
            settings.height = 1 - settingsLabel.height;
            settingsScrollbar.height = settings.height;
        });


        //Populate main column
        linesLabel = new GUITextLabel(this, 1, ELEMENT_SCALE).setText(reformat(MODID + ".config.toggleLines"));
        main.addAll(linesLabel);

        //Populate other columns
        for (Equip equip : data.equipment.values()) equips.add(new GUIEquip(this, equip, 1, ELEMENT_SCALE));
        for (Set set : data.sets.values()) sets.add(new GUISet(this, set, 1, ELEMENT_SCALE));
        for (Bonus bonus : data.bonuses.values()) bonuses.add(new GUIBonus(this, bonus, 1, ELEMENT_SCALE));


        //Main column clicks
        linesLabel.addClickActions(() ->
        {
            showLines = !showLines;
            remakeLines();
        });
    }


    public void select(GUITextLabel selected)
    {
        this.selected = selected;

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

        if (selected != null)
        {
            selected.setColor(Color.PURPLE);
            settingsLabel.setText(selected.internalText.getText());
        }
        else settingsLabel.setText("");


        settings.clear();
        populateSettings(selected);


        remakeLines();
    }


    public void populateSettings(GUITextLabel label)
    {
        GUITextLabel button = new GUITextLabel(this, 1, Color.AQUA, ELEMENT_SCALE);
        button.setText(reformat(MODID + ".config.edit"));
        settings.add(button.addClickActions(label::click));

        settings.add(new GUITextSpacer(this));

        button = new GUITextLabel(this, 1, Color.RED, ELEMENT_SCALE);
        button.setText(reformat(MODID + ".config.delete"));
        settings.add(button);
        button.addClickActions(() ->
        {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            {
                //TODO delete
                System.out.println("delete");
                //TODO update gui
                System.out.println("update gui");
                if (sets.size() > 0) select((GUITextLabel) sets.get(0));
            }
            else
            {
                YesNoGUI yesNoGUI = new YesNoGUI(reformat(label.internalText.getText()), reformat(MODID + ".config.deleteThingMaybe", label.internalText.getText()));
                yesNoGUI.addOnClosedActions(() ->
                {
                    if (yesNoGUI.pressedYes)
                    {
                        //TODO delete
                        System.out.println("delete");
                        //TODO update gui
                        System.out.println("update gui");
                    }
                });
            }
        });
    }


    public void remakeLines()
    {
        for (GUILine line : lines) root.remove(line);
        lines.clear();
        if (!showLines) return;


        GUITextLabel selected = this.selected; //Necessary for runnables to work correctly
        if (selected instanceof GUIEquip)
        {
            GUIEquip guiEquip = (GUIEquip) selected;

            //Equips to sets
            LinkedHashMap<Set, GUISet> linkedSets = new LinkedHashMap<>();
            for (GUIElement element : sets.children)
            {
                if (element instanceof GUISet)
                {
                    GUISet other = (GUISet) element;
                    if (other.set.involvedEquips.containsKey(guiEquip.equip.name))
                    {
                        GUILine line = new GUILine(this, selected.absoluteX() + selected.absoluteWidth() * (1 - lineOffset), selected.absoluteY() + selected.absoluteHeight() * 0.5, other.absoluteX() + other.absoluteWidth() * lineOffset, other.absoluteY() + other.absoluteHeight() * 0.5, lineColors[0], lineColors[1], 3);
                        line.addRecalcActions(() -> line.set(selected.absoluteX() + selected.absoluteWidth() * (1 - lineOffset), selected.absoluteY() + selected.absoluteHeight() * 0.5, other.absoluteX() + other.absoluteWidth() * lineOffset, other.absoluteY() + other.absoluteHeight() * 0.5));
                        lines.add(line);
                        root.add(line);
                        linkedSets.put(other.set, other);
                    }
                }
            }

            //Sets to bonuses
            for (GUIElement element : bonuses.children)
            {
                if (element instanceof GUIBonus)
                {
                    for (ABonusRequirement requirement : ((GUIBonus) element).bonus.bonusRequirements)
                    {
                        if (requirement instanceof SetRequirement)
                        {
                            GUISet guiSet = linkedSets.get(((SetRequirement) requirement).set);
                            if (guiSet != null)
                            {
                                GUILine line = new GUILine(this, guiSet.absoluteX() + guiSet.absoluteWidth() * (1 - lineOffset), guiSet.absoluteY() + guiSet.absoluteHeight() * 0.5, element.absoluteX() + element.absoluteWidth() * lineOffset, element.absoluteY() + element.absoluteHeight() * 0.5, lineColors[0], lineColors[1], 3);
                                line.addRecalcActions(() -> line.set(guiSet.absoluteX() + guiSet.absoluteWidth() * (1 - lineOffset), guiSet.absoluteY() + guiSet.absoluteHeight() * 0.5, element.absoluteX() + element.absoluteWidth() * lineOffset, element.absoluteY() + element.absoluteHeight() * 0.5));
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
            GUIBonus guiBonus = (GUIBonus) selected;

            //Sets to bonuses
            for (GUIElement other : sets.children)
            {
                if (other instanceof GUISet)
                {
                    for (ABonusRequirement requirement : guiBonus.bonus.bonusRequirements)
                    {
                        if (requirement instanceof SetRequirement && ((SetRequirement) requirement).set == ((GUISet) other).set)
                        {
                            GUILine line = new GUILine(this, other.absoluteX() + other.absoluteWidth() * (1 - lineOffset), other.absoluteY() + other.absoluteHeight() * 0.5, selected.absoluteX() + selected.absoluteWidth() * lineOffset, selected.absoluteY() + selected.absoluteHeight() * 0.5, lineColors[0], lineColors[1], 3);
                            line.addRecalcActions(() -> line.set(other.absoluteX() + other.absoluteWidth() * (1 - lineOffset), other.absoluteY() + other.absoluteHeight() * 0.5, selected.absoluteX() + selected.absoluteWidth() * lineOffset, selected.absoluteY() + selected.absoluteHeight() * 0.5));
                            lines.add(line);
                            root.add(line);


                            //Equips to sets
                            for (GUIElement other2 : equips.children)
                            {
                                if (other2 instanceof GUIEquip && ((GUISet) other).set.involvedEquips.keySet().contains(((GUIEquip) other2).equip.name))
                                {
                                    GUILine line2 = new GUILine(this, other2.absoluteX() + other2.absoluteWidth() * (1 - lineOffset), other2.absoluteY() + other2.absoluteHeight() * 0.5, other.absoluteX() + other.absoluteWidth() * lineOffset, other.absoluteY() + other.absoluteHeight() * 0.5, lineColors[0], lineColors[1], 3);
                                    line2.addRecalcActions(() -> line2.set(other2.absoluteX() + other2.absoluteWidth() * (1 - lineOffset), other2.absoluteY() + other2.absoluteHeight() * 0.5, other.absoluteX() + other.absoluteWidth() * lineOffset, other.absoluteY() + other.absoluteHeight() * 0.5));
                                    lines.add(line2);
                                    root.add(line2);
                                }
                            }
                        }
                    }
                }
            }
        }


        else if (selected instanceof GUISet)
        {
            GUISet guiSet = (GUISet) selected;

            //Equips to sets
            for (GUIElement other : equips.children)
            {
                if (other instanceof GUIEquip && guiSet.set.involvedEquips.keySet().contains(((GUIEquip) other).equip.name))
                {
                    GUILine line = new GUILine(this, other.absoluteX() + other.absoluteWidth() * (1 - lineOffset), other.absoluteY() + other.absoluteHeight() * 0.5, selected.absoluteX() + selected.absoluteWidth() * lineOffset, selected.absoluteY() + selected.absoluteHeight() * 0.5, lineColors[0], lineColors[1], 3);
                    line.addRecalcActions(() -> line.set(other.absoluteX() + other.absoluteWidth() * (1 - lineOffset), other.absoluteY() + other.absoluteHeight() * 0.5, selected.absoluteX() + selected.absoluteWidth() * lineOffset, selected.absoluteY() + selected.absoluteHeight() * 0.5));
                    lines.add(line);
                    root.add(line);
                }
            }

            //Sets to bonuses
            for (GUIElement other : bonuses.children)
            {
                if (other instanceof GUIBonus)
                {
                    for (ABonusRequirement requirement : ((GUIBonus) other).bonus.bonusRequirements)
                    {
                        if (requirement instanceof SetRequirement && ((SetRequirement) requirement).set == guiSet.set)
                        {
                            GUILine line = new GUILine(this, selected.absoluteX() + selected.absoluteWidth() * (1 - lineOffset), selected.absoluteY() + selected.absoluteHeight() * 0.5, other.absoluteX() + other.absoluteWidth() * lineOffset, other.absoluteY() + other.absoluteHeight() * 0.5, lineColors[0], lineColors[1], 3);
                            line.addRecalcActions(() -> line.set(selected.absoluteX() + selected.absoluteWidth() * (1 - lineOffset), selected.absoluteY() + selected.absoluteHeight() * 0.5, other.absoluteX() + other.absoluteWidth() * lineOffset, other.absoluteY() + other.absoluteHeight() * 0.5));
                            lines.add(line);
                            root.add(line);
                        }
                    }
                }
            }
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
