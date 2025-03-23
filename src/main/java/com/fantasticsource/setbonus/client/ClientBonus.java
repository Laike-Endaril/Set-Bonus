package com.fantasticsource.setbonus.client;

import com.fantasticsource.setbonus.Compat;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;

public class ClientBonus extends Bonus
{
    public static boolean refreshJEI = false;

    private LinkedHashMap<EntityPlayer, BonusInstance> instances = new LinkedHashMap<>();


    public static ClientBonus getInstance(String parsableBonus)
    {
        return (ClientBonus) Bonus.getInstance(parsableBonus, Side.CLIENT);
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
        Compat.refreshJEITooltips();
    }

    public static void updateBonuses(EntityPlayer player)
    {
        //Happens once per second on player tick event
        for (ClientBonus bonus : ClientData.bonuses.values()) bonus.update(player);
        if (refreshJEI)
        {
            Compat.refreshJEITooltips();
            refreshJEI = false;
        }
    }

    @Nonnull
    public BonusInstance getBonusInstance(EntityPlayer player)
    {
        BonusInstance result = instances.get(player);
        if (result == null)
        {
            result = new BonusInstance();
            result.update();
            instances.put(player, result);
        }
        return result;
    }

    public void update(EntityPlayer player)
    {
        instances.computeIfAbsent(player, k -> new BonusInstance()).update();
    }


    public class BonusInstance
    {
        public int[] lastReqStatus = new int[0]; //Right now this is strictly for JEI compat; for more detailed requirement count stuff, see TooltipRenderer
        public boolean active;

        private BonusInstance()
        {
        }

        public void update()
        {
            boolean activate = true;
            int[] reqStatus = new int[bonusRequirements.size() << 1];
            int i = 0;
            for (ABonusRequirement requirement : bonusRequirements)
            {
                reqStatus[i++] = requirement.active(Minecraft.getMinecraft().player);
                reqStatus[i++] = requirement.required();
                if (reqStatus[i - 2] < reqStatus[i - 1]) activate = false;
            }

            update(activate);

            if (reqStatus.length != lastReqStatus.length) refreshJEI = true;
            else
            {
                for (i = 0; i < reqStatus.length; i++)
                {
                    if (reqStatus[i] != lastReqStatus[i])
                    {
                        refreshJEI = true;
                        break;
                    }
                }
            }

            lastReqStatus = reqStatus;
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
