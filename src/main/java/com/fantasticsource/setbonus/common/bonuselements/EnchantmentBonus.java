package com.fantasticsource.setbonus.common.bonuselements;

import com.fantasticsource.mctools.enchantments.Enchantments;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.client.ClientData;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SlotData;
import com.fantasticsource.setbonus.server.ServerData;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Arrays;
import java.util.HashMap;

public class EnchantmentBonus extends ABonusElement
{
    public SlotData slotDataToEnchant;
    public HashMap<Enchantment, Integer> enchantments;
    public HashMap<EntityPlayer, ItemStack> affectedItemStacks = null; //NOT static; if it were static, there could be bad overwrites from OTHER ENCHANTMENT BONUSES

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
        int equippedInSlot = slotDataToEnchant.equipped(player, null, false);
        if (equippedInSlot != Integer.MIN_VALUE)
        {
            ItemStack stack = SlotData.getStackInSlot(player, equippedInSlot);
            ItemStack old = affectedItemStacks.get(player);
            if (old != null) removeFromStack(old);
            addToStack(stack);
        }
    }

    @Override
    public void deactivate(EntityPlayer player)
    {
        //Real / permanent enchantments are generally applied or removed when the item is NOT equipped to the player, so we shouldn't need to worry about the state of real / permanent enchantments changing while the bonus is active, hopefully
        ItemStack stack = affectedItemStacks.get(player);
        if (stack != null) removeFromStack(stack);
    }

    @Override
    public void updateActive(EntityPlayer player)
    {
        ItemStack stack = affectedItemStacks.get(player);
        if (stack != null)
        {
            boolean found = false;
            for (int slot : slotDataToEnchant.slots)
            {
                if (SlotData.getStackInSlot(player, slot) == stack)
                {
                    found = true;
                    break;
                }
            }
            if (found) return;
            else removeFromStack(stack);
        }

        activate(player);
    }

    public void addToStack(ItemStack stack)
    {
        //TODO alter item's enchantments; make sure to SAVE THE STATE OF PERMANENT ENCHANTMENTS TO NBT FIRST...BUT ONLY IF IT'S NOT ALREADY SAVED THERE BY ANOTHER BONUS, and account for other enchantment bonuses that may be active (check them!)
    }

    public void removeFromStack(ItemStack stack)
    {
        //TODO revert affected item's enchantment changes; make sure to account for other enchantment bonuses that may still be active (check them!)
    }
}
