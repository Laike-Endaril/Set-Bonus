package com.fantasticsource.setbonus.client;

import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;

public class ClientBonus extends Bonus
{
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
