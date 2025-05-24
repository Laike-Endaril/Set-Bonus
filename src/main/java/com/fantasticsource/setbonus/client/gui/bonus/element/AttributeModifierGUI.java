package com.fantasticsource.setbonus.client.gui.bonus.element;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterFloat;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.entity.ai.attributes.AttributeModifier;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class AttributeModifierGUI extends GUIScreen
{
    public GUIAttributeModifier clickedElement;
    public AttributeModifier modifier;

    public AttributeModifierGUI(GUIAttributeModifier clickedElement)
    {
        this.clickedElement = clickedElement;
        modifier = clickedElement.modifier;
        if (modifier == null) modifier = new AttributeModifier("generic.attackDamage", 2, 2);


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


        //Name
        GUILabeledTextInput name = new GUILabeledTextInput(this, reformat(MODID + ".config.attributeName") + ": ", modifier.getName(), FilterNotEmpty.INSTANCE);
        root.add(name);
        root.add(new GUIElement(this, 1, 0));

        //Amount
        GUILabeledTextInput amount = new GUILabeledTextInput(this, reformat(MODID + ".config.amount") + ": ", "" + modifier.getAmount(), FilterFloat.INSTANCE);
        root.add(amount);
        root.add(new GUIElement(this, 1, 0));

        //Operation
        GUIStringPicker operation = new GUIStringPicker(this, reformat(MODID + ".config.operation"), "0", "1", "2");
        operation.set("" + modifier.getOperation());
        root.add(operation);


        //Save actions
        save.addClickActions(() ->
        {
            if (!name.valid())
            {
                name.setText(modifier.getName());
                name.label.click();
            }
            else if (!amount.valid())
            {
                amount.setText("" + modifier.getAmount());
                amount.label.click();
            }
            else
            {
                //Update GUI
                clickedElement.set(new AttributeModifier(name.getText(), FilterFloat.INSTANCE.parse(amount.getText()), Integer.parseInt(operation.value)));
                close();
            }
        });
    }


    @Override
    public String title()
    {
        return reformat(MODID + ".config.attributeModifier");
    }
}
