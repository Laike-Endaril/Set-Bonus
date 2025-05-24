package com.fantasticsource.setbonus.server;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.Network;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.LinkedHashMap;

public class ServerBonus extends Bonus
{
    public static boolean changed, save;

    private LinkedHashMap<EntityPlayer, BonusInstance> instances = new LinkedHashMap<>();


    public static void dropAll()
    {
        //Needs to be done right before new configs are applied, to remove any eg. potion effects (because they might not be part of the bonus anymore)
        //Also called when a server is stopping, to remove any bonuses on players before they get unloaded, in case said bonuses don't exist next time the server starts due to config changes
        for (Bonus bonus : SetBonusData.SERVER_DATA.bonuses)
        {
            for (BonusInstance data : ((ServerBonus) bonus).instances.values()) data.update(false);
        }
        SetBonusData.SERVER_DATA.bonuses.clear();
    }

    public static void updateBonuses(EntityPlayerMP player, boolean forceNew)
    {
        changed = false;
        save = false;

        for (Bonus bonus : SetBonusData.SERVER_DATA.bonuses) ((ServerBonus) bonus).update(player, forceNew);

        while (changed)
        {
            changed = false;
            for (Bonus bonus : SetBonusData.SERVER_DATA.bonuses) ((ServerBonus) bonus).update(player, false);
        }

        if (save) saveDiscoveries(player);
    }

    public void update(EntityPlayerMP player, boolean forceNew)
    {
        if (forceNew)
        {
            BonusInstance instance = new BonusInstance(player, this);
            instances.put(player, instance);
            instance.update();
        }
        else instances.computeIfAbsent(player, k -> new BonusInstance(player, this)).update();
    }

    @Nonnull
    public BonusInstance getBonusInstance(EntityPlayerMP player)
    {
        return instances.computeIfAbsent(player, k -> new BonusInstance(player, this));
    }


    public static void saveDiscoveries(EntityPlayerMP player)
    {
        World world = player.world;
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

            for (Bonus bonus : SetBonusData.SERVER_DATA.bonuses)
            {
                BonusInstance data = ((ServerBonus) bonus).instances.get(player);
                if (data != null && data.discovered) writer.write(bonus.id + "\r\n");
            }

            writer.close();
        }
        catch (IOException e)
        {
            MCTools.crash(e, 901, false);
        }
    }

    public static void loadDiscoveries(EntityPlayerMP player)
    {
        World world = player.world;
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
                Bonus bonus = null;
                for (Bonus bonus2 : SetBonusData.SERVER_DATA.bonuses)
                {
                    if (bonus2.id.equals(string))
                    {
                        bonus = bonus2;
                        break;
                    }
                }
                if (bonus != null && bonus.discoveryMode != MODE_GLOBALLY_HIDDEN) ((ServerBonus) bonus).getBonusInstance(player).discovered = true;
                string = reader.readLine();
            }

            reader.close();
        }
        catch (IOException e)
        {
            MCTools.crash(e, 901, false);
        }

        //To remove the saved discovery of any removed bonuses
        saveDiscoveries(player);
    }

    public static void clearMem(EntityPlayer player)
    {
        for (Bonus bonus : SetBonusData.SERVER_DATA.bonuses)
        {
            BonusInstance data = ((ServerBonus) bonus).instances.get(player);
            if (data != null)
            {
                data.update(false);
                ((ServerBonus) bonus).instances.remove(player);
            }
        }
    }


    public class BonusInstance
    {
        public boolean active, discovered;
        private EntityPlayerMP player;
        private ServerBonus bonus;

        private BonusInstance(EntityPlayerMP player, ServerBonus bonus)
        {
            this.player = player;
            this.bonus = bonus;
        }

        public BonusInstance update()
        {
            for (ABonusRequirement requirement : requirements)
            {
                if (requirement.active(player) < requirement.required())
                {
                    update(false);
                    return this;
                }
            }

            update(true);
            return this;
        }

        private void update(boolean activate)
        {
            if (activate)
            {
                if (!active)
                {
                    //Activating
                    changed = true;
                    active = true;

                    if (bonus.discoveryMode != MODE_GLOBALLY_HIDDEN && !discovered)
                    {
                        discovered = true;
                        Network.WRAPPER.sendTo(new Network.DiscoverBonusPacket(bonus), player);
                        save = true;
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
                    changed = true;
                    active = false;

                    for (ABonusElement element : bonusElements) element.deactivate(player);
                }
            }
        }
    }
}
