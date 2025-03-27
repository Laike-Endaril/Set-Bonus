package com.fantasticsource.setbonus.common.bonuselements;

import com.fantasticsource.mctools.enchantments.Enchantments;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.client.ClientData;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SlotData;
import com.fantasticsource.setbonus.server.ServerData;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EnchantmentBonus extends ABonusElement
{
    public SlotData slotDataToEnchant;
    public HashMap<Pair<Enchantment, Integer>, Integer> enchantments;
    public HashMap<EntityPlayer, ItemStack> affectedItemStacks = null; //NOT static; if it were static, there could be bad overwrites from OTHER ENCHANTMENT BONUSES

    protected EnchantmentBonus(String parsableEnchantmentBonus, Bonus bonus, SlotData slotDataToEnchant, HashMap<Pair<Enchantment, Integer>, Integer> enchantments)
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
        HashMap<Pair<Enchantment, Integer>, Integer> enchantments = Enchantments.parseEnchantments(Arrays.copyOfRange(tokens, 1, tokens.length));
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
        HashMap<Integer, Integer> data = new HashMap<>();

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey("OldEnchants"))
        {
            //This should mean that we're activating an enchantment bonus on the item when it has no other enchantment bonus active yet
            //DO NOT USE EnchantmentHelper METHODS!  THEY WON'T HAVE INTENDED FUNCTIONS IN REGARD TO ENCHANTED BOOKS FOR THIS MOD'S PURPOSES!

            if (!compound.hasKey("ench")) compound.setBoolean("OldEnchants", false);
            else
            {
                NBTTagList oldEnchants = compound.getTagList("ench", 10);
                compound.setTag("OldEnchants", oldEnchants);

                NBTTagCompound c;
                for (Iterator<NBTBase> it = oldEnchants.iterator(); it.hasNext(); )
                {
                    c = (NBTTagCompound) it.next();
                    data.put((int) c.getShort("id"), (int) c.getShort("lvl"));
                }
            }
        }
        else
        {
            NBTTagList oldEnchants = compound.getTagList("ench", 10);

            NBTTagCompound c;
            for (Iterator<NBTBase> it = oldEnchants.iterator(); it.hasNext(); )
            {
                c = (NBTTagCompound) it.next();
                data.put((int) c.getShort("id"), (int) c.getShort("lvl"));
            }
        }


        Enchantment enchantment;
        int id, level, oldLevel;
        for (Map.Entry<Pair<Enchantment, Integer>, Integer> entry : enchantments.entrySet())
        {
            enchantment = entry.getKey().getKey();
            id = Enchantment.getEnchantmentID(enchantment);
            level = entry.getValue();
            oldLevel = data.getOrDefault(id, 0);
            switch (entry.getKey().getValue())
            {
                case 0:
                    //Vanilla behavior
                    if (level == oldLevel) level++;
                    else level = Tools.max(level, oldLevel);
                    level = Tools.min(Tools.max(level, 0), enchantment.getMaxLevel());
                    break;

                case 1:
                    //Vanilla behavior, but without limits
                    if (level == oldLevel) level++;
                    else level = Tools.max(level, oldLevel);
                    break;

                case 2:
                    //Set level directly, overriding any previous value
                    break;

                case 3:
                    //Add to level directly, with limits (0 -> max level); can be used to subtract levels if attached level is negative
                    level += oldLevel;
                    level = Tools.min(Tools.max(level, 0), enchantment.getMaxLevel());
                    break;

                case 4:
                    //Add to level directly, without limits; can be used to subtract levels if attached level is negative
                    level += oldLevel;
                    break;
            }

            if (level == 0) data.remove(id);
            else data.put(id, level);
        }


        if (data.size() == 0) compound.removeTag("ench");
        else
        {
            NBTTagList enchants = new NBTTagList();
            NBTTagCompound nbttagcompound;
            for (Map.Entry<Integer, Integer> entry : data.entrySet())
            {
                nbttagcompound = new NBTTagCompound();
                nbttagcompound.setShort("id", (short) (int) entry.getKey());
                nbttagcompound.setShort("lvl", (short) (int) entry.getValue());
                enchants.appendTag(nbttagcompound);
            }
            compound.setTag("ench", enchants);
        }
    }


    public void removeFromStack(ItemStack stack)
    {

        //TODO revert affected item's enchantments to permanent values, then re-apply any other enchantment bonuses that are still active on the item
    }
}
