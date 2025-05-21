package com.fantasticsource.setbonus.client.gui.set;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextLabel;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.client.gui.ServerConfigGUI;
import com.fantasticsource.setbonus.client.gui.equip.GUIEquip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SlotData;
import com.fantasticsource.tools.datastructures.Color;

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
        root.add(new GUITextButton(this, reformat(MODID + ".config.cancel"), Color.RED).addClickActions(this::close));


        //Valid Slots
        validSlotsLabel = new GUITextLabel(this, 1, Color.GREEN);
        validSlotsLabel.setText(reformat(MODID + ".config.validSlots"));
        root.add(validSlotsLabel);

        validSlotsView = new GUIView(this, 1, (1 - validSlotsLabel.y - validSlotsLabel.height * 2) * 0.5);
        validSlotsLabel.addRecalcActions(() -> validSlotsView.height = (1 - validSlotsLabel.y - validSlotsLabel.height * 2) * 0.5);
        root.add(validSlotsView);

        validSlots = new GUIScrollView(this, 1 - ServerConfigGUI.SCROLLBAR_WIDTH, 1);
        validSlotsScrollbar = new GUIVerticalScrollbar(this, ServerConfigGUI.SCROLLBAR_WIDTH, 1, Color.AQUA, Color.BLANK, Color.AQUA, Color.BLANK, validSlots);
        validSlotsView.addAll(validSlots, validSlotsScrollbar);

        for (String slotName : slotData.slotNames)
        {
            validSlots.add(new GUITextLabel(this, 1, Color.GREEN).setText(slotName));
        }
        validSlots.add(new GUITextLabel(this, 1, Color.GREEN));


        //Valid Equips
        validEquipsLabel = new GUITextLabel(this, 1, Color.RED);
        validEquipsLabel.setText(reformat(MODID + ".config.validEquips"));
        root.add(validEquipsLabel);

        validEquipsView = new GUIView(this, 1, 1 - validEquipsLabel.y - validEquipsLabel.height);
        validEquipsView.addRecalcActions(() -> validEquipsView.height = 1 - validEquipsLabel.y - validEquipsLabel.height);
        root.add(validEquipsView);

        validEquips = new GUIScrollView(this, 1 - ServerConfigGUI.SCROLLBAR_WIDTH, 1);
        validEquipsScrollbar = new GUIVerticalScrollbar(this, ServerConfigGUI.SCROLLBAR_WIDTH, 1, Color.AQUA, Color.BLANK, Color.AQUA, Color.BLANK, validEquips);
        validEquipsView.addAll(validEquips, validEquipsScrollbar);

        for (Equip equip : slotData.involvedEquips)
        {
            validEquips.add(new GUITextLabel(this, 1, Color.GREEN).setText(reformat(equip.id)));
        }
        validEquips.add(new GUITextLabel(this, 1, Color.GREEN));


        //Save actions
        save.addClickActions(() ->
        {
            //Valid slots
            slotData.slotNames.clear();
            slotData.slots.clear();
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
            Equip equip;
            for (GUIElement element : validEquips.children)
            {
                if (element instanceof GUIEquip)
                {
                    equip = ((GUIEquip) element).equip;
                    if (equip != null) slotData.involvedEquips.add(equip);
                }
            }


            clickedElement.set(slotData);
            close();
        });
    }


    @Override
    public String title()
    {
        return reformat(MODID + ".config.slotData");
    }
}
