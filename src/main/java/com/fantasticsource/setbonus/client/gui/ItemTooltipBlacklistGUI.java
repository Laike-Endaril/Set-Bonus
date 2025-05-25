package com.fantasticsource.setbonus.client.gui;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNone;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.setbonus.config.SetBonusConfig;
import com.fantasticsource.tools.datastructures.Color;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class ItemTooltipBlacklistGUI extends GUIScreen
{
    public GUIScrollView view;


    public ItemTooltipBlacklistGUI()
    {
        show();


        //Root
        root.add(new GUIDarkenedBackground(this));
        GUIElement element = new GUINavbar(this);
        root.add(element);


        //Save, load, etc
        GUITextButton save = new GUITextButton(this, reformat(MODID + ".config.save"), Color.GREEN);
        root.add(save);
        GUITextButton cancel = (GUITextButton) new GUITextButton(this, reformat(MODID + ".config.cancel"), Color.ORANGE).addClickActions(this::close);
        root.add(cancel);


        //Main
        view = new GUIScrollView(this, 1 - ServerConfigGUI.SCROLLBAR_WIDTH, 1 - cancel.y - cancel.height);
        root.add(view);
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(this, ServerConfigGUI.SCROLLBAR_WIDTH, 1 - cancel.y - cancel.height, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, view);
        root.add(scrollbar);


        //Filters
        for (String string : SetBonusConfig.clientSettings.itemTooltipBlacklist)
        {
            view.add(new GUILabeledTextInput(this, reformat(MODID + ".config.filter") + ": ", string, FilterNone.INSTANCE));
            view.add(new GUIElement(this, 1, 0));
        }
        view.add(new GUILabeledTextInput(this, reformat(MODID + ".config.filter") + ": ", "", FilterNone.INSTANCE));
        view.add(new GUIElement(this, 1, 0));


        //Recalc actions
        cancel.addRecalcActions(() ->
        {
            view.height = 1 - cancel.y - cancel.height;
            scrollbar.height = 1 - cancel.y - cancel.height;
        });


        //Remove empty filters in middle of list, add empty at end of list
        root.addClickActions(this::updateFilterList);
        view.addRecalcActions(this::updateFilterList);


        //Save actions
        save.addClickActions(() ->
        {
            SetBonusConfig.clientSettings.itemTooltipBlacklist = new String[(view.size() >> 1) - 1];
            for (int i = 0; i < view.size() - 2; i += 2)
            {
                SetBonusConfig.clientSettings.itemTooltipBlacklist[i >> 1] = ((GUILabeledTextInput) view.get(i)).getText().trim();
            }
            close();
            MCTools.saveConfig(MODID);
        });
    }


    public void updateFilterList()
    {
        GUILabeledTextInput filter;
        for (int i = 0; i < view.size() - 2; i += 2)
        {
            filter = (GUILabeledTextInput) view.get(i);
            if (filter.getText().trim().equals(""))
            {
                view.remove(i);
                view.remove(i);
                i -= 2;
            }
        }
        filter = (GUILabeledTextInput) view.get(view.size() - 2);
        if (!filter.getText().trim().equals(""))
        {
            view.add(new GUILabeledTextInput(this, reformat(MODID + ".config.filter") + ": ", "", FilterNone.INSTANCE));
            view.add(new GUIElement(this, 1, 0));
        }
    }


    @Override
    public String title()
    {
        return reformat(MODID + ".config.itemTooltipBlacklist");
    }
}
