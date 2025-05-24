package com.fantasticsource.setbonus.client.gui.bonus.element;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.GUITextLabel;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.entity.ai.attributes.AttributeModifier;

public class GUIAttributeModifier extends GUITextLabel
{
    public AttributeModifier modifier;

    public GUIAttributeModifier(GUIScreen screen, AttributeModifier modifier, double width)
    {
        this(screen, modifier, width, 1);
    }

    public GUIAttributeModifier(GUIScreen screen, AttributeModifier modifier, double width, double scale)
    {
        super(screen, width, Color.AQUA, scale);
        set(modifier);

        addClickActions(() -> new AttributeModifierGUI(this));
    }


    public void set(AttributeModifier modifier)
    {
        this.modifier = modifier;
        if (modifier == null) internalText.setText("");
        else internalText.setText(MCTools.getAttributeModString(modifier));
        runEditActions();
    }
}
