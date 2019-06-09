package com.fantasticsource.setbonus.server;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.Network;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ServerBonus extends Bonus
{
    public static boolean changed;
    private LinkedHashMap<EntityPlayer, BonusInstance> instances = new LinkedHashMap<>();

    public static ServerBonus getInstance(String parsableBonus)
    {
        return (ServerBonus) Bonus.getInstance(parsableBonus, Side.SERVER);
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

            for (Map.Entry<String, ServerBonus> entry : ServerData.bonuses.entrySet())
            {
                BonusInstance data = entry.getValue().instances.get(player);
                if (data != null && data.discovered) writer.write(entry.getKey() + "\r\n");
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
                ServerBonus bonus = ServerData.bonuses.get(string);
                if (bonus != null) bonus.getBonusInstance(player).discovered = true;
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

    public static void dropAll()
    {
        //Needs to be done right before new configs are applied, to remove any eg. potion effects (because they might not be part of the bonus anymore)
        //Also called when a server is stopping, to remove any bonuses on players before they get unloaded, in case said bonuses don't exist next time the server starts due to config changes
        for (ServerBonus bonus : ServerData.bonuses.values())
        {
            for (BonusInstance data : bonus.instances.values()) data.update(false);
        }
        ServerData.bonuses.clear();
    }

    public static void clearMem(EntityPlayer player)
    {
        for (ServerBonus bonus : ServerData.bonuses.values())
        {
            BonusInstance data = bonus.instances.get(player);
            if (data != null)
            {
                data.update(false);
                bonus.instances.remove(player);
            }
        }
    }

    public static void updateBonuses(EntityPlayerMP player)
    {
        //Happens once per second on player tick event
        changed = true;
        while (changed)
        {
            changed = false;
            for (ServerBonus bonus : ServerData.bonuses.values()) bonus.update(player);
        }
    }


    @Nonnull
    public BonusInstance getBonusInstance(EntityPlayerMP player)
    {
        return instances.computeIfAbsent(player, k -> new BonusInstance(player, this));
    }

    public void update(EntityPlayerMP player)
    {
        instances.computeIfAbsent(player, k -> new BonusInstance(player, this)).update();
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
            for (ABonusRequirement requirement : bonusRequirements)
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

                    if (!discovered)
                    {
                        discovered = true;
                        Network.WRAPPER.sendTo(new Network.DiscoverBonusPacket(bonus), (EntityPlayerMP) player);
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
                    changed = true;
                    active = false;

                    for (ABonusElement element : bonusElements) element.deactivate(player);
                }
            }
        }
    }
}
