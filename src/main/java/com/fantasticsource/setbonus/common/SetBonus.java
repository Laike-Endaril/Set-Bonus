package com.fantasticsource.setbonus.common;

import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.mctools.items.ItemFilter;
import com.fantasticsource.setbonus.config.SyncedConfig;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;
import java.util.Map;

import static net.minecraft.item.ItemStack.DECIMALFORMAT;
import static net.minecraftforge.fml.common.Mod.EventHandler;

@Mod(modid = SetBonus.MODID, name = SetBonus.NAME, version = SetBonus.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.004,)")
public class SetBonus
{
    public static final String MODID = "setbonus";
    public static final String NAME = "Set Bonus";
    public static final String VERSION = "1.12.2.002";

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        Network.init();

        MinecraftForge.EVENT_BUS.register(SetBonus.class);
        MinecraftForge.EVENT_BUS.register(SyncedConfig.class);
        MinecraftForge.EVENT_BUS.register(ServerTickTimer.class);
    }

    @EventHandler
    public static void postInit(FMLPostInitializationEvent event)
    {
        Data.update();
    }

    @EventHandler
    public static void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new Commands());
    }

    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @SubscribeEvent
    public static void calcConfigs(ConfigChangedEvent.PostConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) Data.update();
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.START)
        {
            EntityPlayer player = event.player;

            if (Tools.posMod(ServerTickTimer.currentTick(), 20) == Tools.posMod(player.getUniqueID().getLeastSignificantBits(), 20))
            {
                updateBonuses(player);
            }
        }
    }

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
                            String color = count == 0 ? "§4" : count == max ? "§a" : "§e";
                            tooltip.add(color + "§l=== " + set.getName() + " (" + count + "/" + max + ") ===");
                            for (Map.Entry<Integer, BonusData> bonusEntry : set.bonuses.entrySet())
                            {
                                int required = bonusEntry.getKey();
                                color = count >= required ? "§a" : "§4";
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

    private static void updateBonuses(EntityPlayer player)
    {
        for (SetData data : Data.sets.values())
        {
            data.updateBonuses(player);
        }
    }
}
