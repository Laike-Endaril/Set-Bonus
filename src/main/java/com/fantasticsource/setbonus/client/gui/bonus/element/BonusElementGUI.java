package com.fantasticsource.setbonus.client.gui.bonus.element;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.tools.datastructures.Color;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class BonusElementGUI extends GUIScreen
{
    SetBonusData data;
    public GUIBonusElement clickedElement;
    public ABonusElement element;

    public BonusElementGUI(SetBonusData data, GUIBonusElement clickedElement)
    {
        this.data = data;
        this.clickedElement = clickedElement;
        element = clickedElement.element;


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


        //TODO


        //Save actions
        save.addClickActions(() ->
        {
        });
    }

    @Override
    public String title()
    {
        return reformat(MODID + ".config.bonusElement");
    }
}
