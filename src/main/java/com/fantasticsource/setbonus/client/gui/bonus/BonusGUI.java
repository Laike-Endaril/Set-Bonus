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
    public SetBonusData data;
    public GUIBonus clickedElement;
    public Bonus bonus;
    public GUILabeledTextInput id, name;
    public GUIStringPicker discoveryMode;
    public GUITextLabel requirementsLabel, bonusElementsLabel;
    public GUIView requirementsView, bonusElementsView;
    public GUIScrollView requirements, bonusElements;
    public GUIVerticalScrollbar requirementsScrollbar, bonusElementsScrollbar;

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
        requirementsScrollbar = new GUIVerticalScrollbar(this, ServerConfigGUI.SCROLLBAR_WIDTH, 1, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, requirements);
        requirementsView.addAll(requirements, requirementsScrollbar);

        for (ABonusRequirement requirement : bonus.requirements)
        {
            GUIBonusReq guiBonusReq = new GUIBonusReq(this, data, requirement, 1);
            guiBonusReq.addEditActions(() ->
            {
                if (guiBonusReq.requirement == null) requirements.remove(guiBonusReq);
            });
            requirements.add(guiBonusReq);
        }
        GUIBonusReq emptyDummyBonusReq = new GUIBonusReq(this, data, null, 1);
        emptyDummyBonusReq.addEditActions(() ->
        {
            if (emptyDummyBonusReq.requirement != null)
            {
                GUIBonusReq guiBonusReq = new GUIBonusReq(this, data, emptyDummyBonusReq.requirement, 1);
                guiBonusReq.addEditActions(() ->
                {
                    if (guiBonusReq.requirement == null) requirements.remove(guiBonusReq);
                });
                requirements.add(requirements.size() - 1, guiBonusReq);
                emptyDummyBonusReq.set(null);
            }
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
        bonusElementsScrollbar = new GUIVerticalScrollbar(this, ServerConfigGUI.SCROLLBAR_WIDTH, 1, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, bonusElements);
        bonusElementsView.addAll(bonusElements, bonusElementsScrollbar);

        for (ABonusElement element : bonus.bonusElements)
        {
            GUIBonusElement guiBonusElement = new GUIBonusElement(this, data, element, 1);
            guiBonusElement.addEditActions(() ->
            {
                if (guiBonusElement.bonusElement == null) bonusElements.remove(guiBonusElement);
            });
            bonusElements.add(guiBonusElement);
        }
        GUIBonusElement emptyDummyBonusElement = new GUIBonusElement(this, data, null, 1);
        emptyDummyBonusElement.addEditActions(() ->
        {
            if (emptyDummyBonusElement.bonusElement != null)
            {
                GUIBonusElement guiBonusElement = new GUIBonusElement(this, data, emptyDummyBonusElement.bonusElement, 1);
                guiBonusElement.addEditActions(() ->
                {
                    if (guiBonusElement.bonusElement == null) bonusElements.remove(guiBonusElement);
                });
                bonusElements.add(bonusElements.size() - 1, guiBonusElement);
                emptyDummyBonusElement.set(null);
            }
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

                //Requirements
                bonus.requirements.clear();
                for (GUIElement element : requirements.children)
                {
                    if (element instanceof GUIBonusReq && ((GUIBonusReq) element).requirement != null)
                    {
                        bonus.requirements.add(((GUIBonusReq) element).requirement);
                    }
                }

                //Bonus Elements
                bonus.bonusElements.clear();
                for (GUIElement element : bonusElements.children)
                {
                    if (element instanceof GUIBonusElement && ((GUIBonusElement) element).bonusElement != null)
                    {
                        bonus.bonusElements.add(((GUIBonusElement) element).bonusElement);
                        ((GUIBonusElement) element).bonusElement.bonus = bonus;
                    }
                }


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
