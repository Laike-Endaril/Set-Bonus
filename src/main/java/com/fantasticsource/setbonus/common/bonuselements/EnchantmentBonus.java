package com.fantasticsource.setbonus.common.bonuselements;

import com.fantasticsource.mctools.enchantments.Enchantments;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.client.ClientData;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SlotData;
import com.fantasticsource.setbonus.server.ServerData;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Arrays;
import java.util.HashMap;

public class EnchantmentBonus extends ABonusElement
{
    public SlotData slotDataToEnchant;
    public HashMap<Enchantment, Integer> enchantments;

    protected EnchantmentBonus(String parsableEnchantmentBonus, Bonus bonus, SlotData slotDataToEnchant, HashMap<Enchantment, Integer> enchantments)
    {
        super(parsableEnchantmentBonus, bonus);
        this.slotDataToEnchant = slotDataToEnchant;
        this.enchantments = enchantments;
    }

    public static EnchantmentBonus getInstance(String parsableEnchantmentBonus, Side side)
    {
        String[] tokens = parsableEnchantmentBonus.split(",");
        if (tokens.length < 3)
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

        SlotData slotDataToEnchant = SlotData.getInstance(tokens[1].trim(), null, side);

        //Error messages handled in library
        HashMap<Enchantment, Integer> enchantments = Enchantments.parseEnchantments(Arrays.copyOfRange(tokens, 1, tokens.length));
        if (enchantments.size() == 0) return null;

        return new EnchantmentBonus(parsableEnchantmentBonus, bonus, slotDataToEnchant, enchantments);
    }

    @Override
    public void activate(EntityPlayer player)
    {
        System.out.println("Activate");
    }

    @Override
    public void deactivate(EntityPlayer player)
    {
        System.out.println("Deactivate");
    }

    @Override
    public void updateActive(EntityPlayer player)
    {
    }
}
