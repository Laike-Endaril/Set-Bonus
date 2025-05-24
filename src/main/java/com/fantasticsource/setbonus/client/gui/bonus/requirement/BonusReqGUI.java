package com.fantasticsource.setbonus.client.gui.bonus.requirement;

import com.fantasticsource.mctools.DoubleRequirement;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterFloat;
import com.fantasticsource.mctools.gui.element.text.filter.FilterInt;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import com.fantasticsource.setbonus.common.bonusrequirements.AttributeRequirement;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SetRequirement;
import com.fantasticsource.tools.datastructures.Color;
import com.fantasticsource.tools.datastructures.Pair;

import java.util.LinkedHashMap;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class BonusReqGUI extends GUIScreen
{
    public SetBonusData data;
    public GUIBonusReq clickedElement;
    public ABonusRequirement requirement;

    public BonusReqGUI(SetBonusData data, GUIBonusReq clickedElement)
    {
        this.data = data;
        this.clickedElement = clickedElement;
        requirement = clickedElement.requirement;


        show();


        //Root
        root.add(new GUIDarkenedBackground(this));
        GUINavbar navbar = new GUINavbar(this);
        root.add(navbar);


        //Save, load, etc
        GUITextButton save = new GUITextButton(this, reformat(MODID + ".config.save"), Color.GREEN);
        root.add(save);
        root.add(new GUITextButton(this, reformat(MODID + ".config.cancel"), Color.ORANGE).addClickActions(this::close));
        root.add(new GUITextButton(this, reformat(MODID + ".config.delete"), Color.RED).addClickActions(() ->
        {
            clickedElement.set(null);
            close();
        }));
        root.add(new GUITextSpacer(this));


        //Type
        LinkedHashMap<Class<? extends ABonusRequirement>, String> validTypes = new LinkedHashMap<>();
        validTypes.put(SetRequirement.class, reformat(MODID + ".config.set"));
        validTypes.put(AttributeRequirement.class, reformat(MODID + ".config.attribute"));
        GUIStringPicker type = new GUIStringPicker(this, reformat(MODID + ".config.type"), validTypes.values().toArray(new String[0]));
        root.add(type);
        GUITextSpacer spacer = new GUITextSpacer(this);
        root.add(spacer);


        //Type-Specific Settings
        GUIView typeSettings = new GUIView(this, 1, 1 - spacer.y - spacer.height);
        spacer.addRecalcActions(() -> typeSettings.height = 1 - spacer.y - spacer.height);
        root.add(typeSettings);


        //Type selection actions
        type.addEditActions(() ->
        {
            if (type.value.equals(reformat(MODID + ".config.set")))
            {
                if (data.sets.size() > 0)
                {
                    typeSettings.clear();

                    SetRequirement setRequirement = requirement instanceof SetRequirement ? ((SetRequirement) requirement).clone(data) : new SetRequirement(data.sets.iterator().next(), -1);
                    requirement = setRequirement;

                    String[] setIDs = new String[data.sets.size()];
                    int i = 0;
                    for (Set set : data.sets) setIDs[i++] = set.id;
                    GUIStringPicker setPicker = new GUIStringPicker(this, reformat(MODID + ".config.set"), setIDs);
                    setPicker.addEditActions(() ->
                    {
                        for (Set set : data.sets)
                        {
                            if (set.id.equals(setPicker.value))
                            {
                                setRequirement.set = set;
                                break;
                            }
                        }
                    });
                    typeSettings.add(setPicker);
                    typeSettings.add(new GUIElement(this, 1, 0));

                    typeSettings.add(new GUILabeledTextInput(this, reformat(MODID + ".config.numberOfSetEquips") + ": ", "" + setRequirement.num, FilterInt.INSTANCE));
                }
            }
            else if (type.value.equals(reformat(MODID + ".config.attribute")))
            {
                typeSettings.clear();

                AttributeRequirement attributeRequirement = requirement instanceof AttributeRequirement ? ((AttributeRequirement) requirement).clone(data) : new AttributeRequirement(new Pair<>("generic.armorToughness", new DoubleRequirement(2)));
                requirement = attributeRequirement;

                typeSettings.add(new GUILabeledTextInput(this, reformat(MODID + ".config.attributeName") + ": ", attributeRequirement.attributeName, FilterNotEmpty.INSTANCE));
                typeSettings.add(new GUIElement(this, 1, 0));

                GUIStringPicker modePicker = new GUIStringPicker(this, reformat(MODID + ".config.comparator"), DoubleRequirement.VALID_MODE_STRINGS.toArray(new String[0]));
                modePicker.addEditActions(() ->
                {
                    int index = DoubleRequirement.VALID_MODE_STRINGS.indexOf(modePicker.value);
                    if (index != -1) attributeRequirement.requirement.mode = index;
                });
                modePicker.set(attributeRequirement.requirement.getModeString());
                typeSettings.add(modePicker);
                typeSettings.add(new GUIElement(this, 1, 0));

                typeSettings.add(new GUILabeledTextInput(this, reformat(MODID + ".config.amount") + ": ", "" + attributeRequirement.requirement.amount, FilterFloat.INSTANCE));
            }
        });


        //Populate
        if (requirement instanceof AttributeRequirement) type.set(reformat(MODID + ".config.attribute"));
        else type.runEditActions();


        //Save actions
        save.addClickActions(() ->
        {
            if (requirement instanceof SetRequirement)
            {
                GUILabeledTextInput number = (GUILabeledTextInput) typeSettings.get(2);
                if (!number.valid())
                {
                    number.setText("-1");
                    number.label.click();
                }
                else
                {
                    ((SetRequirement) requirement).num = FilterInt.INSTANCE.parse(number.getText());
                    clickedElement.set(requirement);
                    close();
                }
            }
            else if (requirement instanceof AttributeRequirement)
            {
                GUILabeledTextInput
                        attributeName = (GUILabeledTextInput) typeSettings.get(0),
                        amount = (GUILabeledTextInput) typeSettings.get(4);
                if (!attributeName.valid())
                {
                    attributeName.setText("generic.armorToughness");
                    attributeName.label.click();
                }
                else if (!amount.valid())
                {
                    amount.setText("2");
                    amount.label.click();
                }
                else
                {
                    AttributeRequirement req = (AttributeRequirement) requirement;
                    req.attributeName = attributeName.getText();
                    req.requirement.amount = FilterFloat.INSTANCE.parse(amount.getText());
                    clickedElement.set(requirement);
                    close();
                }
            }
        });
    }

    @Override
    public String title()
    {
        return reformat(MODID + ".config.requirement");
    }
}
