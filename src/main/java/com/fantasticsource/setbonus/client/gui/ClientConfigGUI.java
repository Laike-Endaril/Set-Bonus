package com.fantasticsource.setbonus.client.gui;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.setbonus.config.SetBonusConfig;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;

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


        //Item tooltip blacklist
        GUITextButton itemTooltipBlacklist = new GUITextButton(this, reformat(MODID + ".config.itemTooltipBlacklist"), Color.AQUA);
        itemTooltipBlacklist.setColor(Color.AQUA);
        itemTooltipBlacklist.addClickActions(ItemTooltipBlacklistGUI::new);
        GUITextSpacer spacer = new GUITextSpacer(this);


        //Bonus element tooltips
        GUIBooleanToggle attributeModTooltips = new GUIBooleanToggle(this, reformat(MODID + ".config.enableAttributeModifierTooltips")).set(SetBonusConfig.clientSettings.enableAttributeModifierTooltips);
        GUIBooleanToggle potionTooltips = new GUIBooleanToggle(this, reformat(MODID + ".config.enablePotionEffectTooltips")).set(SetBonusConfig.clientSettings.enablePotionEffectTooltips);
        GUIBooleanToggle enchantmentTooltips = new GUIBooleanToggle(this, reformat(MODID + ".config.enableEnchantmentTooltips")).set(SetBonusConfig.clientSettings.enableEnchantmentTooltips);
        GUITextSpacer spacer2 = new GUITextSpacer(this);


        //JEI / HEI tooltip cache updates
        GUIStringPicker dynamicTooltipSearch = new GUIStringPicker(this, reformat(MODID + ".config.dynamicTooltipSearch"),
                reformat(MODID + ".config.dynamicTooltipSearch.never"),
                reformat(MODID + ".config.dynamicTooltipSearch.onBonusDiscovery"),
                reformat(MODID + ".config.dynamicTooltipSearch.always")
        );
        dynamicTooltipSearch.set(dynamicTooltipSearch.possibleValues[SetBonusConfig.clientSettings.dynamicTooltipSearch]);


        //Populate
        if (tooltips.value)
        {
            root.add(itemTooltipBlacklist);
            root.add(spacer);

            root.add(attributeModTooltips);
            root.add(potionTooltips);
            root.add(enchantmentTooltips);
            root.add(spacer2);

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

                root.add(itemTooltipBlacklist);
                root.add(spacer);

                root.add(attributeModTooltips);
                root.add(potionTooltips);
                root.add(enchantmentTooltips);
                root.add(spacer2);

                root.add(dynamicTooltipSearch);
            }
            else
            {
                SetBonusConfig.clientSettings.enableTooltips = false;

                root.remove(itemTooltipBlacklist);
                root.remove(spacer);

                root.remove(attributeModTooltips);
                root.remove(potionTooltips);
                root.remove(enchantmentTooltips);
                root.remove(spacer2);

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
