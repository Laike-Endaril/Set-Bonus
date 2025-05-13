package com.fantasticsource.setbonus.client.gui;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUIBooleanToggle;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.setbonus.config.SetBonusConfig;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class ClientConfigGUI extends GUIScreen
{
    public ClientConfigGUI()
    {
        show();


        //Root
        root.setSubElementAutoplaceMethod(GUIElement.AP_CENTERED_H_TOP_TO_BOTTOM);

        root.add(new GUIDarkenedBackground(this));
        GUIElement element = new GUINavbar(this);
        ((GUINavbar) element).maxParentsDisplayed = 1;
        root.add(element);

        root.add(new GUITextSpacer(this));
        root.add(new GUITextSpacer(this));

        GUIBooleanToggle tooltips = new GUIBooleanToggle(this, reformat(MODID + ".config.enableTooltips")).set(SetBonusConfig.clientSettings.enableTooltips);
        tooltips.onClickActions.add(() ->
        {
            SetBonusConfig.clientSettings.enableTooltips = tooltips.value;
            MCTools.saveConfig(MODID);
        });
        root.add(tooltips.setTooltip(reformat(MODID + ".config.enableTooltips.tooltip")));
    }


    @Override
    public void show()
    {
        showStacked();
    }

    @Override
    public String title()
    {
        return reformat(MODID + ".config.clientSettings");
    }
}
