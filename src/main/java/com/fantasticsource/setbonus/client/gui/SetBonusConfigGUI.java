package com.fantasticsource.setbonus.client.gui;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.tools.datastructures.Color;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class SetBonusConfigGUI extends GUIScreen
{
    public SetBonusConfigGUI()
    {
        show();


        //Root
        root.setSubElementAutoplaceMethod(GUIElement.AP_CENTERED_H_TOP_TO_BOTTOM);

        root.add(new GUIDarkenedBackground(this));
        GUIElement element = new GUINavbar(this);
        ((GUINavbar) element).maxParentsDisplayed = 0;
        root.add(element);

        root.add(new GUITextSpacer(this));
        root.add(new GUITextSpacer(this));

        element = new GUITextButton(this, reformat(MODID + ".config.clientSettings"));
        element.addClickActions(ClientConfigGUI::new);
        ((GUITextButton) element).setColor(Color.AQUA);
        root.add(element);

        element = new GUITextButton(this, reformat(MODID + ".config.serverSettings"));
        element.addClickActions(ServerConfigGUI::new);
        ((GUITextButton) element).setColor(Color.AQUA);
        root.add(element);
    }


    @Override
    public void show()
    {
        showStacked();
    }

    @Override
    public String title()
    {
        return "Set Bonus";
    }
}
