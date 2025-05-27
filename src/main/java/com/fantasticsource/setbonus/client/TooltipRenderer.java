package com.fantasticsource.setbonus.client;

import com.fantasticsource.mctools.items.AdvancedItemFilter;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementAttributeModifier;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementEnchantment;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementPotionEffect;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SetRequirement;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SlotData;
import com.fantasticsource.setbonus.config.SetBonusConfig;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

import static net.minecraft.util.text.TextFormatting.*;

public class TooltipRenderer
{
    protected static ArrayList<AdvancedItemFilter> itemTooltipBlacklist = new ArrayList<>();
    protected static ItemStack recentlyDenied = null, recentlyDisplayed = null;
    protected static ArrayList<String> recentlyAddedTooltips = new ArrayList<>();


    public static void update()
    {
        itemTooltipBlacklist.clear();
        for (String string : SetBonusConfig.clientSettings.itemTooltipBlacklist)
        {
            itemTooltipBlacklist.add(AdvancedItemFilter.getInstance(string));
        }
        recentlyDenied = null;

        recentlyDisplayed = null;
        recentlyAddedTooltips.clear();
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tooltips(ItemTooltipEvent event)
    {
        if (!SetBonusConfig.clientSettings.enableTooltips) return;

        EntityPlayer player = event.getEntityPlayer();
        if (player == null) return;


        ItemStack stack = event.getItemStack();
        ArrayList<String> list = new ArrayList<>();


        boolean clientThread = Thread.currentThread().getName().equals("Client thread");
        if (clientThread)
        {
            if (recentlyDisplayed == stack && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            {
                if (recentlyAddedTooltips.size() > 0)
                {
                    event.getToolTip().add("");
                    event.getToolTip().addAll(recentlyAddedTooltips);
                }
                return;
            }


            if (recentlyDenied == stack) return;
            for (AdvancedItemFilter filter : itemTooltipBlacklist)
            {
                if (filter.matches(stack))
                {
                    recentlyDenied = stack;
                    return;
                }
            }


            recentlyDisplayed = stack;
            recentlyAddedTooltips.clear();
        }


        boolean showTooltip;
        for (Set set : SetBonusData.CLIENT_DATA.sets)
        {
            showTooltip = false;
            for (SlotData slotData : set.slotData)
            {
                for (Equip equip : slotData.involvedEquips)
                {
                    if (equip.filter.matches(stack))
                    {
                        showTooltip = true;
                        break;
                    }
                }
                if (showTooltip) break;
            }


            if (showTooltip)
            {
                //This item is part of a known set; add set tooltip for the set we're currently looking at
                int count = set.getNumberEquipped(player);
                int max = set.getMaxNumber();
                String color = "" + (count == 0 ? RED : count == max ? GREEN : YELLOW);
                list.add(color + BOLD + "=== " + I18n.translateToLocal(set.name) + " (" + count + "/" + max + ") ===");
                for (Bonus bonus : SetBonusData.CLIENT_DATA.bonuses)
                {
                    int req = 0;
                    boolean otherReqs = false;

                    for (ABonusRequirement requirement : bonus.requirements)
                    {
                        if (requirement instanceof SetRequirement)
                        {
                            SetRequirement setRequirement = ((SetRequirement) requirement);
                            if (setRequirement.set.id.equals(set.id))
                            {
                                req = Tools.max(req, setRequirement.required());
                            }
                            else otherReqs = true;
                        }
                        else otherReqs = true;
                    }


                    if (req > 0)
                    {
                        //This item fills a requirement for the particular bonus we're looking at, so add bonus tooltip(s) for the set bonus we're currently looking at
                        ClientBonus.BonusInstance bonusInstance = ((ClientBonus) bonus).getBonusInstance(player);

                        color = "";
                        int active = set.getNumberEquipped(player);

                        if (bonusInstance.active) color += GREEN; //All requirements met
                        else
                        {
                            if (active >= req) color += DARK_PURPLE; //Set requirements are met, but non-set requirements are not met
                            else if (active == 0) color += RED; //No set requirements met
                            else color += YELLOW; //Some set requirements met
                        }

                        list.add(color + " (" + active + "/" + req + ")" + (otherReqs ? "*" : "") + " " + I18n.translateToLocal(bonus.name));


                        if (SetBonusConfig.clientSettings.enableAttributeModifierTooltips || SetBonusConfig.clientSettings.enablePotionEffectTooltips || SetBonusConfig.clientSettings.enableEnchantmentTooltips)
                        {
                            //Detailed bonus tooltips
                            ArrayList<BonusElementAttributeModifier> bonusElementAttributeModifiers = new ArrayList<>();
                            ArrayList<BonusElementPotionEffect> bonusElementPotionEffects = new ArrayList<>();
                            ArrayList<BonusElementEnchantment> bonusElementEnchantments = new ArrayList<>();
                            for (ABonusElement element : bonus.bonusElements)
                            {
                                if (element instanceof BonusElementAttributeModifier) bonusElementAttributeModifiers.add((BonusElementAttributeModifier) element);
                                else if (element instanceof BonusElementPotionEffect) bonusElementPotionEffects.add((BonusElementPotionEffect) element);
                                else if (element instanceof BonusElementEnchantment) bonusElementEnchantments.add((BonusElementEnchantment) element);
                            }
                            if (SetBonusConfig.clientSettings.enableAttributeModifierTooltips)
                            {
                                for (BonusElementAttributeModifier bonusElementAttributeModifier : bonusElementAttributeModifiers)
                                {
                                    for (String line : bonusElementAttributeModifier.tooltips()) list.add("  " + line);
                                }
                            }
                            if (SetBonusConfig.clientSettings.enablePotionEffectTooltips)
                            {
                                for (BonusElementPotionEffect bonusElementPotionEffect : bonusElementPotionEffects)
                                {
                                    for (String line : bonusElementPotionEffect.tooltips()) list.add("  " + line);
                                }
                            }
                            if (SetBonusConfig.clientSettings.enableEnchantmentTooltips)
                            {
                                for (BonusElementEnchantment bonusElementEnchantment : bonusElementEnchantments)
                                {
                                    //TODO change how the enchantment bonus displays based on this item, where it is, where it could be, and whether it has it applied?
                                    for (String line : bonusElementEnchantment.tooltips()) list.add("  " + line);
                                }
                            }
                        }
                    }
                }
                list.add("");
            }
        }


        if (list.size() > 0)
        {
            event.getToolTip().add("");
            event.getToolTip().addAll(list);
        }
        if (clientThread) recentlyAddedTooltips = list;
    }
}
