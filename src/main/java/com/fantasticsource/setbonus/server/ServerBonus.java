package com.fantasticsource.setbonus.server;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.common.Network;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class ServerBonus
{
    public static int
            MODE_DISCOVERABLE = 0,
            MODE_GLOBALLY_KNOWN = 1;


    public String parsedString, id, name;
    public int discoveryMode;


    public ArrayList<ABonusRequirement> bonusRequirements = new ArrayList<>();
    public ArrayList<ABonusElement> bonusElements = new ArrayList<>();

    private LinkedHashMap<EntityPlayer, BonusInstance> instances = new LinkedHashMap<>();


    private ServerBonus()
    {
    }

    public static ServerBonus getInstance(String parsableBonus)
    {
        ServerBonus result = new ServerBonus();

        String[] tokens = parsableBonus.split(",");
        if (tokens.length < 3)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.notEnoughBonusArgs", parsableBonus));
            return null;
        }

        result.id = tokens[0].trim();
        if (result.id.equals(""))
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.noBonusID", parsableBonus));
            return null;
        }

        result.name = tokens[1].trim();

        try
        {
            result.discoveryMode = Integer.parseInt(tokens[2].trim());
        }
        catch (NumberFormatException e)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.bonusDiscoveryMode", parsableBonus));
            return null;
        }
        if (result.discoveryMode < 0 || result.discoveryMode > 1)
        {
            System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.bonusDiscoveryMode", parsableBonus));
            return null;
        }

        for (String requirementString : Arrays.copyOfRange(tokens, 3, tokens.length))
        {
            ABonusRequirement requirement = ABonusRequirement.parse(requirementString);

            if (requirement == null)
            {
                System.err.println(I18n.translateToLocalFormatted(SetBonus.MODID + ".error.unknownBonusReq", parsableBonus));
                return null;
            }

            result.bonusRequirements.add(requirement);
        }

        result.parsedString = parsableBonus;
        return result;
    }

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

                for (Map.Entry<String, ServerBonus> entry : ServerData.bonuses.entrySet())
                {
                    BonusInstance data = entry.getValue().instances.get(player);
                    if (data != null && data.discovered) writer.write(entry.getKey());
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
                    ServerBonus bonus = ServerData.bonuses.get(string);
                    if (bonus != null)
                    {
                        bonus.getBonusInstance(player).discovered = true;
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

    public static void updateBonuses(EntityPlayer player)
    {
        //Happens once per second on player tick event
        for (ServerBonus bonus : ServerData.bonuses.values()) bonus.update(player);
    }


    @Nonnull
    public BonusInstance getBonusInstance(EntityPlayer player)
    {
        return instances.computeIfAbsent(player, k -> new BonusInstance(player, this));
    }

    public void update(EntityPlayer player)
    {
        instances.computeIfAbsent(player, k -> new BonusInstance(player, this)).update();
    }


    public class BonusInstance
    {
        public boolean active, discovered;
        private EntityPlayer player;
        private ServerBonus bonus;

        private BonusInstance(EntityPlayer player, ServerBonus bonus)
        {
            this.player = player;
            this.bonus = bonus;
            update();
        }

        public void update()
        {
            for (ABonusRequirement requirement : bonusRequirements)
            {
                if (requirement.active(player) < requirement.required())
                {
                    update(false);
                    return;
                }
            }

            update(true);
        }

        private void update(boolean activate)
        {
            if (activate)
            {
                if (!active)
                {
                    //Activating
                    active = true;
                    System.out.println("activate");

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
                    active = false;

                    for (ABonusElement element : bonusElements) element.deactivate(player);
                }
            }
        }
    }
}
