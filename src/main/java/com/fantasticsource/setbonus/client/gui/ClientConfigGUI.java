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
        root.add(tooltips.setTooltip(reformat(MODID + ".config.enableTooltips.tooltip")));

        GUIBooleanToggle attributeModTooltips = new GUIBooleanToggle(this, reformat(MODID + ".config.enableAttributeModifierTooltips")).set(SetBonusConfig.clientSettings.enableAttributeModifierTooltips);
        GUIBooleanToggle potionTooltips = new GUIBooleanToggle(this, reformat(MODID + ".config.enablePotionEffectTooltips")).set(SetBonusConfig.clientSettings.enablePotionEffectTooltips);
        GUIBooleanToggle enchantmentTooltips = new GUIBooleanToggle(this, reformat(MODID + ".config.enableEnchantmentTooltips")).set(SetBonusConfig.clientSettings.enableEnchantmentTooltips);

        if (tooltips.value)
        {
            root.add(attributeModTooltips.setTooltip(reformat(MODID + ".config.enableAttributeModifierTooltips.tooltip")));
            root.add(potionTooltips.setTooltip(reformat(MODID + ".config.enablePotionEffectTooltips.tooltip")));
            root.add(enchantmentTooltips.setTooltip(reformat(MODID + ".config.enableEnchantmentTooltips.tooltip")));
        }


        //Actions
        tooltips.addClickActions(() ->
        {
            if (tooltips.value)
            {
                SetBonusConfig.clientSettings.enableTooltips = true;
                root.add(attributeModTooltips);
                root.add(potionTooltips);
                root.add(enchantmentTooltips);
            }
            else
            {
                SetBonusConfig.clientSettings.enableTooltips = false;
                root.remove(attributeModTooltips);
                root.remove(potionTooltips);
                root.remove(enchantmentTooltips);
            }
            MCTools.saveConfig(MODID);
        });

        attributeModTooltips.addClickActions(() ->
        {
            SetBonusConfig.clientSettings.enableAttributeModifierTooltips = attributeModTooltips.value;
            MCTools.saveConfig(MODID);
        });

        potionTooltips.addClickActions(() ->
        {
            SetBonusConfig.clientSettings.enablePotionEffectTooltips = potionTooltips.value;
            MCTools.saveConfig(MODID);
        });

        enchantmentTooltips.addClickActions(() ->
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
