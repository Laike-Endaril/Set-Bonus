package com.fantasticsource.setbonus.client.gui.bonus.element;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.GUITextLabel;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.text.TextFormatting;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class GUIEnchantment extends GUITextLabel
{
    public Enchantment enchantment;
    public int mode, level;

    public GUIEnchantment(GUIScreen screen, Enchantment enchantment, int mode, int level, double width)
    {
        this(screen, enchantment, mode, level, width, 1);
    }

    public GUIEnchantment(GUIScreen screen, Enchantment enchantment, int mode, int level, double width, double scale)
    {
        super(screen, width, Color.AQUA, scale);
        set(enchantment, mode, level);

        addClickActions(() -> new EnchantmentGUI(this));
    }


    public void set(Enchantment enchantment, int mode, int level)
    {
        this.enchantment = enchantment;
        if (enchantment == null) internalText.setText("");
        else
        {
            String name = enchantment.getTranslatedName(level);
            if (name.contains("enchantment.level")) name = reformat(enchantment.getName()) + " " + level;
            internalText.setText((enchantment.isCurse() ? TextFormatting.RED : TextFormatting.GREEN) + "" + name + " (" + reformat(MODID + ".enchantmode." + mode) + ")");
        }
        runEditActions();
    }
}
