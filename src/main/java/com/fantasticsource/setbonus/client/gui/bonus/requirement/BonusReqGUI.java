package com.fantasticsource.setbonus.client.gui.bonus.requirement;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import com.fantasticsource.tools.datastructures.Color;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class BonusReqGUI extends GUIScreen
{
    SetBonusData data;
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
        return reformat(MODID + ".config.requirement");
    }
}
