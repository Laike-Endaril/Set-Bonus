package com.fantasticsource.setbonus.client.gui.equip;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterBlacklist;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNone;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.client.gui.ServerConfigGUI;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.tools.datastructures.Color;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class EquipGUI extends GUIScreen
{
    public SetBonusData data;
    public GUIEquip clickedElement;
    public Equip equip;
    public GUILabeledTextInput id, domain, item, meta;
    public GUITextLabel requiredNBTLabel, disallowedNBTLabel;
    public GUIView requiredNBTView, disallowedNBTView;
    public GUIScrollView requiredNBT, disallowedNBT;
    public GUIVerticalScrollbar requiredNBTScrollbar, disallowedNBTScrollbar;


    public EquipGUI(SetBonusData data, GUIEquip clickedElement)
    {
        this.data = data;
        this.clickedElement = clickedElement;
        equip = clickedElement.equip;


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
            if (element instanceof GUIEquip) idBlacklist.add(((GUIEquip) element).equip.id);
        }
        id = new GUILabeledTextInput(this, reformat(MODID + ".config.id") + ": ", equip.id, new FilterBlacklist(idBlacklist.toArray(new String[0])));
        root.addAll(id, new GUIElement(this, 1, 0));


        //Domain
        domain = new GUILabeledTextInput(this, reformat(MODID + ".config.domain") + ": ", equip.filter.getDomainRegex(), FilterNone.INSTANCE);
        root.addAll(domain, new GUIElement(this, 1, 0));

        //Item
        item = new GUILabeledTextInput(this, reformat(MODID + ".config.item") + ": ", equip.filter.getItemRegex(), FilterNotEmpty.INSTANCE);
        root.addAll(item, new GUIElement(this, 1, 0));

        //Meta
        meta = new GUILabeledTextInput(this, reformat(MODID + ".config.meta") + ": ", equip.filter.getMetaRegex(), FilterNone.INSTANCE);
        root.addAll(meta, new GUIElement(this, 1, 0));


        //Section Separator
        root.add(new GUITextSpacer(this));


        //Required NBT
        requiredNBTLabel = new GUITextLabel(this, 1, Color.GREEN);
        requiredNBTLabel.setText(reformat(MODID + ".config.requiredNBT"));
        root.add(requiredNBTLabel);

        requiredNBTView = new GUIView(this, 1, (1 - requiredNBTLabel.y - requiredNBTLabel.height * 2) * 0.5);
        requiredNBTLabel.addRecalcActions(() -> requiredNBTView.height = (1 - requiredNBTLabel.y - requiredNBTLabel.height * 2) * 0.5);
        root.add(requiredNBTView);

        requiredNBT = new GUIScrollView(this, 1 - ServerConfigGUI.SCROLLBAR_WIDTH, 1);
        requiredNBTScrollbar = new GUIVerticalScrollbar(this, ServerConfigGUI.SCROLLBAR_WIDTH, 1, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, requiredNBT);
        requiredNBTView.addAll(requiredNBT, requiredNBTScrollbar);

        GUIAutocroppedView view;
        for (Map.Entry<String, String> entry : equip.filter.getTagsRequired().entrySet())
        {
            view = new GUIAutocroppedView(this);
            view.add(new GUIElement(this, 1, 0));
            view.add(new GUILabeledTextInput(this, reformat(MODID + ".config.nbt") + ": ", entry.getKey(), FilterNotEmpty.INSTANCE));
            view.add(new GUILabeledTextInput(this, 0.5, 0, reformat(MODID + ".config.is") + ": ", entry.getValue(), FilterNotEmpty.INSTANCE));
            requiredNBT.add(view);
        }
        view = new GUIAutocroppedView(this);
        view.add(new GUIElement(this, 1, 0));
        view.add(new GUILabeledTextInput(this, reformat(MODID + ".config.nbt") + ": ", "", FilterNotEmpty.INSTANCE));
        view.add(new GUILabeledTextInput(this, 0.5, 0, reformat(MODID + ".config.is") + ": ", "", FilterNotEmpty.INSTANCE));
        requiredNBT.add(view);


        //Disallowed NBT
        disallowedNBTLabel = new GUITextLabel(this, 1, Color.RED);
        disallowedNBTLabel.setText(reformat(MODID + ".config.disallowedNBT"));
        root.add(disallowedNBTLabel);

        disallowedNBTView = new GUIView(this, 1, 1 - disallowedNBTLabel.y - disallowedNBTLabel.height);
        disallowedNBTView.addRecalcActions(() -> disallowedNBTView.height = 1 - disallowedNBTLabel.y - disallowedNBTLabel.height);
        root.add(disallowedNBTView);

        disallowedNBT = new GUIScrollView(this, 1 - ServerConfigGUI.SCROLLBAR_WIDTH, 1);
        disallowedNBTScrollbar = new GUIVerticalScrollbar(this, ServerConfigGUI.SCROLLBAR_WIDTH, 1, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, disallowedNBT);
        disallowedNBTView.addAll(disallowedNBT, disallowedNBTScrollbar);

        for (Map.Entry<String, String> entry : equip.filter.getTagsDisallowed().entrySet())
        {
            view = new GUIAutocroppedView(this);
            view.add(new GUIElement(this, 1, 0));
            view.add(new GUILabeledTextInput(this, reformat(MODID + ".config.nbt") + ": ", entry.getKey(), FilterNotEmpty.INSTANCE));
            view.add(new GUILabeledTextInput(this, 0.5, 0, reformat(MODID + ".config.not") + ": ", entry.getValue(), FilterNotEmpty.INSTANCE));
            disallowedNBT.add(view);
        }
        view = new GUIAutocroppedView(this);
        view.add(new GUIElement(this, 1, 0));
        view.add(new GUILabeledTextInput(this, reformat(MODID + ".config.nbt") + ": ", "", FilterNotEmpty.INSTANCE));
        view.add(new GUILabeledTextInput(this, 0.5, 0, reformat(MODID + ".config.not") + ": ", "", FilterNotEmpty.INSTANCE));
        disallowedNBT.add(view);


        //Remove empty NBT entries in middle of list, add empty at end of list
        root.addClickActions(this::updateNBTLists);
        requiredNBT.addRecalcActions(this::updateNBTLists);
        disallowedNBT.addRecalcActions(this::updateNBTLists);


        //Save actions
        save.addClickActions(() ->
        {
            if (!id.valid())
            {
                id.setText(equip.id);
                id.label.click();
            }
            else if (!item.valid())
            {
                item.setText(equip.filter.getItemRegex());
                item.label.click();
            }
            else
            {
                GUILabeledTextInput nbtKey, nbtValue;

                LinkedHashMap<String, String> requiredNBTStrings = new LinkedHashMap<>();
                for (GUIElement element : requiredNBT.children)
                {
                    nbtKey = (GUILabeledTextInput) element.children.get(1);
                    nbtValue = (GUILabeledTextInput) element.children.get(2);
                    if (nbtKey.getText().equals("") && nbtValue.getText().equals("")) continue;
                    if (!nbtKey.valid())
                    {
                        nbtKey.label.click();
                        return;
                    }
                    if (!nbtValue.valid())
                    {
                        nbtValue.label.click();
                        return;
                    }
                    requiredNBTStrings.put(nbtKey.getText(), nbtValue.getText());
                }

                LinkedHashMap<String, String> disallowedNBTStrings = new LinkedHashMap<>();
                for (GUIElement element : disallowedNBT.children)
                {
                    nbtKey = (GUILabeledTextInput) element.children.get(1);
                    nbtValue = (GUILabeledTextInput) element.children.get(2);
                    if (nbtKey.getText().equals("") && nbtValue.getText().equals("")) continue;
                    if (!nbtKey.valid())
                    {
                        nbtKey.label.click();
                        return;
                    }
                    if (!nbtValue.valid())
                    {
                        nbtValue.label.click();
                        return;
                    }
                    disallowedNBTStrings.put(nbtKey.getText(), nbtValue.getText());
                }


                //Update equip
                equip.id = id.getText();
                equip.filter.set(domain.getText(), item.getText(), meta.getText(), requiredNBTStrings, disallowedNBTStrings);


                //Update GUI
                clickedElement.set(equip);
                close();
            }
        });
    }


    protected void updateNBTLists()
    {
        GUIAutocroppedView entry;
        GUILabeledTextInput key, value;
        for (int i = 0; i < requiredNBT.size() - 1; i++)
        {
            entry = (GUIAutocroppedView) requiredNBT.get(i);
            key = (GUILabeledTextInput) entry.get(1);
            value = (GUILabeledTextInput) entry.get(2);
            if (!key.input.isActive() && !value.input.isActive() && key.getText().equals("") && value.getText().equals(""))
            {
                requiredNBT.remove(entry);
                i--;
            }
        }
        entry = (GUIAutocroppedView) requiredNBT.get(requiredNBT.size() - 1);
        key = (GUILabeledTextInput) entry.get(1);
        value = (GUILabeledTextInput) entry.get(2);
        if (!key.getText().equals("") || !value.getText().equals(""))
        {
            GUIAutocroppedView view2 = new GUIAutocroppedView(this);
            view2.add(new GUIElement(this, 1, 0));
            view2.add(new GUILabeledTextInput(this, reformat(MODID + ".config.nbt"), "", FilterNotEmpty.INSTANCE));
            view2.add(new GUILabeledTextInput(this, 0.5, 0, reformat(MODID + ".config.is"), "", FilterNotEmpty.INSTANCE));
            requiredNBT.add(view2);
        }


        for (int i = 0; i < disallowedNBT.size() - 1; i++)
        {
            entry = (GUIAutocroppedView) disallowedNBT.get(i);
            key = (GUILabeledTextInput) entry.get(1);
            value = (GUILabeledTextInput) entry.get(2);
            if (!key.input.isActive() && !value.input.isActive() && key.getText().equals("") && value.getText().equals(""))
            {
                disallowedNBT.remove(entry);
                i--;
            }
        }
        entry = (GUIAutocroppedView) disallowedNBT.get(disallowedNBT.size() - 1);
        key = (GUILabeledTextInput) entry.get(1);
        value = (GUILabeledTextInput) entry.get(2);
        if (!key.getText().equals("") || !value.getText().equals(""))
        {
            GUIAutocroppedView view2 = new GUIAutocroppedView(this);
            view2.add(new GUIElement(this, 1, 0));
            view2.add(new GUILabeledTextInput(this, reformat(MODID + ".config.nbt"), "", FilterNotEmpty.INSTANCE));
            view2.add(new GUILabeledTextInput(this, 0.5, 0, reformat(MODID + ".config.is"), "", FilterNotEmpty.INSTANCE));
            disallowedNBT.add(view2);
        }
    }


    @Override
    public String title()
    {
        return reformat(equip.id);
    }
}
