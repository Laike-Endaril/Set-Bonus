package com.fantasticsource.setbonus.client.gui.bonus;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterBlacklist;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.client.gui.ServerConfigGUI;
import com.fantasticsource.setbonus.client.gui.bonus.element.GUIBonusElement;
import com.fantasticsource.setbonus.client.gui.bonus.requirement.GUIBonusReq;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;

import java.util.ArrayList;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class BonusGUI extends GUIScreen
{
    SetBonusData data;
    public GUIBonus clickedElement;
    public Bonus bonus;
    public GUILabeledTextInput id, name;
    GUIStringPicker discoveryMode;
    GUITextLabel requirementsLabel, bonusElementsLabel;
    GUIView requirementsView, bonusElementsView;
    GUIScrollView requirements, bonusElements;
    GUIVerticalScrollbar requirementsScrollbar, bonusElementsScrollbar;

    public BonusGUI(SetBonusData data, GUIBonus clickedElement)
    {
        this.data = data;
        this.clickedElement = clickedElement;
        bonus = clickedElement.bonus;


        show();


        //Root
        root.add(new GUIDarkenedBackground(this));
        GUINavbar navbar = new GUINavbar(this);
        root.add(navbar);


        //Save, load, etc
        GUITextButton save = new GUITextButton(this, reformat(MODID + ".config.save"), Color.GREEN);
        root.add(save);
        root.add(new GUITextButton(this, reformat(MODID + ".config.cancel"), Color.ORANGE).addClickActions(this::close));
        root.add(new GUITextSpacer(this));


        //ID
        ArrayList<String> idBlacklist = new ArrayList<>();
        idBlacklist.add("");
        for (GUIElement element : clickedElement.parent.children)
        {
            if (element == clickedElement) continue;
            if (element instanceof GUIBonus) idBlacklist.add(((GUIBonus) element).bonus.id);
        }
        id = new GUILabeledTextInput(this, reformat(MODID + ".config.id") + ": ", bonus.id, new FilterBlacklist(idBlacklist.toArray(new String[0])));
        root.addAll(id, new GUIElement(this, 1, 0));


        //Name
        name = new GUILabeledTextInput(this, reformat(MODID + ".config.name") + ": ", bonus.name, FilterNotEmpty.INSTANCE);
        root.addAll(name, new GUIElement(this, 1, 0));


        //Discovery Mode
        discoveryMode = new GUIStringPicker(this, reformat(MODID + ".config.discoveryMode"),
                reformat(MODID + ".config.discoveryMode.onActivation"),
                reformat(MODID + ".config.discoveryMode.alwaysVisible"),
                reformat(MODID + ".config.discoveryMode.alwaysHidden")
        );
        discoveryMode.set(discoveryMode.possibleValues[bonus.discoveryMode]);
        root.addAll(discoveryMode, new GUIElement(this, 1, 0));


        //Section Separator
        root.add(new GUITextSpacer(this));


        //Requirements
        requirementsLabel = new GUITextLabel(this, 1, Color.GREEN);
        requirementsLabel.setText(reformat(MODID + ".config.requirements"));
        root.add(requirementsLabel);

        requirementsView = new GUIView(this, 1, (1 - requirementsLabel.y - requirementsLabel.height * 2) * 0.5);
        requirementsLabel.addRecalcActions(() -> requirementsView.height = (1 - requirementsLabel.y - requirementsLabel.height * 2) * 0.5);
        root.add(requirementsView);

        requirements = new GUIScrollView(this, 1 - ServerConfigGUI.SCROLLBAR_WIDTH, 1);
        requirementsScrollbar = new GUIVerticalScrollbar(this, ServerConfigGUI.SCROLLBAR_WIDTH, 1, Color.AQUA, Color.BLANK, Color.AQUA, Color.BLANK, requirements);
        requirementsView.addAll(requirements, requirementsScrollbar);

        for (ABonusRequirement requirement : bonus.bonusRequirements)
        {
            requirements.add(new GUIBonusReq(this, data, requirement, 1));
        }
        GUIBonusReq emptyDummyBonusReq = new GUIBonusReq(this, data, null, 1);
        emptyDummyBonusReq.addEditActions(() ->
        {
            requirements.add(requirements.size() - 1, new GUIBonusReq(this, data, emptyDummyBonusReq.requirement, 1));
            emptyDummyBonusReq.set(null);
        });
        requirements.add(emptyDummyBonusReq);


        //Bonus Elements
        bonusElementsLabel = new GUITextLabel(this, 1, Color.GREEN);
        bonusElementsLabel.setText(reformat(MODID + ".config.bonusElements"));
        root.add(bonusElementsLabel);

        bonusElementsView = new GUIView(this, 1, 1 - bonusElementsLabel.y - bonusElementsLabel.height);
        bonusElementsLabel.addRecalcActions(() -> bonusElementsView.height = 1 - bonusElementsLabel.y - bonusElementsLabel.height);
        root.add(bonusElementsView);

        bonusElements = new GUIScrollView(this, 1 - ServerConfigGUI.SCROLLBAR_WIDTH, 1);
        bonusElementsScrollbar = new GUIVerticalScrollbar(this, ServerConfigGUI.SCROLLBAR_WIDTH, 1, Color.AQUA, Color.BLANK, Color.AQUA, Color.BLANK, bonusElements);
        bonusElementsView.addAll(bonusElements, bonusElementsScrollbar);

        for (ABonusElement element : bonus.bonusElements)
        {
            bonusElements.add(new GUIBonusElement(this, data, element, 1));
        }
        GUIBonusElement emptyDummyBonusElement = new GUIBonusElement(this, data, null, 1);
        emptyDummyBonusElement.addEditActions(() ->
        {
            bonusElements.add(bonusElements.size() - 1, new GUIBonusElement(this, data, emptyDummyBonusElement.element, 1));
            emptyDummyBonusElement.set(null);
        });
        bonusElements.add(emptyDummyBonusElement);


        //Save actions
        save.addClickActions(() ->
        {
            if (!id.valid())
            {
                id.setText(bonus.id);
                id.label.click();
            }
            else if (!name.valid())
            {
                name.setText(bonus.name);
                name.label.click();
            }
            else
            {
                //ID, name, discovery mode
                bonus.id = id.getText();
                bonus.name = name.getText();
                bonus.discoveryMode = Tools.indexOf(discoveryMode.possibleValues, discoveryMode.value);


                //Update GUI
                clickedElement.set(bonus);
                close();
            }
        });
    }

    @Override
    public String title()
    {
        return reformat(bonus.id) + " (" + reformat(bonus.name) + ")";
    }
}
