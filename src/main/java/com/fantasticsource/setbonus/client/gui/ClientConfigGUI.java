package com.fantasticsource.setbonus.client.gui;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUIBooleanToggle;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUIStringPicker;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.setbonus.config.SetBonusConfig;
import com.fantasticsource.tools.Tools;

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
        root.add(element);
        root.add(new GUITextSpacer(this));


        //Main
        GUIBooleanToggle tooltips = new GUIBooleanToggle(this, reformat(MODID + ".config.enableTooltips")).set(SetBonusConfig.clientSettings.enableTooltips);
        root.add(tooltips);
        root.add(new GUITextSpacer(this));


        GUIBooleanToggle attributeModTooltips = new GUIBooleanToggle(this, reformat(MODID + ".config.enableAttributeModifierTooltips")).set(SetBonusConfig.clientSettings.enableAttributeModifierTooltips);
        GUIBooleanToggle potionTooltips = new GUIBooleanToggle(this, reformat(MODID + ".config.enablePotionEffectTooltips")).set(SetBonusConfig.clientSettings.enablePotionEffectTooltips);
        GUIBooleanToggle enchantmentTooltips = new GUIBooleanToggle(this, reformat(MODID + ".config.enableEnchantmentTooltips")).set(SetBonusConfig.clientSettings.enableEnchantmentTooltips);
        GUITextSpacer spacer = new GUITextSpacer(this);


        GUIStringPicker dynamicTooltipSearch = new GUIStringPicker(this, reformat(MODID + ".config.dynamicTooltipSearch"),
                reformat(MODID + ".config.dynamicTooltipSearch.never"),
                reformat(MODID + ".config.dynamicTooltipSearch.onBonusDiscovery"),
                reformat(MODID + ".config.dynamicTooltipSearch.always")
        );
        dynamicTooltipSearch.set(dynamicTooltipSearch.possibleValues[SetBonusConfig.clientSettings.dynamicTooltipSearch]);


        if (tooltips.value)
        {
            root.add(attributeModTooltips);
            root.add(potionTooltips);
            root.add(enchantmentTooltips);
            root.add(spacer);

            root.add(dynamicTooltipSearch);
        }


        //Actions
        dynamicTooltipSearch.addEditActions(() ->
        {
            SetBonusConfig.clientSettings.dynamicTooltipSearch = Tools.indexOf(dynamicTooltipSearch.possibleValues, dynamicTooltipSearch.value);
            MCTools.saveConfig(MODID);
        });

        tooltips.addEditActions(() ->
        {
            if (tooltips.value)
            {
                SetBonusConfig.clientSettings.enableTooltips = true;
                root.add(attributeModTooltips);
                root.add(potionTooltips);
                root.add(enchantmentTooltips);
                root.add(spacer);

                root.add(dynamicTooltipSearch);
            }
            else
            {
                SetBonusConfig.clientSettings.enableTooltips = false;
                root.remove(attributeModTooltips);
                root.remove(potionTooltips);
                root.remove(enchantmentTooltips);
                root.remove(spacer);

                root.remove(dynamicTooltipSearch);
            }
            MCTools.saveConfig(MODID);
        });

        attributeModTooltips.addEditActions(() ->
        {
            SetBonusConfig.clientSettings.enableAttributeModifierTooltips = attributeModTooltips.value;
            MCTools.saveConfig(MODID);
        });

        potionTooltips.addEditActions(() ->
        {
            SetBonusConfig.clientSettings.enablePotionEffectTooltips = potionTooltips.value;
            MCTools.saveConfig(MODID);
        });

        enchantmentTooltips.addEditActions(() ->
        {
            SetBonusConfig.clientSettings.enableEnchantmentTooltips = enchantmentTooltips.value;
            MCTools.saveConfig(MODID);
        });
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
