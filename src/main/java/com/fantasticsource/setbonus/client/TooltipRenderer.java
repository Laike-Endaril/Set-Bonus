package com.fantasticsource.setbonus.client;

import com.fantasticsource.mctools.items.ItemFilter;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.Data;
import com.fantasticsource.setbonus.common.SetBonus;
import com.fantasticsource.setbonus.common.SetData;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

import static net.minecraft.util.text.TextFormatting.*;

public class TooltipRenderer
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tooltips(ItemTooltipEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null) return;

        List<String> tooltip = event.getToolTip();

        boolean edited = false;
        for (SetData set : Data.sets.values())
        {
            for (ItemFilter filter : set.involvedEquips.values())
            {
                if (filter.matches(event.getItemStack()))
                {
                    if (!edited)
                    {
                        edited = true;
                        tooltip.add("");
                        tooltip.add("" + LIGHT_PURPLE + UNDERLINE + I18n.format(SetBonus.MODID + ".tooltip.pressDetailKey"));
                        tooltip.add("");
                    }
                    int count = set.getNumberEquipped(player);
                    int max = set.getMaxNumber();
                    String color = "" + (count == 0 ? RED : count == max ? GREEN : YELLOW);
                    tooltip.add(color + BOLD + "=== " + set.name + " (" + count + "/" + max + ") ===");
                    for (Bonus bonus : Bonus.bonusMap.values())
                    {
                        if (bonus.setRequirements.keySet().contains(set))
                        {
                            Bonus.BonusData bonusData = bonus.bonusData.get(player);
                            color = "" + (bonusData != null && bonusData.active ? GREEN : RED);
                            tooltip.add(color + " " + bonus.name);
                        }
                    }
                    tooltip.add("");
                }
            }
        }
    }
}
