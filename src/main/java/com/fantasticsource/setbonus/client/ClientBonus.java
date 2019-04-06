package com.fantasticsource.setbonus.client;

import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class ClientBonus
{
    public static int
            MODE_DISCOVERABLE = 0,
            MODE_GLOBALLY_KNOWN = 1;


    public String parsedString, id, name;
    public int discoveryMode;


    public ArrayList<ABonusRequirement> bonusRequirements = new ArrayList<>();
    public ArrayList<ABonusElement> bonusElements = new ArrayList<>();

    private LinkedHashMap<EntityPlayer, BonusInstance> instances = new LinkedHashMap<>();


    private ClientBonus()
    {
    }

    public static ClientBonus getInstance(String parsableBonus)
    {
        ClientBonus result = new ClientBonus();

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

    public static void dropAll()
    {
        //Needs to be done right before new configs are applied, to remove any eg. potion effects (because they might not be part of the bonus anymore)
        //Also called when a server is stopping, to remove any bonuses on players before they get unloaded, in case said bonuses don't exist next time the server starts due to config changes
        for (ClientBonus bonus : ClientData.bonuses.values())
        {
            for (BonusInstance data : bonus.instances.values()) data.update(false);
        }
        ClientData.bonuses.clear();
    }

    public static void updateBonuses(EntityPlayer player)
    {
        //Happens once per second on player tick event
        for (ClientBonus bonus : ClientData.bonuses.values()) bonus.update(player);
    }

    @Nonnull
    public BonusInstance getBonusInstance(EntityPlayer player)
    {
        return instances.computeIfAbsent(player, k -> new BonusInstance());
    }

    public void update(EntityPlayer player)
    {
        instances.computeIfAbsent(player, k -> new BonusInstance()).update();
    }


    public class BonusInstance
    {
        public boolean active;

        private BonusInstance()
        {
            update();
        }

        public void update()
        {
            for (ABonusRequirement requirement : bonusRequirements)
            {
                if (requirement.active(Minecraft.getMinecraft().player) < requirement.required())
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
                }
            }
            else
            {
                if (active)
                {
                    //Deactivating
                    active = false;
                }
            }
        }
    }
}
