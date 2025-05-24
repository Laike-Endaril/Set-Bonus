package com.fantasticsource.setbonus.client.gui.bonus.element;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUIStringPicker;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUIView;
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
        root.add(new GUITextButton(this, reformat(MODID + ".config.delete"), Color.RED).addClickActions(() ->
        {
            clickedElement.set(null);
            close();
        }));
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
        });


        //Populate
        type.set(validTypes.get(element.getClass()));


        //Save actions
        save.addClickActions(() ->
        {
//            if (requirement instanceof SetRequirement)
//            {
//                GUILabeledTextInput number = (GUILabeledTextInput) typeSettings.get(2);
//                if (!number.valid())
//                {
//                    number.setText("-1");
//                    number.label.click();
//                }
//                else
//                {
//                    ((SetRequirement) requirement).num = FilterInt.INSTANCE.parse(number.getText());
//                    clickedElement.set(requirement);
//                    close();
//                }
//            }
//            else if (requirement instanceof AttributeRequirement)
//            {
//                GUILabeledTextInput
//                        attributeName = (GUILabeledTextInput) typeSettings.get(0),
//                        amount = (GUILabeledTextInput) typeSettings.get(4);
//                if (!attributeName.valid())
//                {
//                    attributeName.setText("generic.armorToughness");
//                    attributeName.label.click();
//                }
//                else if (!amount.valid())
//                {
//                    amount.setText("2");
//                    amount.label.click();
//                }
//                else
//                {
//                    AttributeRequirement req = (AttributeRequirement) requirement;
//                    req.attributeName = attributeName.getText();
//                    req.requirement.amount = FilterFloat.INSTANCE.parse(amount.getText());
//                    clickedElement.set(requirement);
//                    close();
//                }
//            }
        });
    }

    @Override
    public String title()
    {
        return reformat(MODID + ".config.bonusElement");
    }
}
