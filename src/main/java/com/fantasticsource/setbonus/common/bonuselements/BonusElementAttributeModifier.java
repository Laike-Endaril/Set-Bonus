package com.fantasticsource.setbonus.common.bonuselements;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.attributes.AttributeMods;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.client.ClientData;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.server.ServerData;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.Arrays;

public class BonusElementAttributeModifier extends ABonusElement
{
    private Multimap<String, AttributeModifier> modifiers = ArrayListMultimap.create();


    private BonusElementAttributeModifier(String parsableBonusElement, Bonus bonus, ArrayList<AttributeModifier> modifiers)
    {
        super(parsableBonusElement, bonus);

        for (AttributeModifier modifier : modifiers)
        {
            this.modifiers.put(modifier.getName(), modifier.setSaved(false));
        }
    }

    public static BonusElementAttributeModifier getInstance(String parsableModifierBonus, Side side)
    {
        String[] tokens = parsableModifierBonus.split(",");
        if (tokens.length < 2)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.notEnoughAttribBonusArgs", parsableModifierBonus));
            return null;
        }

        Bonus bonus = side == Side.SERVER ? ServerData.SERVER_DATA.bonuses.get(tokens[0].trim()) : ClientData.CLIENT_DATA.bonuses.get(tokens[0].trim());
        if (bonus == null)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.attribBonusIDNotFound", tokens[0].trim(), parsableModifierBonus));
            return null;
        }

        //Error messages handled in library
        ArrayList<AttributeModifier> modifiers = AttributeMods.parseMods(Arrays.copyOfRange(tokens, 1, tokens.length));
        if (modifiers == null) return null;

        return new BonusElementAttributeModifier(parsableModifierBonus, bonus, modifiers);
    }

    @Override
    public void activate(EntityPlayer player)
    {
        float hpRatio = player.getHealth() / player.getMaxHealth();

        player.getAttributeMap().applyAttributeModifiers(modifiers);

        //This might help with edge cases
        if (hpRatio <= Float.MIN_VALUE) hpRatio = 0;
        else if (hpRatio >= 1 - Float.MIN_VALUE) hpRatio = 1;

        player.setHealth(hpRatio * player.getMaxHealth());
    }

    @Override
    public void deactivate(EntityPlayer player)
    {
        float hpRatio = player.getHealth() / player.getMaxHealth();

        player.getAttributeMap().removeAttributeModifiers(modifiers);

        //This might help with edge cases
        if (hpRatio <= Float.MIN_VALUE) hpRatio = 0;
        else if (hpRatio >= 1 - Float.MIN_VALUE) hpRatio = 1;

        player.setHealth(hpRatio * player.getMaxHealth());
    }

    @Override
    public void updateActive(EntityPlayer player)
    {
    }


    @Override
    public String[] tooltips()
    {
        String[] result = new String[modifiers.size()];
        int i = 0;
        for (AttributeModifier modifier : modifiers.values()) result[i++] = MCTools.getAttributeModString(modifier);
        return result;
    }
}
