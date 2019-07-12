package com.fantasticsource.setbonus;

import com.fantasticsource.mctools.ClientTickTimer;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.setbonus.client.ClientBonus;
import com.fantasticsource.setbonus.client.ClientData;
import com.fantasticsource.setbonus.client.TooltipRenderer;
import com.fantasticsource.setbonus.common.Commands;
import com.fantasticsource.setbonus.common.Network;
import com.fantasticsource.setbonus.config.ConfigHandler;
import com.fantasticsource.setbonus.server.ServerBonus;
import com.fantasticsource.setbonus.server.ServerData;
import com.fantasticsource.tools.Tools;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.minecraftforge.fml.common.Mod.EventHandler;

@Mod(modid = SetBonus.MODID, name = SetBonus.NAME, version = SetBonus.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.011,)")
public class SetBonus
{
    public static final String MODID = "setbonus";
    public static final String NAME = "Set Bonus";
    public static final String VERSION = "1.12.2.015";
    public static final String CONFIG_VERSION = "1.12.2.009";


    static
    {
        ConfigHandler.init();
    }


    @EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        Network.init();

        MinecraftForge.EVENT_BUS.register(SetBonus.class);
        MinecraftForge.EVENT_BUS.register(ServerTickTimer.class);

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            //Physical client
//            Keys.init(event);

            MinecraftForge.EVENT_BUS.register(ClientTickTimer.class);
            MinecraftForge.EVENT_BUS.register(TooltipRenderer.class);
//            MinecraftForge.EVENT_BUS.register(BonusScreen.class);
        }
    }

    @EventHandler
    public static void serverStarting(FMLServerStartingEvent event)
    {
        //This event is very reliable
        //It happens very early when a logical server is starting and does not happen when the client connects to a remote server
        //It works for both dedicated and integrated as well

        event.registerServerCommand(new Commands());
        ServerData.update();
    }

    @EventHandler
    public static void serverStop(FMLServerStoppingEvent event)
    {
        ServerBonus.dropAll();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void calcConfigs(ConfigChangedEvent.PostConfigChangedEvent event)
    {
        //Only auto-update data from configs if we are on the title screen OR if we are hosting the world
        if (event.isWorldRunning())
        {
            if (MCTools.hosting())
            {
                //Changed config while in-game (hosting)
                ServerData.update();

                EntityPlayer localPlayer = Minecraft.getMinecraft().player;
                World world = localPlayer.world;
                for (EntityPlayer player : world.playerEntities)
                {
                    if (player != localPlayer) Network.updateConfig((EntityPlayerMP) player);
                }
            }
            else
            {
                //Changed config while in-game (not hosting)
            }
        }
        else
        {
            //Changed config from title screen
            ServerData.update();
        }
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            if (event.side == Side.SERVER)
            {
                EntityPlayerMP player = (EntityPlayerMP) event.player;

                if (Tools.posMod(ServerTickTimer.currentTick(), 20) == Tools.posMod(player.getUniqueID().getLeastSignificantBits(), 20))
                {
                    ServerBonus.updateBonuses(player);
                }
            }
            else
            {
                //Client side
                EntityPlayer player = event.player;
                if (ClientTickTimer.currentTick() % 20 == 0 && Minecraft.getMinecraft().player == player) ClientBonus.updateBonuses(event.player);
            }
        }
    }

    @SubscribeEvent
    public static void playerConnect(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            ServerBonus.loadDiscoveries(player);
            Network.updateConfig(player);
        }
    }

    @SubscribeEvent
    public static void playerDisconnect(PlayerEvent.PlayerLoggedOutEvent event)
    {
        ServerBonus.clearMem(event.player);
    }

    @SubscribeEvent
    public static void disconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        ClientData.clear();
    }

//    @SubscribeEvent
//    public static void test(PlayerInteractEvent.RightClickEmpty event)
//    {
//        ItemStack itemStack = event.getEntityPlayer().inventory.getStackInSlot(0);
//        if (itemStack == ItemStack.EMPTY) System.out.println("empty");
//        else
//        {
//            System.out.println(itemStack.getDisplayName());
//            NBTTagCompound nbt = itemStack.getTagCompound();
//            if (nbt != null)
//            {
//                for (String string : nbt.getKeySet())
//                {
//                    System.out.println(string + ", " + nbt.getTag(string));
//                }
//            }
//        }
//    }
}
