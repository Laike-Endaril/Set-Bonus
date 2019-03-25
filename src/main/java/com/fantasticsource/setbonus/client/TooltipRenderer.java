package com.fantasticsource.setbonus.client;

import com.fantasticsource.mctools.items.ItemFilter;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.common.BonusData;
import com.fantasticsource.setbonus.common.Data;
import com.fantasticsource.setbonus.common.Set;
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
        for (Set set : Data.sets.values())
        {
            for (ItemFilter filter : set.data.involvedEquips.values())
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
                    int count = set.data.getNumberEquipped(player);
                    int max = set.data.getMaxNumber();
                    String color = "" + (count == 0 ? RED : count == max ? GREEN : YELLOW);
                    tooltip.add(color + BOLD + "=== " + set.name + " (" + count + "/" + max + ") ===");
                    for (BonusData bonusData : Data.bonuses.values())
                    {
                        if (bonusData.setRequirements.keySet().contains(set.data))
                        {
                            BonusData.BonusInstance bonusInstance = bonusData.getData(player);
                            color = "" + (bonusInstance != null && bonusInstance.active ? GREEN : RED);
                            tooltip.add(color + " " + bonusData.name);
                        }
                    }
                    tooltip.add("");
                }
            }
        }
    }
}
