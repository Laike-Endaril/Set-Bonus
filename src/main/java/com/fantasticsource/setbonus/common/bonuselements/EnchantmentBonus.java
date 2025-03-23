package com.fantasticsource.setbonus.common.bonuselements;

import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.client.ClientData;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.server.ServerData;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class EnchantmentBonus extends ABonusElement
{
    //TODO WIP
    public HashMap<Enchantment, Integer> enchantments;
    public ArrayList<Integer> validSlots;

    public LinkedHashMap<EntityPlayer, ItemStack> activeItems = new LinkedHashMap<>();

    protected EnchantmentBonus(String parsableEnchantmentBonus, Bonus bonus, HashMap<Enchantment, Integer> enchantments, ArrayList<Integer> validSlots)
    {
        super(parsableEnchantmentBonus, bonus);
        this.validSlots = validSlots;
        this.enchantments = enchantments;
    }

    public static EnchantmentBonus getInstance(String parsableEnchantmentBonus, Side side)
    {
        String[] tokens = parsableEnchantmentBonus.split(",");
        if (tokens.length < 2)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.notEnoughEnchantmentBonusArgs", parsableEnchantmentBonus));
            return null;
        }

        Bonus bonus = side == Side.SERVER ? ServerData.bonuses.get(tokens[0].trim()) : ClientData.bonuses.get(tokens[0].trim());
        if (bonus == null)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.enchantmentBonusIDNotFound", tokens[0].trim(), parsableEnchantmentBonus));
            return null;
        }

        LinkedHashMap<Enchantment, Integer> enchants = new LinkedHashMap<>();
        for (String enchantString : Arrays.copyOfRange(tokens, 2, tokens.length))
        {
            //TODO
        }
        if (enchants.size() == 0) return null;

        //TODO set bonuses don't even necessarily require items at all...but if this one does, we can reference the requirements in the bonus object and find applicable items based on config...maybe?
        ArrayList<Integer> validSlots = null;

        return new EnchantmentBonus(parsableEnchantmentBonus, bonus, enchants, validSlots);
    }

    @Override
    public void activate(EntityPlayer player)
    {
    }

    @Override
    public void deactivate(EntityPlayer player)
    {
    }

    @Override
    public void updateActive(EntityPlayer player)
    {
    }
}
