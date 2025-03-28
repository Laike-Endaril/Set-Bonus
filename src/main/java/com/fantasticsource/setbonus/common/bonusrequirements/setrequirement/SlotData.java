package com.fantasticsource.setbonus.common.bonusrequirements.setrequirement;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import com.fantasticsource.mctools.items.RegistryRegexItemFilter;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.client.ClientData;
import com.fantasticsource.setbonus.server.ServerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SlotData
{
    public ArrayList<Integer> slots = new ArrayList<>(); //Because multiple slot options can be defined
    public LinkedHashMap<String, RegistryRegexItemFilter> involvedEquips = new LinkedHashMap<>();


    private SlotData()
    {
    }

    public static SlotData getInstance(String slotsAndEquipment, LinkedHashMap<String, RegistryRegexItemFilter> setdataEquipIDTracker, Side side)
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
        LinkedHashMap<String, Equip> equipment = side == Side.SERVER ? ServerData.equipment : ClientData.equipment;
        for (String equipID : tokens[1].split("[|]"))
        {
            equipID = equipID.trim();
            Equip equip = equipment.get(equipID);
            if (equip == null)
            {
                System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.slotBadEquipID", equipID, slotsAndEquipment));
                return null;
            }

            result.involvedEquips.put(equip.parsedString, equip.filter);
            if (setdataEquipIDTracker != null) setdataEquipIDTracker.put(equipID, equip.filter);
        }


        return result;
    }


    public static ItemStack getStackInSlot(EntityPlayer player, int slot)
    {
        if (slot == -1)
        {
            //Mainhand
            slot = player.inventory.currentItem;
        }

        if (slot > -1)
        {
            //Vanilla slot
            return player.inventory.getStackInSlot(slot);
        }
        else
        {
            //Numbered baubles slot
            return BaublesApi.getBaublesHandler(player).getStackInSlot(slot - Integer.MIN_VALUE - 1);
        }
    }

    public int equipped(EntityPlayer player, ArrayList<Integer> blocked, boolean allowEmptyStackIfMatching)
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


            for (RegistryRegexItemFilter filter : involvedEquips.values())
            {
                if (stack.getMaxStackSize() == 1 && filter.matches(stack)) return slot;
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

        else if (slotString.equals("bauble_amulet"))
        {
            for (int i : BaubleType.AMULET.getValidSlots()) result.add(Integer.MIN_VALUE + 1 + i);
        }
        else if (slotString.equals("bauble_ring"))
        {
            for (int i : BaubleType.RING.getValidSlots()) result.add(Integer.MIN_VALUE + 1 + i);
        }
        else if (slotString.equals("bauble_belt"))
        {
            for (int i : BaubleType.BELT.getValidSlots()) result.add(Integer.MIN_VALUE + 1 + i);
        }
        else if (slotString.equals("bauble_head"))
        {
            for (int i : BaubleType.HEAD.getValidSlots()) result.add(Integer.MIN_VALUE + 1 + i);
        }
        else if (slotString.equals("bauble_body"))
        {
            for (int i : BaubleType.BODY.getValidSlots()) result.add(Integer.MIN_VALUE + 1 + i);
        }
        else if (slotString.equals("bauble_charm"))
        {
            for (int i : BaubleType.CHARM.getValidSlots()) result.add(Integer.MIN_VALUE + 1 + i);
        }
        else if (slotString.equals("bauble_trinket"))
        {
            for (int i : BaubleType.TRINKET.getValidSlots()) result.add(Integer.MIN_VALUE + 1 + i);
        }

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
}
