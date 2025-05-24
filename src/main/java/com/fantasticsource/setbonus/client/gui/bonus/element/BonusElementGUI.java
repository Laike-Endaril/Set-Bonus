package com.fantasticsource.setbonus.client.gui.bonus.element;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUIStringPicker;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.mctools.potions.FantasticPotionEffect;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.client.gui.ServerConfigGUI;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementAttributeModifier;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementEnchantment;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementPotionEffect;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.util.LinkedHashMap;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class BonusElementGUI extends GUIScreen
{
    public SetBonusData data;
    public GUIBonusElement clickedElement;
    public ABonusElement element;

    public BonusElementGUI(SetBonusData data, GUIBonusElement clickedElement)
    {
        this.data = data;
        this.clickedElement = clickedElement;
        element = clickedElement.bonusElement;
        if (element == null) element = new BonusElementAttributeModifier();


        show();


        //Root
        root.add(new GUIDarkenedBackground(this));
        GUINavbar navbar = new GUINavbar(this);
        root.add(navbar);


        //Save, load, etc
        GUITextButton save = new GUITextButton(this, reformat(MODID + ".config.save"), Color.GREEN);
        root.add(save);
        root.add(new GUITextButton(this, reformat(MODID + ".config.cancel"), Color.ORANGE).addClickActions(this::close));
        GUITextButton delete = (GUITextButton) new GUITextButton(this, reformat(MODID + ".config.delete"), Color.RED).addClickActions(() ->
        {
            clickedElement.set(null);
            close();
        });
        root.add(delete);
        root.add(new GUITextSpacer(this));


        //Type
        LinkedHashMap<Class<? extends ABonusElement>, String> validTypes = new LinkedHashMap<>();
        validTypes.put(BonusElementAttributeModifier.class, reformat(MODID + ".config.attributeModifier"));
        validTypes.put(BonusElementPotionEffect.class, reformat(MODID + ".config.potionEffect"));
        validTypes.put(BonusElementEnchantment.class, reformat(MODID + ".config.enchantment"));
        GUIStringPicker type = new GUIStringPicker(this, reformat(MODID + ".config.type"), validTypes.values().toArray(new String[0]));
        root.add(type);
        GUITextSpacer spacer = new GUITextSpacer(this);
        root.add(spacer);


        //Type-Specific Settings
        GUIView typeSettings = new GUIView(this, 1, 1 - spacer.y - spacer.height);
        spacer.addRecalcActions(() -> typeSettings.height = 1 - spacer.y - spacer.height);
        root.add(typeSettings);


        //Type selection actions
        type.addEditActions(() ->
        {
            if (type.value.equals(reformat(MODID + ".config.attributeModifier")))
            {
                typeSettings.clear();

                BonusElementAttributeModifier attributeModifierElement = element instanceof BonusElementAttributeModifier ? ((BonusElementAttributeModifier) element).clone() : new BonusElementAttributeModifier();
                element = attributeModifierElement;

                GUIScrollView modifiers = new GUIScrollView(this, 1 - ServerConfigGUI.SCROLLBAR_WIDTH, 1);
                typeSettings.add(modifiers);
                GUIVerticalScrollbar modifiersScrollbar = new GUIVerticalScrollbar(this, ServerConfigGUI.SCROLLBAR_WIDTH, 1, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, modifiers);
                typeSettings.add(modifiersScrollbar);

                for (AttributeModifier modifier : attributeModifierElement.modifiers.values())
                {
                    GUIAttributeModifier guiAttributeModifier = new GUIAttributeModifier(this, modifier, 1);
                    guiAttributeModifier.addEditActions(() ->
                    {
                        if (guiAttributeModifier.modifier == null) modifiers.remove(guiAttributeModifier);
                    });
                    modifiers.add(guiAttributeModifier);
                }
                GUIAttributeModifier emptyDummyAttributeModifier = new GUIAttributeModifier(this, null, 1);
                emptyDummyAttributeModifier.addEditActions(() ->
                {
                    GUIAttributeModifier guiAttributeModifier = new GUIAttributeModifier(this, emptyDummyAttributeModifier.modifier, 1);
                    guiAttributeModifier.addEditActions(() ->
                    {
                        if (guiAttributeModifier.modifier == null) modifiers.remove(guiAttributeModifier);
                    });
                    modifiers.add(modifiers.size() - 1, guiAttributeModifier);
                    emptyDummyAttributeModifier.set(null);
                });
                modifiers.add(emptyDummyAttributeModifier);
            }
            else if (type.value.equals(reformat(MODID + ".config.potionEffect")))
            {
                typeSettings.clear();

                BonusElementPotionEffect potionEffectElement = element instanceof BonusElementPotionEffect ? ((BonusElementPotionEffect) element).clone() : new BonusElementPotionEffect();
                element = potionEffectElement;

                GUIScrollView potions = new GUIScrollView(this, 1 - ServerConfigGUI.SCROLLBAR_WIDTH, 1);
                typeSettings.add(potions);
                GUIVerticalScrollbar potionsScrollbar = new GUIVerticalScrollbar(this, ServerConfigGUI.SCROLLBAR_WIDTH, 1, getHoverColor(Color.AQUA), Color.BLANK, Color.AQUA, Color.BLANK, potions);
                typeSettings.add(potionsScrollbar);

                for (FantasticPotionEffect potionEffect : potionEffectElement.potions)
                {
                    GUIPotionEffect guiPotionEffect = new GUIPotionEffect(this, potionEffect, 1);
                    guiPotionEffect.addEditActions(() ->
                    {
                        if (guiPotionEffect.potionEffect == null) potions.remove(guiPotionEffect);
                    });
                    potions.add(guiPotionEffect);
                }
                GUIPotionEffect emptyDummyAttributeModifier = new GUIPotionEffect(this, null, 1);
                emptyDummyAttributeModifier.addEditActions(() ->
                {
                    GUIPotionEffect guiPotionEffect = new GUIPotionEffect(this, emptyDummyAttributeModifier.potionEffect, 1);
                    guiPotionEffect.addEditActions(() ->
                    {
                        if (guiPotionEffect.potionEffect == null) potions.remove(guiPotionEffect);
                    });
                    potions.add(potions.size() - 1, guiPotionEffect);
                    emptyDummyAttributeModifier.set(null);
                });
                potions.add(emptyDummyAttributeModifier);
            }
            else if (type.value.equals(reformat(MODID + ".config.enchantment")))
            {
                //TODO
            }
        });


        //Populate
        type.set(validTypes.get(element.getClass()));


        //Save actions
        save.addClickActions(() ->
        {
            if (element instanceof BonusElementAttributeModifier)
            {
                if (typeSettings.get(0).size() <= 1) delete.click();
                else
                {
                    ((BonusElementAttributeModifier) element).modifiers.clear();
                    AttributeModifier mod;
                    for (GUIElement guiElement : typeSettings.get(0).children)
                    {
                        mod = ((GUIAttributeModifier) guiElement).modifier;
                        if (mod != null) ((BonusElementAttributeModifier) element).modifiers.put(mod.getName(), mod);
                    }
                    clickedElement.set(element);
                    close();
                }
            }
            else if (element instanceof BonusElementPotionEffect)
            {
                if (typeSettings.get(0).size() <= 1) delete.click();
                else
                {
                    ((BonusElementPotionEffect) element).potions.clear();
                    FantasticPotionEffect potionEffect;
                    for (GUIElement guiElement : typeSettings.get(0).children)
                    {
                        potionEffect = ((GUIPotionEffect) guiElement).potionEffect;
                        if (potionEffect != null) ((BonusElementPotionEffect) element).potions.add(potionEffect);
                    }
                    clickedElement.set(element);
                    close();
                }
            }
            else if (element instanceof BonusElementEnchantment)
            {
                //TODO
            }
        });
    }

    @Override
    public String title()
    {
        return reformat(MODID + ".config.bonusElement");
    }
}
