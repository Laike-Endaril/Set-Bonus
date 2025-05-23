package com.fantasticsource.setbonus.client.gui.bonus;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUIStringPicker;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterBlacklist;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.common.Bonus;
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


        //Save, load, etc
        GUITextButton save = new GUITextButton(this, reformat(MODID + ".config.save"), Color.GREEN);
        root.add(save);
        root.add(new GUITextButton(this, reformat(MODID + ".config.cancel"), Color.ORANGE).addClickActions(this::close));


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
