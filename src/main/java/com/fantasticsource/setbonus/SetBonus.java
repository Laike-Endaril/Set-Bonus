package com.fantasticsource.setbonus;

import com.fantasticsource.mctools.ClientTickTimer;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.setbonus.client.ClientBonus;
import com.fantasticsource.setbonus.client.SetBonusGUI;
import com.fantasticsource.setbonus.client.TooltipRenderer;
import com.fantasticsource.setbonus.client.gui.SetBonusConfigGUI;
import com.fantasticsource.setbonus.common.Commands;
import com.fantasticsource.setbonus.common.Network;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementPotionEffect;
import com.fantasticsource.setbonus.config.ConfigHandler;
import com.fantasticsource.setbonus.config.SetBonusConfig;
import com.fantasticsource.setbonus.server.ServerBonus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.config.GuiConfig;
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

@Mod(modid = SetBonus.MODID, name = SetBonus.NAME, version = SetBonus.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.059,)")
public class SetBonus
{
    public static final String MODID = "setbonus";
    public static final String NAME = "Set Bonus";
    public static final String VERSION = "1.12.2.035";
    public static final String CONFIG_VERSION = "1.12.2.009";


    static
    {
        ConfigHandler.init();
    }


    @EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        Compat.init();

        Network.init();

        MinecraftForge.EVENT_BUS.register(SetBonus.class);
        MinecraftForge.EVENT_BUS.register(ServerTickTimer.class);
        MinecraftForge.EVENT_BUS.register(BonusElementPotionEffect.class);

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            //Physical client
//            Keys.init(event);

            MinecraftForge.EVENT_BUS.register(ClientTickTimer.class);
            MinecraftForge.EVENT_BUS.register(TooltipRenderer.class);
            MinecraftForge.EVENT_BUS.register(SetBonusGUI.class);
        }
    }

    @EventHandler
    public static void serverStarting(FMLServerStartingEvent event)
    {
        //This event is very reliable
        //It happens very early when a logical server is starting and does not happen when the client connects to a remote server
        //It works for both dedicated and integrated as well

        event.registerServerCommand(new Commands());
        SetBonusData.setServerFromConfig();
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
                SetBonusData.setServerFromConfig();

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
            SetBonusData.setServerFromConfig();
        }
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            if (event.side == Side.SERVER)
            {
                //Server side
                ServerBonus.updateBonuses((EntityPlayerMP) event.player, false);
            }
            else
            {
                //Client side
                EntityPlayer player = event.player;
                if (Minecraft.getMinecraft().player == player) ClientBonus.updateBonuses(player);
            }
        }
    }

    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        Network.WRAPPER.sendTo(new Network.HPFixPacket(player), player);
        ServerTickTimer.schedule(20, () -> Network.WRAPPER.sendTo(new Network.HPFixPacket(player), player));
    }

    @SubscribeEvent
    public static void playerJoinWorld(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            ServerBonus.loadDiscoveries(player);
            Network.updateConfig(player);

            Network.WRAPPER.sendTo(new Network.HPFixPacket(player), player);
            ServerTickTimer.schedule(20, () -> Network.WRAPPER.sendTo(new Network.HPFixPacket(player), player));
        }
    }

    @SubscribeEvent
    public static void playerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        ServerBonus.clearMem(event.player);
    }

    @SubscribeEvent
    public static void disconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        Minecraft.getMinecraft().addScheduledTask(() ->
        {
            ClientBonus.dropAll();
            SetBonusData.CLIENT_DATA.clear();
            if (SetBonusConfig.clientSettings.dynamicTooltipSearch > 0) Compat.refreshTooltips();
        });
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void gui(GuiOpenEvent event)
    {
        GuiScreen gui = event.getGui();
        if (gui instanceof GuiConfig && MODID.equals(((GuiConfig) gui).modID))
        {
            event.setCanceled(true);
            new SetBonusConfigGUI();
        }
    }
}
