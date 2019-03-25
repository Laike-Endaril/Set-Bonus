package com.fantasticsource.setbonus.common;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.setbonus.SetBonus;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class BonusData
{
    public static int
            MODE_DISCOVERABLE = 0,
            MODE_IDENTIFIABLE = 1,
            MODE_GLOBALLY_KNOWN = 2;


    public String name;
    public int discoveryMode;
    public LinkedHashMap<SetData, Integer> setRequirements = new LinkedHashMap<>();
    public LinkedHashMap<String, DoubleRequirement> attributeRequirements = new LinkedHashMap<>();
    public Multimap<String, AttributeModifier> modifiers = ArrayListMultimap.create();
    public ArrayList<PotionEffect> potions = new ArrayList<>();

    private LinkedHashMap<EntityPlayer, BonusInstance> bonusData = new LinkedHashMap<>();

    public static void saveDiscoveries(EntityPlayer player)
    {
        World world = player.world;
        if (!world.isRemote)
        {
            try
            {
                String string = MCTools.getDataDir(world.getMinecraftServer()) + SetBonus.MODID + File.separator;
                File file = new File(string);
                if (!file.exists()) file.mkdir();

                string += "discoveries" + File.separator;
                file = new File(string);
                if (!file.exists()) file.mkdir();

                string += player.getCachedUniqueIdString() + ".txt";
                file = new File(string);
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));

                for (Map.Entry<String, BonusData> entry : Data.bonuses.entrySet())
                {
                    BonusInstance data = entry.getValue().bonusData.get(player);
                    if (data != null && data.identified) writer.write(entry.getKey());
                }

                writer.close();
            }
            catch (IOException e)
            {
                MCTools.crash(e, 901, false);
            }
        }
    }

    public static void loadDiscoveries(EntityPlayer player)
    {
        World world = player.world;
        if (!world.isRemote)
        {
            try
            {
                String string = MCTools.getDataDir(world.getMinecraftServer()) + SetBonus.MODID + File.separator;
                File file = new File(string);
                if (!file.exists()) return;

                string += "discoveries" + File.separator;
                file = new File(string);
                if (!file.exists()) return;

                string += player.getCachedUniqueIdString() + ".txt";
                file = new File(string);
                if (!file.exists()) return;

                BufferedReader reader = new BufferedReader(new FileReader(file));

                string = reader.readLine();
                while (string != null && !string.equals(""))
                {
                    BonusData bonusData = Data.bonuses.get(string);
                    if (bonusData != null)
                    {
                        bonusData.getData(player).identified = true;
                    }
                    string = reader.readLine();
                }

                reader.close();
            }
            catch (IOException e)
            {
                MCTools.crash(e, 901, false);
            }
        }

        //To remove the saved discovery of any removed bonuses
        saveDiscoveries(player);
    }

    public static void dropAll()
    {
        //Needs to be done right before new configs are applied, to remove any eg. potion effects (because they might not be part of the bonus anymore)
        //Also called when a server is stopping, to remove any bonuses on players before they get unloaded, in case said bonuses don't exist next time the server starts due to config changes
        for (BonusData bonusData : Data.bonuses.values())
        {
            for (BonusInstance data : bonusData.bonusData.values()) data.update(false);
        }
        Data.bonuses.clear();
    }

    public static void deactivateBonuses(EntityPlayer player)
    {
        for (BonusData bonusData : Data.bonuses.values())
        {
            BonusInstance data = bonusData.bonusData.get(player);
            if (data != null) data.update(false);
        }
    }

    public static void updateBonuses(EntityPlayer player)
    {
        //Happens once per second on player tick event
        for (BonusData bonusData : Data.bonuses.values()) bonusData.update(player);
    }

    public BonusInstance getData(EntityPlayer player)
    {
        BonusInstance result = bonusData.computeIfAbsent(player, k -> new BonusInstance(player));
        return result;
    }

    public void update(EntityPlayer player)
    {
        BonusInstance data = bonusData.computeIfAbsent(player, k -> new BonusInstance(player));

        for (Map.Entry<SetData, Integer> entry : setRequirements.entrySet())
        {
            if (entry.getKey().getNumberEquipped(player) < entry.getValue())
            {
                data.update(false);
                return;
            }
        }

        for (Map.Entry<String, DoubleRequirement> entry : attributeRequirements.entrySet())
        {
            IAttributeInstance attributeInstance = player.getAttributeMap().getAttributeInstanceByName(entry.getKey());
            if (attributeInstance == null)
            {
                data.update(false);
                return;
            }
            if (!entry.getValue().check(attributeInstance.getAttributeValue()))
            {
                data.update(false);
                return;
            }
        }

        data.update(true);
    }


    public class BonusInstance
    {
        public boolean active, identified;
        EntityPlayer player;


        public BonusInstance(EntityPlayer player)
        {
            this.player = player;

            active = true;
            if (attributeRequirements.size() > 0) active = false;
            else
            {
                for (Map.Entry<SetData, Integer> entry : setRequirements.entrySet())
                {
                    boolean check = entry.getValue() <= 0;
                    active &= check;
                }
            }

            identified = active;
        }


        private void update(boolean activate)
        {
            if (activate)
            {
                if (!active)
                {
                    //Activating
                    active = true;
                    if (!identified)
                    {
                        identified = true;
                        saveDiscoveries(player);
                    }
                    for (PotionEffect potion : potions) player.addPotionEffect(potion);
                    player.getAttributeMap().applyAttributeModifiers(modifiers);
                }
                else
                {
                    //Remaining active
                    for (PotionEffect potion : potions)
                    {
                        PotionEffect potionEffect = player.getActivePotionEffect(potion.getPotion());
                        if (potionEffect == null) player.addPotionEffect(potion);
                    }
                }
            }
            else
            {
                if (active)
                {
                    //Deactivating
                    active = false;
                    for (PotionEffect potion : potions) player.removePotionEffect(potion.getPotion());
                    player.getAttributeMap().removeAttributeModifiers(modifiers);
                }
            }
        }
    }
}
