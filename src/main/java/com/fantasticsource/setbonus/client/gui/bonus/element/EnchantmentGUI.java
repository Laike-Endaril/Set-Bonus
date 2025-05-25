package com.fantasticsource.setbonus.client.gui.bonus.element;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterInt;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class EnchantmentGUI extends GUIScreen
{
    public GUIEnchantment clickedElement;
    public Enchantment enchantment;
    public int mode, level;

    public EnchantmentGUI(GUIEnchantment clickedElement)
    {
        this.clickedElement = clickedElement;
        enchantment = clickedElement.enchantment;
        mode = clickedElement.mode;
        level = clickedElement.level;
        if (enchantment == null)
        {
            enchantment = ForgeRegistries.ENCHANTMENTS.iterator().next();
            mode = 0;
            level = 1;
        }


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
            clickedElement.set(null, 0, 1);
            close();
        }));
        root.add(new GUITextSpacer(this));


        //Enchantment Name
        String[] validNames = new String[ForgeRegistries.ENCHANTMENTS.getKeys().size()];
        int i = 0;
        for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS.getValues()) validNames[i++] = (enchantment.isCurse() ? TextFormatting.RED : TextFormatting.GREEN) + "" + enchantment.getTranslatedName(1);
        GUIStringPicker name = new GUIStringPicker(this, reformat(MODID + ".config.type"), validNames);
        name.set((enchantment.isCurse() ? TextFormatting.RED : TextFormatting.GREEN) + "" + enchantment.getTranslatedName(1));
        root.add(name);
        root.add(new GUIElement(this, 1, 0));

        //Mode
        String[] possibleModes = new String[5];
        for (int i2 = 0; i2 < possibleModes.length; i2++) possibleModes[i2] = reformat(MODID + ".enchantmode." + i2);
        GUIStringPicker modePicker = new GUIStringPicker(this, reformat(MODID + ".config.mode"), possibleModes);
        modePicker.set(possibleModes[mode]);
        root.add(modePicker);
        root.add(new GUIElement(this, 1, 0));

        //Level
        GUILabeledTextInput levelInput = new GUILabeledTextInput(this, reformat(MODID + ".config.level") + ": ", "" + level, FilterInt.INSTANCE);
        root.add(levelInput);
        root.add(new GUIElement(this, 1, 0));


        //Save actions
        save.addClickActions(() ->
        {
            if (!levelInput.valid())
            {
                levelInput.setText("" + level);
                levelInput.label.click();
            }
            else
            {
                //Update GUI
                clickedElement.set(ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(name.value)), Tools.indexOf(possibleModes, modePicker.value), FilterInt.INSTANCE.parse(levelInput.getText()));
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
