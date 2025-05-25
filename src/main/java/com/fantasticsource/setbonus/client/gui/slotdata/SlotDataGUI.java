package com.fantasticsource.setbonus.client.gui.slotdata;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextLabel;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.mctools.gui.screen.TextSelectionGUI;
import com.fantasticsource.mctools.gui.screen.YesNoGUI;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.client.gui.ServerConfigGUI;
import com.fantasticsource.setbonus.client.gui.set.SetGUI;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SlotData;
import com.fantasticsource.tools.datastructures.Color;

import java.util.Iterator;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class SlotDataGUI extends GUIScreen
{
    public SetBonusData data;
    public GUISlotData clickedElement;
    public SlotData slotData;
    public GUITextLabel validSlotsLabel, validEquipsLabel;
    public GUIView validSlotsView, validEquipsView;
    public GUIScrollView validSlots, validEquips;
    public GUIVerticalScrollbar validSlotsScrollbar, validEquipsScrollbar;


    public SlotDataGUI(SetBonusData data, GUISlotData clickedElement)
    {
        this.data = data;
        this.clickedElement = clickedElement;
        slotData = clickedElement.slotData;


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
            if (clickedElement.screen instanceof SetGUI)
            {
                YesNoGUI yesNoGUI = new YesNoGUI("", reformat(MODID + ".config.deleteThingMaybe", clickedElement.internalText.getText()));
                yesNoGUI.addPostClosedActions(() ->
                {
                    if (yesNoGUI.pressedYes)
                    {
                        clickedElement.parent.remove(clickedElement);
                        close();
                    }
                });
            }
            else
            {
                clickedElement.slotData = SlotData.getEmpty();
                close();
            }
        }));
        root.add(new GUITextSpacer(this));


        //Valid Slots
        validSlotsLabel = new GUITextLabel(this, 1, Color.GREEN);
        validSlotsLabel.setText(reformat(MODID + ".config.validSlots"));
        root.add(validSlotsLabel);

        validSlotsView = new GUIView(this, 1, (1 - validSlotsLabel.y - validSlotsLabel.height * 2) * 0.5);
        validSlotsLabel.addRecalcActions(() -> validSlotsView.height = (1 - validSlotsLabel.y - validSlotsLabel.height * 2) * 0.5);
        root.add(validSlotsView);

        validSlots = new GUIScrollView(this, 1 - ServerConfigGUI.SCROLLBAR_WIDTH, 1);
        validSlotsScrollbar = new GUIVerticalScrollbar(this, ServerConfigGUI.SCROLLBAR_WIDTH, 1, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, validSlots);
        validSlotsView.addAll(validSlots, validSlotsScrollbar);

        for (String slotName : slotData.slotNames)
        {
            GUITextLabel label = new GUITextLabel(this, 1).setText(slotName);
            label.addClickActions(() ->
            {
                YesNoGUI yesNoGUI = new YesNoGUI("", reformat(MODID + ".config.deleteThingMaybe", label.internalText.getText()));
                yesNoGUI.addPostClosedActions(() ->
                {
                    if (yesNoGUI.pressedYes) validSlots.remove(label);
                });
            });
            validSlots.add(label);
        }
        GUITextLabel emptyDummySlot = new GUITextLabel(this, 1);
        emptyDummySlot.addClickActions(() ->
        {
            GUITextLabel label = new GUITextLabel(this, 1);
            TextSelectionGUI gui = new TextSelectionGUI(label.internalText, reformat(MODID + ".config.selectSlot"), SlotData.VALID_SLOT_NAMES);
            gui.addPostClosedActions(() ->
            {
                if (!label.internalText.getText().equals(""))
                {
                    label.addClickActions(() ->
                    {
                        YesNoGUI yesNoGUI = new YesNoGUI("", reformat(MODID + ".config.deleteThingMaybe", label.internalText.getText()));
                        yesNoGUI.addPostClosedActions(() ->
                        {
                            if (yesNoGUI.pressedYes) validSlots.remove(label);
                        });
                    });
                    validSlots.add(validSlots.size() - 1, label);
                }
            });
        });
        validSlots.add(emptyDummySlot);


        //Valid Equips
        validEquipsLabel = new GUITextLabel(this, 1, Color.GREEN);
        validEquipsLabel.setText(reformat(MODID + ".config.validEquips"));
        root.add(validEquipsLabel);

        validEquipsView = new GUIView(this, 1, 1 - validEquipsLabel.y - validEquipsLabel.height);
        validEquipsView.addRecalcActions(() -> validEquipsView.height = 1 - validEquipsLabel.y - validEquipsLabel.height);
        root.add(validEquipsView);

        validEquips = new GUIScrollView(this, 1 - ServerConfigGUI.SCROLLBAR_WIDTH, 1);
        validEquipsScrollbar = new GUIVerticalScrollbar(this, ServerConfigGUI.SCROLLBAR_WIDTH, 1, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, validEquips);
        validEquipsView.addAll(validEquips, validEquipsScrollbar);

        for (Equip equip : slotData.involvedEquips)
        {
            GUITextLabel label = new GUITextLabel(this, 1).setText(reformat(equip.id));
            label.addClickActions(() ->
            {
                YesNoGUI yesNoGUI = new YesNoGUI("", reformat(MODID + ".config.deleteThingMaybe", label.internalText.getText()));
                yesNoGUI.addPostClosedActions(() ->
                {
                    if (yesNoGUI.pressedYes) validEquips.remove(label);
                });
            });
            validEquips.add(label);
        }
        GUITextLabel emptyDummyEquip = new GUITextLabel(this, 1);
        emptyDummyEquip.addClickActions(() ->
        {
            GUITextLabel label = new GUITextLabel(this, 1);
            String[] equipIDs = new String[data.equipment.size()];
            int i = 0;
            Iterator<Equip> iterator = data.equipment.iterator();
            while (iterator.hasNext())
            {
                equipIDs[i++] = iterator.next().id;
            }
            TextSelectionGUI gui = new TextSelectionGUI(label.internalText, reformat(MODID + ".config.selectEquip"), equipIDs);
            gui.addPostClosedActions(() ->
            {
                if (!label.internalText.getText().equals(""))
                {
                    label.addClickActions(() ->
                    {
                        YesNoGUI yesNoGUI = new YesNoGUI("", reformat(MODID + ".config.deleteThingMaybe", label.internalText.getText()));
                        yesNoGUI.addPostClosedActions(() ->
                        {
                            if (yesNoGUI.pressedYes) validEquips.remove(label);
                        });
                    });
                    validEquips.add(validEquips.size() - 1, label);
                }
            });
        });
        validEquips.add(emptyDummyEquip);


        //Save actions
        save.addClickActions(() ->
        {
            //Because one slot of each is always an empty dummy for adding more
            if (validSlots.size() < 2) validSlots.get(0).click();
            else if (validEquips.size() < 2) validEquips.get(0).click();
            else
            {
                //Valid slots
                slotData.slotNames.clear();
                String slotName;
                for (GUIElement element : validSlots.children)
                {
                    if (element instanceof GUITextLabel)
                    {
                        slotName = ((GUITextLabel) element).internalText.getText().trim();
                        if (!slotName.equals("")) SlotData.addSlotsFromString(slotName, slotData);
                    }
                }


                //Valid equips
                slotData.involvedEquips.clear();
                String equipID;
                Equip equip;
                for (GUIElement element : validEquips.children)
                {
                    if (element instanceof GUITextLabel)
                    {
                        equip = null;
                        equipID = ((GUITextLabel) element).internalText.getText();
                        for (Equip equip2 : data.equipment)
                        {
                            if (equip2.id.equals(equipID))
                            {
                                equip = equip2;
                                break;
                            }
                        }
                        if (equip != null) slotData.involvedEquips.add(equip);
                    }
                }


                clickedElement.set(slotData);
                close();
            }
        });
    }


    @Override
    public String title()
    {
        return reformat(MODID + ".config.slotData");
    }
}
