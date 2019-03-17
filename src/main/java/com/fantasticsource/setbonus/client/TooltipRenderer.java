package com.fantasticsource.setbonus.client;

import com.fantasticsource.mctools.items.ItemFilter;
import com.fantasticsource.setbonus.common.BonusData;
import com.fantasticsource.setbonus.common.Data;
import com.fantasticsource.setbonus.common.SetData;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Map;

import static net.minecraft.item.ItemStack.DECIMALFORMAT;
import static net.minecraft.util.text.TextFormatting.*;

public class TooltipRenderer
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tooltips(ItemTooltipEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        if (player != null)
        {
            ItemStack stack = event.getItemStack();
            String equipID = null;
            for (Map.Entry<String, ItemFilter> entry : Data.equipment.entrySet())
            {
                if (entry.getValue().matches(stack))
                {
                    equipID = entry.getKey();
                    break;
                }
            }
            if (equipID == null) return;

            for (Map.Entry<String, ItemFilter> equipEntry : Data.equipment.entrySet())
            {
                if (equipEntry.getValue().matches(stack))
                {
                    String equip = equipEntry.getKey();
                    for (Map.Entry<String, SetData> setEntry : Data.sets.entrySet())
                    {
                        SetData set = setEntry.getValue();
                        if (set.involvedEquipIDs.contains(equip))
                        {
                            //Display tooltip for set
                            List<String> tooltip = event.getToolTip();
                            tooltip.add("");
                            int count = set.getNumberEquipped(player);
                            int max = set.getMaxNumber();
                            String color = "" + (count == 0 ? RED : count == max ? GREEN : YELLOW);
                            tooltip.add(color + BOLD + "=== " + set.getName() + " (" + count + "/" + max + ") ===");
                            for (Map.Entry<Integer, BonusData> bonusEntry : set.bonuses.entrySet())
                            {
                                int required = bonusEntry.getKey();
                                color = "" + (count >= required ? GREEN : RED);
                                BonusData bonus = bonusEntry.getValue();
                                for (AttributeModifier modifier : bonus.modifiers.values())
                                {
                                    int operation = modifier.getOperation();
                                    double amount = modifier.getAmount();
                                    if (amount != 0)
                                    {
                                        tooltip.add(color + count + "/" + required + ": " + I18n.translateToLocalFormatted("attribute.modifier." + (amount < 0 ? "take" : "plus") + "." + operation, DECIMALFORMAT.format(operation == 0 ? amount : amount * 100), I18n.translateToLocal("attribute.name." + modifier.getName())));
                                    }
                                }
                                for (PotionEffect potionEffect : bonus.potions)
                                {
                                    int level = potionEffect.getAmplifier();
                                    if (level >= 0) level++;
                                    tooltip.add(color + count + "/" + required + ": " + I18n.translateToLocal(potionEffect.getEffectName()) + (level == 1 ? "" : " " + level));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
