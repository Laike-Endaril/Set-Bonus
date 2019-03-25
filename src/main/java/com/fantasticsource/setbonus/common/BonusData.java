package com.fantasticsource.setbonus.common;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class BonusData
{
    public LinkedHashMap<SetData, Integer> setRequirements = new LinkedHashMap<>();
    public LinkedHashMap<String, DoubleRequirement> attributeRequirements = new LinkedHashMap<>();

    public ArrayList<ABonusElement> bonusElements = new ArrayList<>();

    private LinkedHashMap<EntityPlayer, BonusInstance> instances = new LinkedHashMap<>();

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

                for (Map.Entry<String, Bonus> entry : Data.bonuses.entrySet())
                {
                    BonusInstance data = entry.getValue().data.instances.get(player);
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
                    Bonus bonus = Data.bonuses.get(string);
                    if (bonus != null)
                    {
                        bonus.data.getInstance(player).identified = true;
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
        for (Bonus bonus : Data.bonuses.values())
        {
            for (BonusInstance data : bonus.data.instances.values()) data.update(false);
        }
        Data.bonuses.clear();
    }

    public static void deactivateBonuses(EntityPlayer player)
    {
        for (Bonus bonus : Data.bonuses.values())
        {
            BonusInstance data = bonus.data.instances.get(player);
            if (data != null) data.update(false);
        }
    }

    public static void updateBonuses(EntityPlayer player)
    {
        //Happens once per second on player tick event
        for (Bonus bonus : Data.bonuses.values()) bonus.data.update(player);
    }

    public BonusInstance getInstance(EntityPlayer player)
    {
        return instances.computeIfAbsent(player, k -> new BonusInstance(player));
    }

    public void update(EntityPlayer player)
    {
        BonusInstance data = instances.computeIfAbsent(player, k -> new BonusInstance(player));

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

                    for (ABonusElement element : bonusElements) element.activate(player);
                }
                else
                {
                    //Remaining active
                    for (ABonusElement element : bonusElements) element.updateActive(player);
                }
            }
            else
            {
                if (active)
                {
                    //Deactivating
                    active = false;
                    for (ABonusElement element : bonusElements) element.deactivate(player);
                }
            }
        }
    }
}
