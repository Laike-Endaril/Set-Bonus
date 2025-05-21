package com.fantasticsource.setbonus.client.gui.set;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextLabel;
import com.fantasticsource.mctools.gui.element.text.filter.FilterBlacklist;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.client.gui.ServerConfigGUI;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SlotData;
import com.fantasticsource.tools.datastructures.Color;

import java.util.ArrayList;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class SetGUI extends GUIScreen
{
    public SetBonusData data;
    public GUISet clickedElement;
    public Set set;
    public GUILabeledTextInput id, name;
    public GUITextLabel slotDataLabel;
    public GUIView slotDataView;
    public GUIScrollView slotData;
    public GUIVerticalScrollbar slotDataScrollbar;


    public SetGUI(SetBonusData data, GUISet clickedElement)
    {
        this.data = data;
        this.clickedElement = clickedElement;
        set = clickedElement.set;


        show();


        //Root
        root.add(new GUIDarkenedBackground(this));
        GUINavbar navbar = new GUINavbar(this);
        root.add(navbar);


        //ID
        ArrayList<String> idBlacklist = new ArrayList<>();
        idBlacklist.add("");
        for (GUIElement element : clickedElement.parent.children)
        {
            if (element == clickedElement) continue;
            if (element instanceof GUISet) idBlacklist.add(((GUISet) element).set.id);
        }
        id = new GUILabeledTextInput(this, reformat(MODID + ".config.id") + ": ", set.id, new FilterBlacklist(idBlacklist.toArray(new String[0])));
        root.addAll(id, new GUIElement(this, 1, 0));


        //Name
        name = new GUILabeledTextInput(this, reformat(MODID + ".config.name") + ": ", set.name, FilterNotEmpty.INSTANCE);
        root.addAll(name, new GUIElement(this, 1, 0));


        //Save, load, etc
        GUITextButton save = new GUITextButton(this, reformat(MODID + ".config.save"), Color.GREEN);
        root.add(save);
        root.add(new GUITextButton(this, reformat(MODID + ".config.cancel"), Color.RED).addClickActions(this::close));


        //SlotData
        slotDataLabel = new GUITextLabel(this, 1, Color.GREEN);
        slotDataLabel.setText(reformat(MODID + ".config.slotData"));
        root.add(slotDataLabel);

        slotDataView = new GUIView(this, 1, 1 - slotDataLabel.y - slotDataLabel.height);
        slotDataLabel.addRecalcActions(() -> slotDataView.height = 1 - slotDataLabel.y - slotDataLabel.height);
        root.add(slotDataView);

        slotData = new GUIScrollView(this, 1 - ServerConfigGUI.SCROLLBAR_WIDTH, 1);
        slotDataScrollbar = new GUIVerticalScrollbar(this, ServerConfigGUI.SCROLLBAR_WIDTH, 1, Color.AQUA, Color.BLANK, Color.AQUA, Color.BLANK, slotData);
        slotDataView.addAll(slotData, slotDataScrollbar);

        for (SlotData slotData2 : set.slotData)
        {
            slotData.add(new GUISlotData(this, data, slotData2, 1));
        }
        slotData.add(new GUISlotData(this, data, SlotData.getEmpty(), 1));


        //Save actions
        save.addClickActions(() ->
        {
            if (!id.valid())
            {
                id.setText(set.id);
                id.label.click();
            }
            else if (!name.valid())
            {
                name.setText(set.name);
                name.label.click();
            }
            else
            {
                //ID and name
                set.id = id.getText();
                set.name = name.getText();


                //Slot data
                set.slotData.clear();
                SlotData slotData2;
                for (GUIElement element : slotData.children)
                {
                    slotData2 = ((GUISlotData) element).slotData;
                    if (slotData2 != null && slotData2.slotNames.size() > 0 && slotData2.involvedEquips.size() > 0) set.slotData.add(slotData2);
                }


                //Update GUI
                clickedElement.set(set);
                close();
            }
        });
    }

    @Override
    public String title()
    {
        return reformat(set.id) + " (" + reformat(set.name) + ")";
    }
}
