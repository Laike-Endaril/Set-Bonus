package com.fantasticsource.setbonus.client;

import com.fantasticsource.mctools.items.RegistryRegexItemFilter;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.setbonus.common.bonuselements.EnchantmentBonus;
import com.fantasticsource.setbonus.common.bonuselements.ModifierBonus;
import com.fantasticsource.setbonus.common.bonuselements.PotionBonus;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SetRequirement;
import com.fantasticsource.setbonus.config.SetBonusConfig;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.text.TextFormatting.*;

public class TooltipRenderer
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tooltips(ItemTooltipEvent event)
    {
        if (!SetBonusConfig.clientSettings.enableTooltips) return;

        EntityPlayer player = event.getEntityPlayer();
        if (player == null) return;

        ItemStack stack = event.getItemStack();
        List<String> tooltip = event.getToolTip();

        boolean edited = false;
        for (Set set : ClientData.sets.values())
        {
            for (RegistryRegexItemFilter filter : set.involvedEquips.values())
            {
                if (filter.matches(stack))
                {
                    //This item is part of a known set; add set tooltip for the set we're currently looking at
                    if (!edited)
                    {
                        edited = true;
                        tooltip.add("");
//                        tooltip.add("" + LIGHT_PURPLE + UNDERLINE + I18n.translateToLocalFormatted(SetBonus.MODID + ".tooltip.pressDetailKey"));
//                        tooltip.add("");
                    }
                    int count = set.getNumberEquipped(player);
                    int max = set.getMaxNumber();
                    String color = "" + (count == 0 ? RED : count == max ? GREEN : YELLOW);
                    tooltip.add(color + BOLD + "=== " + set.name + " (" + count + "/" + max + ") ===");
                    for (ClientBonus bonus : ClientData.bonuses.values())
                    {
                        int req = 0;
                        boolean otherReqs = false;

                        for (ABonusRequirement requirement : bonus.bonusRequirements)
                        {
                            if (requirement instanceof SetRequirement)
                            {
                                SetRequirement setRequirement = ((SetRequirement) requirement);
                                if (setRequirement.set.id.equals(set.id))
                                {
                                    req = Tools.max(req, setRequirement.num);
                                }
                                else otherReqs = true;
                            }
                            else otherReqs = true;
                        }


                        if (req > 0)
                        {
                            //This item fills a requirement for the particular bonus we're looking at, so add bonus tooltip(s) for the set bonus we're currently looking at
                            ClientBonus.BonusInstance bonusInstance = bonus.getBonusInstance(player);

                            color = "";
                            int active = set.getNumberEquipped(player);

                            if (bonusInstance.active) color += GREEN; //All requirements met
                            else
                            {
                                if (active >= req) color += DARK_PURPLE; //Set requirements are met, but non-set requirements are not met
                                else if (active == 0) color += RED; //No set requirements met
                                else color += YELLOW; //Some set requirements met
                            }

                            tooltip.add(color + " (" + active + "/" + req + ")" + (otherReqs ? "*" : "") + " " + bonus.name);


                            if (SetBonusConfig.clientSettings.enableAttributeModifierTooltips || SetBonusConfig.clientSettings.enablePotionEffectTooltips || SetBonusConfig.clientSettings.enableEnchantmentTooltips)
                            {
                                //Detailed bonus tooltips
                                ArrayList<ModifierBonus> modifierBonuses = new ArrayList<>();
                                ArrayList<PotionBonus> potionBonuses = new ArrayList<>();
                                ArrayList<EnchantmentBonus> enchantmentBonuses = new ArrayList<>();
                                for (ABonusElement element : bonus.bonusElements)
                                {
                                    if (element instanceof ModifierBonus) modifierBonuses.add((ModifierBonus) element);
                                    else if (element instanceof PotionBonus) potionBonuses.add((PotionBonus) element);
                                    else if (element instanceof EnchantmentBonus) enchantmentBonuses.add((EnchantmentBonus) element);
                                }
                                if (SetBonusConfig.clientSettings.enableAttributeModifierTooltips)
                                {
                                    for (ModifierBonus modifierBonus : modifierBonuses)
                                    {
                                        for (String line : modifierBonus.tooltips()) tooltip.add(color + "  " + line);
                                    }
                                }
                                if (SetBonusConfig.clientSettings.enablePotionEffectTooltips)
                                {
                                    for (PotionBonus potionBonus : potionBonuses)
                                    {
                                        for (String line : potionBonus.tooltips()) tooltip.add(color + "  " + line);
                                    }
                                }
                                if (SetBonusConfig.clientSettings.enableEnchantmentTooltips)
                                {
                                    for (EnchantmentBonus enchantmentBonus : enchantmentBonuses)
                                    {
                                        //TODO change how the enchantment bonus displays based on this item, where it is, where it could be, and whether it has it applied?
                                        for (String line : enchantmentBonus.tooltips()) tooltip.add(color + "  " + line);
                                    }
                                }
                            }
                        }
                    }
                    tooltip.add("");
                }
            }
        }
    }
}
