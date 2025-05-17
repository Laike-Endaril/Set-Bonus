package com.fantasticsource.setbonus.client;

import com.fantasticsource.setbonus.Compat;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import com.fantasticsource.setbonus.config.SetBonusConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;

public class ClientBonus extends Bonus
{
    public static boolean refreshJEI = false;

    private LinkedHashMap<EntityPlayer, BonusInstance> instances = new LinkedHashMap<>();


    public static void dropAll()
    {
        //Needs to be done right before new configs are applied, to remove any eg. potion effects (because they might not be part of the bonus anymore)
        //Also called when a server is stopping, to remove any bonuses on players before they get unloaded, in case said bonuses don't exist next time the server starts due to config changes
        for (Bonus bonus : SetBonusData.CLIENT_DATA.bonuses.values())
        {
            for (BonusInstance data : ((ClientBonus) bonus).instances.values()) data.update(false);
        }
        SetBonusData.CLIENT_DATA.bonuses.clear();
        if (SetBonusConfig.clientSettings.dynamicTooltipSearch > 0) Compat.refreshTooltips();
    }

    public static void updateBonuses(EntityPlayer player)
    {
        //Happens once per second on player tick event
        for (Bonus bonus : SetBonusData.CLIENT_DATA.bonuses.values()) ((ClientBonus) bonus).update(player);
        if (refreshJEI)
        {
            if (SetBonusConfig.clientSettings.dynamicTooltipSearch > 1)
            {
                Compat.refreshTooltips();
                refreshJEI = false;
            }
        }
    }

    public void update(EntityPlayer player)
    {
        instances.computeIfAbsent(player, k -> new BonusInstance()).update();
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
