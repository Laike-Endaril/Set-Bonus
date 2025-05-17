package com.fantasticsource.setbonus.common.bonusrequirements.setrequirement;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import com.fantasticsource.mctools.items.RegistryRegexItemFilter;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.tools.ReflectionTool;
import com.gildedgames.the_aether.api.AetherAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class SlotData
{
    public static final int
            BAUBLES_OFFSET = Integer.MIN_VALUE + 1, BAUBLES_THRESHOLD = BAUBLES_OFFSET + 300,
            AETHER_OFFSET = BAUBLES_THRESHOLD, AETHER_THRESHOLD = AETHER_OFFSET + 8,
            TRINKETS_OFFSET = AETHER_THRESHOLD, TRINKETS_THRESHOLD = TRINKETS_OFFSET + 32;


    public ArrayList<Integer> slots = new ArrayList<>(); //Because multiple slot options can be defined
    public LinkedHashSet<Equip> involvedEquips = new LinkedHashSet<>();


    private SlotData()
    {
    }

    public static SlotData getInstance(String slotsAndEquipment, LinkedHashMap<String, RegistryRegexItemFilter> setdataEquipIDTracker, SetBonusData data)
    {
        SlotData result = new SlotData();

        String[] tokens = slotsAndEquipment.split("=");
        if (tokens.length != 2)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.wrongSlotArgCount", slotsAndEquipment));
            return null;
        }


        //Slots
        ArrayList<String> errors = addSlotsFromString(tokens[0], result.slots);
        if (errors.size() > 0)
        {
            for (String error : errors) System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.unknownSlot", error, slotsAndEquipment));
            return null;
        }


        //Equipment
        for (String equipID : tokens[1].split("[|]"))
        {
            equipID = equipID.trim();
            Equip equip = data.equipment.get(equipID);
            if (equip == null)
            {
                System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.slotBadEquipID", equipID, slotsAndEquipment));
                return null;
            }

            result.involvedEquips.add(equip);
            if (setdataEquipIDTracker != null) setdataEquipIDTracker.put(equipID, equip.filter);
        }


        return result;
    }


    public static ItemStack getStackInSlot(EntityPlayer player, int slot)
    {
        //Mainhand conversion
        if (slot == -1) slot = player.inventory.currentItem;

        //Vanilla
        if (slot > -1) return player.inventory.getStackInSlot(slot);


        //Baubles
        if (slot < BAUBLES_THRESHOLD) return BaublesApi.getBaublesHandler(player).getStackInSlot(slot - BAUBLES_OFFSET);

        //Aether
        if (slot < AETHER_THRESHOLD) return AetherAPI.getInstance().get(player).getAccessoryInventory().getStackInSlot(slot - AETHER_OFFSET);

        //Trinkets
        if (slot < TRINKETS_THRESHOLD)
        {
            //Doesn't have a real API (API causes crash if mod is not loaded), so reflecting in
            Class trinketHelperClass = ReflectionTool.getClassByName("xzeroair.trinkets.api.TrinketHelper");
            IItemHandlerModifiable trinketContainerHandler = (IItemHandlerModifiable) ReflectionTool.invoke(trinketHelperClass, "getTrinketHandler", null, player);
            slot -= TRINKETS_OFFSET;
            return slot < trinketContainerHandler.getSlots() ? trinketContainerHandler.getStackInSlot(slot) : ItemStack.EMPTY;
        }


        throw new IllegalArgumentException();
    }

    public int equipped(EntityPlayer player, ArrayList<Integer> blocked, boolean allowEmptyStackIfMatching, boolean allowStackableItems)
    {
        for (int slot : slots)
        {
            if (slot == -1)
            {
                //Mainhand
                slot = player.inventory.currentItem;
            }


            if (blocked != null && blocked.contains(slot)) continue;

            ItemStack stack = getStackInSlot(player, slot);
            if (!allowEmptyStackIfMatching && stack == ItemStack.EMPTY) continue;
            if (!allowStackableItems && stack.getMaxStackSize() != 1) continue;


            for (Equip equip : involvedEquips)
            {
                if (equip.filter.matches(stack)) return slot;
            }
        }

        return Integer.MIN_VALUE;
    }


    //Returns the error-causing part of the string, or null if successful
    public static ArrayList<String> addSlotsFromString(String slots, ArrayList<Integer> arrayList)
    {
        ArrayList<String> errors = new ArrayList<>();
        ArrayList<Integer> slotIDs;

        for (String slotString : slots.split("[|]"))
        {
            slotIDs = getSlotIDs(slotString);
            if (slotIDs == null) errors.add(slotString.trim().toLowerCase());
            else arrayList.addAll(slotIDs);
        }

        return errors;
    }

    public static ArrayList<Integer> getSlotIDs(String slotString)
    {
        ArrayList<Integer> result = new ArrayList<>();
        slotString = slotString.trim().toLowerCase();


        //Vanilla
        if (slotString.equals("mainhand")) result.add(-1); //Mainhand is not a specific slot #
        else if (slotString.equals("hotbar"))
        {
            for (int i = 0; i < 9; i++) result.add(i);
        }
        else if (slotString.equals("inventory"))
        {
            for (int i = 9; i < 36; i++) result.add(i);
        }
        else if (slotString.equals("feet")) result.add(36);
        else if (slotString.equals("legs")) result.add(37);
        else if (slotString.equals("chest")) result.add(38);
        else if (slotString.equals("head")) result.add(39);
        else if (slotString.equals("offhand")) result.add(40);


            //Baubles
        else if (slotString.equals("bauble_amulet"))
        {
            for (int i : BaubleType.AMULET.getValidSlots()) result.add(BAUBLES_OFFSET + i);
        }
        else if (slotString.equals("bauble_ring"))
        {
            for (int i : BaubleType.RING.getValidSlots()) result.add(BAUBLES_OFFSET + i);
        }
        else if (slotString.equals("bauble_belt"))
        {
            for (int i : BaubleType.BELT.getValidSlots()) result.add(BAUBLES_OFFSET + i);
        }
        else if (slotString.equals("bauble_head"))
        {
            for (int i : BaubleType.HEAD.getValidSlots()) result.add(BAUBLES_OFFSET + i);
        }
        else if (slotString.equals("bauble_body"))
        {
            for (int i : BaubleType.BODY.getValidSlots()) result.add(BAUBLES_OFFSET + i);
        }
        else if (slotString.equals("bauble_charm"))
        {
            for (int i : BaubleType.CHARM.getValidSlots()) result.add(BAUBLES_OFFSET + i);
        }
        else if (slotString.equals("bauble_trinket"))
        {
            for (int i : BaubleType.TRINKET.getValidSlots()) result.add(BAUBLES_OFFSET + i);
        }


        //The Aether
        else if (slotString.equals("aether_pendant")) result.add(AETHER_OFFSET);
        else if (slotString.equals("aether_cape")) result.add(AETHER_OFFSET + 1);
        else if (slotString.equals("aether_shield")) result.add(AETHER_OFFSET + 2);
        else if (slotString.equals("aether_ring"))
        {
            result.add(AETHER_OFFSET + 4);
            result.add(AETHER_OFFSET + 5);
        }
        else if (slotString.equals("aether_glove") || slotString.equals("aether_gloves")) result.add(AETHER_OFFSET + 6);
        else if (slotString.equals("aether_other"))
        {
            result.add(AETHER_OFFSET + 3);
            result.add(AETHER_OFFSET + 7);
        }


        //Trinkets
        else if (slotString.equals("trinket"))
        {
            for (int i = TRINKETS_OFFSET; i < TRINKETS_THRESHOLD; i++) result.add(i);
        }


        //Vanilla extended (if mods add slots to vanilla inventory)
        else
        {
            try
            {
                result.add(Integer.parseInt(slotString));
            }
            catch (NumberFormatException e)
            {
                return null;
            }
        }

        return result;
    }


    public SlotData clone()
    {
        SlotData other = new SlotData();

        other.slots.addAll(slots);
        for (Equip equip : involvedEquips) other.involvedEquips.add(equip.clone());

        return other;
    }

    @Override
    public String toString()
    {
        throw new IllegalStateException("WIP");
    }
}
