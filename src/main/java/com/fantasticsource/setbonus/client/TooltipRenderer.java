package com.fantasticsource.setbonus.client;

import com.fantasticsource.mctools.items.ItemFilter;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.Data;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SetRequirement;
import com.fantasticsource.tools.Tools;
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
                    for (Bonus bonus : Data.bonuses.values())
                    {
                        int req = 0;
                        boolean otherReqs = false;

                        for (ABonusRequirement requirement : bonus.bonusRequirements)
                        {
                            if (requirement instanceof SetRequirement)
                            {
                                SetRequirement setRequirement = ((SetRequirement) requirement);
                                if (setRequirement.set == set)
                                {
                                    req = Tools.max(req, setRequirement.num);
                                }
                                else otherReqs = true;
                            }
                            else otherReqs = true;
                        }

                        Bonus.BonusInstance bonusInstance = bonus.getBonusInstance(player);

                        color = "";
                        if (bonusInstance.active) color += GREEN; //All requirements met
                        else
                        {
                            int active = set.getNumberEquipped(player);

                            if (active >= req) color += DARK_PURPLE; //Set requirements are met, but non-set requirements are not met
                            else if (active == 0) color += RED; //No set requirements met
                            else color += YELLOW; //Some set requirements met
                        }

                        tooltip.add(color + " " + bonus.name + (otherReqs ? "*" : ""));
                    }
                    tooltip.add("");
                }
            }
        }
    }
}
