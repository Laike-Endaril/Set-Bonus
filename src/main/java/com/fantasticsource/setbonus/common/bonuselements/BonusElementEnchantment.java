package com.fantasticsource.setbonus.common.bonuselements;

import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.mctools.enchantments.Enchantments;
import com.fantasticsource.mctools.event.InventoryChangedEvent;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.client.ClientBonus;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SlotData;
import com.fantasticsource.setbonus.server.ServerBonus;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class BonusElementEnchantment extends ABonusElement
{
    static
    {
        InventoryChangedEvent.watchedClasses.add(EntityPlayer.class);
        MinecraftForge.EVENT_BUS.register(BonusElementEnchantment.class);
    }


    public SlotData slotDataToEnchant;
    public HashMap<Pair<Enchantment, Integer>, Integer> enchantments;
    public HashMap<EntityPlayer, ItemStack> affectedItemStacks = new HashMap<>(); //NOT static; if it were static, there could be bad overwrites from OTHER ENCHANTMENT BONUSES

    protected BonusElementEnchantment(String parsableEnchantmentBonus, Bonus bonus, SlotData slotDataToEnchant, HashMap<Pair<Enchantment, Integer>, Integer> enchantments)
    {
        super(parsableEnchantmentBonus, bonus);
        this.slotDataToEnchant = slotDataToEnchant;
        this.enchantments = enchantments;
    }

    public static BonusElementEnchantment getInstance(String parsableEnchantmentBonus, SetBonusData data)
    {
        String[] tokens = parsableEnchantmentBonus.split(",");
        if (tokens.length < 3)
        {
            System.err.println(I18n.translateToLocalFormatted(MODID + ".error.notEnoughEnchantmentBonusArgs", parsableEnchantmentBonus));
            return null;
        }

        Bonus bonus = data.bonuses.get(tokens[0].trim());
        if (bonus == null)
        {
            System.err.println(I18n.translateToLocalFormatted(MODID + ".error.enchantmentBonusIDNotFound", tokens[0].trim(), parsableEnchantmentBonus));
            return null;
        }

        SlotData slotDataToEnchant = SlotData.getInstance(tokens[1].trim(), null, data);

        //Error messages handled in library
        HashMap<Pair<Enchantment, Integer>, Integer> enchantments = Enchantments.parseEnchantments(Arrays.copyOfRange(tokens, 2, tokens.length));
        if (enchantments.size() == 0) return null;

        return new BonusElementEnchantment(parsableEnchantmentBonus, bonus, slotDataToEnchant, enchantments);
    }

    @Override
    public void activate(EntityPlayer player)
    {
        int equippedInSlot = slotDataToEnchant.equipped(player, null, false, false);
        if (equippedInSlot != Integer.MIN_VALUE) addToStack(player, SlotData.getStackInSlot(player, equippedInSlot), equippedInSlot);
    }

    @Override
    public void deactivate(EntityPlayer player)
    {
        //Real / permanent enchantments are generally applied or removed when the item is NOT equipped to the player, so we shouldn't need to worry about the state of real / permanent enchantments changing while the bonus is active, hopefully
        ItemStack stack = affectedItemStacks.remove(player);
        if (stack != null) removeFromStack(player, stack);
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
                    stack.getTagCompound().setInteger("SBSlot", slot);
                    break;
                }
            }
            if (found) return;


            removeFromStack(player, stack);
        }

        activate(player);
    }


    public void addToStack(EntityPlayer player, ItemStack stack, int slot)
    {
        ItemStack old = affectedItemStacks.get(player);
        if (old == stack) return;


        if (old != null) removeFromStack(player, old);

        affectedItemStacks.put(player, stack);

        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null)
        {
            compound = new NBTTagCompound();
            stack.setTagCompound(compound);
        }


        NBTTagCompound ids;
        if (compound.hasKey("SBIDs")) ids = compound.getCompoundTag("SBIDs");
        else
        {
            ids = new NBTTagCompound();
            compound.setTag("SBIDs", ids);
        }


        String bonusHash = "" + bonus.hashCode();
        if (ids.hasKey(bonusHash)) return;


        ids.setTag(bonusHash, new NBTTagInt(0));
        compound.setUniqueId("SBOwner", player.getUniqueID());
        compound.setInteger("SBSlot", slot);


        HashMap<Integer, Integer> data = new HashMap<>();
        if (!compound.hasKey("OldEnchants"))
        {
            //This should mean that we're activating an enchantment bonus on the item when it has no other enchantment bonus active yet
            //DO NOT USE EnchantmentHelper METHODS!  THEY WON'T HAVE INTENDED FUNCTIONS IN REGARD TO ENCHANTED BOOKS FOR THIS MOD'S PURPOSES!

            if (!compound.hasKey("ench")) compound.setTag("OldEnchants", new NBTTagList());
            else
            {
                NBTTagList oldEnchants = compound.getTagList("ench", 10);
                compound.setTag("OldEnchants", oldEnchants);

                NBTTagCompound c;
                for (NBTBase oldEnchant : oldEnchants)
                {
                    c = (NBTTagCompound) oldEnchant;
                    data.put((int) c.getShort("id"), (int) c.getShort("lvl"));
                }
            }
        }
        else
        {
            NBTTagList oldEnchants = compound.getTagList("ench", 10);

            NBTTagCompound c;
            for (NBTBase oldEnchant : oldEnchants)
            {
                c = (NBTTagCompound) oldEnchant;
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
                    //Normal; vanilla behavior
                    if (level == oldLevel) level++;
                    else level = Tools.max(level, oldLevel);
                    level = Tools.min(Tools.max(level, 0), enchantment.getMaxLevel());
                    break;

                case 1:
                    //Unbound; vanilla behavior, but without limits
                    if (level == oldLevel) level++;
                    else level = Tools.max(level, oldLevel);
                    break;

                case 2:
                    //Forced; set level directly, overriding any previous value
                    break;

                case 3:
                    //Add; add to level directly, with limits (0 -> max level); can be used to subtract levels if attached level is negative
                    level += oldLevel;
                    level = Tools.min(Tools.max(level, 0), enchantment.getMaxLevel());
                    break;

                case 4:
                    //Add Unbound; add to level directly, without limits; can be used to subtract levels if attached level is negative
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


    public void removeFromStack(EntityPlayer player, ItemStack stack)
    {
        affectedItemStacks.remove(player, stack);


        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) return;

        if (compound.hasKey("OldEnchants"))
        {
            NBTTagList oldEnchants = compound.getTagList("OldEnchants", 10);
            if (oldEnchants.tagCount() == 0) compound.removeTag("ench");
            else compound.setTag("ench", compound.getTag("OldEnchants"));
        }
        //NOTE: If the stack somehow has enchantment bonus enchantments applied but is somehow missing the "OldEnchants" tag, it will keep the bonus enchantments permanently


        boolean otherApplied = false;
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
        {
            ServerBonus.BonusInstance bonusInstance;
            for (Bonus bonus : SetBonusData.SERVER_DATA.bonuses.values())
            {
                bonusInstance = ((ServerBonus) bonus).getBonusInstance((EntityPlayerMP) player);
                if (!bonusInstance.active) continue;

                for (ABonusElement bonusElement : bonus.bonusElements)
                {
                    if (bonusElement instanceof BonusElementEnchantment && bonusElement != this)
                    {
                        bonusElement.activate(player);
                        if (((BonusElementEnchantment) bonusElement).affectedItemStacks.get(player) == stack) otherApplied = true;
                    }
                }
            }
        }
        else
        {
            ClientBonus.BonusInstance bonusInstance;
            for (Bonus bonus : SetBonusData.CLIENT_DATA.bonuses.values())
            {
                bonusInstance = ((ClientBonus) bonus).getBonusInstance(player);
                if (!bonusInstance.active) continue;

                for (ABonusElement bonusElement : bonus.bonusElements)
                {
                    if (bonusElement instanceof BonusElementEnchantment && bonusElement != this)
                    {
                        bonusElement.activate(player);
                        if (((BonusElementEnchantment) bonusElement).affectedItemStacks.get(player) == stack) otherApplied = true;
                    }
                }
            }
        }


        if (!otherApplied)
        {
            compound.removeTag("OldEnchants");
            compound.removeTag("SBOwnerMost");
            compound.removeTag("SBOwnerLeast");
            compound.removeTag("SBSlot");
            compound.removeTag("SBIDs");
            if (compound.getSize() == 0) stack.setTagCompound(null);
        }
    }


    @SubscribeEvent
    public static void inventoryChanged(InventoryChangedEvent event)
    {
        if (!(event.getEntity() instanceof EntityPlayer)) return;


        //Just reset enchantments if the itemstack gets messed with; if it should have any enchantment bonuses re-applied, the bonuses will detect so automatically in updateActive()
        EntityPlayer player = (EntityPlayer) event.getEntity();
        NBTTagCompound compound;
        for (ItemStack stack : GlobalInventory.getAllNonSkinItems(player))
        {
            compound = stack.getTagCompound();
            if (compound != null && compound.hasKey("SBSlot"))
            {
                if (!stack.isItemEqual(SlotData.getStackInSlot(player, compound.getInteger("SBSlot"))))
                {
                    if (compound.getTagList("OldEnchants", 10).tagCount() == 0) compound.removeTag("ench");
                    else compound.setTag("ench", compound.getTag("OldEnchants"));

                    compound.removeTag("OldEnchants");
                    compound.removeTag("SBOwnerMost");
                    compound.removeTag("SBOwnerLeast");
                    compound.removeTag("SBSlot");
                    compound.removeTag("SBIDs");
                    if (compound.getSize() == 0) stack.setTagCompound(null);
                }
            }
        }

        MCTools.syncInventory((EntityPlayerMP) player);
    }

    @SubscribeEvent
    public static void itemStackCreation(AttachCapabilitiesEvent<ItemStack> event)
    {
        //Minecraft likes to "move" (or even leave in the same place) items by duplicating them and deleting the original...
        //The new clone doesn't have the NBT set yet when this event happens, but if it's scheduled with minimal delay like this then it does
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
        {
            ServerTickTimer.schedule(1, () ->
            {
                ItemStack stack = event.getObject();
                NBTTagCompound compound = stack.getTagCompound();
                if (compound != null)
                {
                    compound.removeTag("SBRandom"); //Purge outdated tag if it exists

                    if (compound.hasKey("SBSlot"))
                    {
                        EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(compound.getUniqueId("SBOwner"));
                        if (player == null || !stack.isItemEqual(SlotData.getStackInSlot(player, compound.getInteger("SBSlot"))))
                        {
                            //The player is not online, OR this is not in the slot it was in before it got replicated

                            if (compound.getTagList("OldEnchants", 10).tagCount() == 0) compound.removeTag("ench");
                            else compound.setTag("ench", compound.getTag("OldEnchants"));

                            compound.removeTag("OldEnchants");
                            compound.removeTag("SBOwnerMost");
                            compound.removeTag("SBOwnerLeast");
                            compound.removeTag("SBSlot");
                            compound.removeTag("SBIDs");
                            if (compound.getSize() == 0) stack.setTagCompound(null);

                            if (player != null) MCTools.syncInventory(player);
                        }
                    }
                }
            });
        }
    }


    @Override
    public String[] tooltips()
    {
        //Enchant, behavior/mode, level
        String[] result = new String[enchantments.size()];
        int i = 0;
        Enchantment enchantment;
        int mode, level;
        for (Map.Entry<Pair<Enchantment, Integer>, Integer> entry : enchantments.entrySet())
        {
            enchantment = entry.getKey().getKey();
            mode = entry.getKey().getValue();
            level = entry.getValue();
            result[i++] = (enchantment.isCurse() ? TextFormatting.RED : TextFormatting.GREEN) + "" + enchantment.getTranslatedName(level) + " (" + I18n.translateToLocal(MODID + ".enchantmode." + mode) + ")";
        }
        return result;
    }


    public BonusElementEnchantment clone(SetBonusData data)
    {
        return getInstance(parsedString, data);
    }
}
