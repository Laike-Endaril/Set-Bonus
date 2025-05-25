package com.fantasticsource.setbonus.common;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.screen.MessageGUI;
import com.fantasticsource.mctools.potions.FantasticPotionEffect;
import com.fantasticsource.setbonus.Compat;
import com.fantasticsource.setbonus.SetBonusData;
import com.fantasticsource.setbonus.client.gui.ServerConfigGUI;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementAttributeModifier;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementEnchantment;
import com.fantasticsource.setbonus.common.bonuselements.BonusElementPotionEffect;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SetRequirement;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.SlotData;
import com.fantasticsource.setbonus.config.SetBonusConfig;
import com.fantasticsource.setbonus.server.ServerBonus;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashSet;

import static com.fantasticsource.setbonus.SetBonus.MODID;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(AllDiscoveredBonusesPacketHandler.class, AllDiscoveredBonusesPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(DiscoverBonusPacketHandler.class, DiscoverBonusPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(HPFixPacketHandler.class, HPFixPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(PotionFixPacketHandler.class, PotionFixPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(RequestServerDataPacketHandler.class, RequestServerDataPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(ServerDataPacketHandler.class, ServerDataPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(ServerDataRequestDeniedPacketHandler.class, ServerDataRequestDeniedPacket.class, discriminator++, Side.CLIENT);
    }

    public static void updateConfig(EntityPlayerMP player)
    {
        ServerBonus.updateBonuses(player, true);
        ServerBonus.loadDiscoveries(player);
        Network.WRAPPER.sendTo(new AllDiscoveredBonusesPacket(player), player);
    }


    public static class DiscoverBonusPacket implements IMessage
    {
        public HashSet<String> equipment = new HashSet<>();
        public HashSet<String> sets = new HashSet<>();

        public String bonusString;

        public HashSet<String> attributeMods = new HashSet<>();
        public HashSet<String> potions = new HashSet<>();
        public HashSet<String> enchants = new HashSet<>();


        private ServerBonus bonus;


        public DiscoverBonusPacket() //Required; probably for when the packet is received
        {
        }

        public DiscoverBonusPacket(ServerBonus bonus)
        {
            this.bonus = bonus;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, bonus.toString());


            for (ABonusRequirement bonusRequirement : bonus.requirements)
            {
                if (bonusRequirement instanceof SetRequirement)
                {
                    Set set = ((SetRequirement) bonusRequirement).set;
                    sets.add(set.toString());

                    for (SlotData slotData : set.slotData)
                    {
                        for (Equip equip : slotData.involvedEquips)
                        {
                            equipment.add(equip.toString());
                        }
                    }
                }
            }

            for (ABonusElement element : bonus.bonusElements)
            {
                if (element instanceof BonusElementAttributeModifier)
                {
                    attributeMods.add(element.toString());
                }
                else if (element instanceof BonusElementPotionEffect)
                {
                    potions.add(element.toString());
                }
                else if (element instanceof BonusElementEnchantment)
                {
                    enchants.add(element.toString());
                    for (Equip equip : ((BonusElementEnchantment) element).slotDataToEnchant.involvedEquips)
                    {
                        equipment.add(equip.toString());
                    }
                }
            }


            buf.writeInt(equipment.size());
            for (String string : equipment) ByteBufUtils.writeUTF8String(buf, string);

            buf.writeInt(sets.size());
            for (String string : sets) ByteBufUtils.writeUTF8String(buf, string);

            buf.writeInt(attributeMods.size());
            for (String string : attributeMods) ByteBufUtils.writeUTF8String(buf, string);

            buf.writeInt(potions.size());
            for (String string : potions) ByteBufUtils.writeUTF8String(buf, string);

            buf.writeInt(enchants.size());
            for (String string : enchants) ByteBufUtils.writeUTF8String(buf, string);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            bonusString = ByteBufUtils.readUTF8String(buf);


            for (int i = buf.readInt(); i > 0; i--)
            {
                equipment.add(ByteBufUtils.readUTF8String(buf));
            }

            for (int i = buf.readInt(); i > 0; i--)
            {
                sets.add(ByteBufUtils.readUTF8String(buf));
            }


            for (int i = buf.readInt(); i > 0; i--)
            {
                attributeMods.add(ByteBufUtils.readUTF8String(buf));
            }

            for (int i = buf.readInt(); i > 0; i--)
            {
                potions.add(ByteBufUtils.readUTF8String(buf));
            }

            for (int i = buf.readInt(); i > 0; i--)
            {
                enchants.add(ByteBufUtils.readUTF8String(buf));
            }
        }
    }

    public static class DiscoverBonusPacketHandler implements IMessageHandler<DiscoverBonusPacket, IMessage>
    {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(DiscoverBonusPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() ->
            {
                SetBonusData.CLIENT_DATA.addFromPacket(packet);
                if (SetBonusConfig.clientSettings.dynamicTooltipSearch > 0) Compat.refreshTooltips();
            });
            return null;
        }
    }


    public static class AllDiscoveredBonusesPacket implements IMessage
    {
        public HashSet<String> equipment = new HashSet<>();
        public HashSet<String> sets = new HashSet<>();

        public HashSet<String> bonuses = new HashSet<>();

        public HashSet<String> attributeMods = new HashSet<>();
        public HashSet<String> potions = new HashSet<>();
        public HashSet<String> enchantments = new HashSet<>();


        private EntityPlayerMP player;


        public AllDiscoveredBonusesPacket() //Required; probably for when the packet is received
        {
        }

        private AllDiscoveredBonusesPacket(EntityPlayerMP player)
        {
            this.player = player;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            for (Bonus bonus : SetBonusData.SERVER_DATA.bonuses)
            {
                if (bonus.discoveryMode == Bonus.MODE_GLOBALLY_KNOWN || (bonus.discoveryMode == Bonus.MODE_DISCOVERABLE && ((ServerBonus) bonus).getBonusInstance(player).discovered))
                {
                    bonuses.add(bonus.toString());

                    for (ABonusRequirement bonusRequirement : bonus.requirements)
                    {
                        if (bonusRequirement instanceof SetRequirement)
                        {
                            Set set = ((SetRequirement) bonusRequirement).set;
                            sets.add(set.toString());

                            for (SlotData slotData : set.slotData)
                            {
                                for (Equip equip : slotData.involvedEquips)
                                {
                                    equipment.add(equip.toString());
                                }
                            }
                        }
                    }

                    for (ABonusElement element : bonus.bonusElements)
                    {
                        if (element instanceof BonusElementAttributeModifier)
                        {
                            attributeMods.add(element.toString());
                        }
                        else if (element instanceof BonusElementPotionEffect)
                        {
                            potions.add(element.toString());
                        }
                        else if (element instanceof BonusElementEnchantment)
                        {
                            enchantments.add(element.toString());
                            for (Equip equip : ((BonusElementEnchantment) element).slotDataToEnchant.involvedEquips)
                            {
                                equipment.add(equip.toString());
                            }
                        }
                    }
                }
            }


            buf.writeInt(equipment.size());
            for (String string : equipment) ByteBufUtils.writeUTF8String(buf, string);

            buf.writeInt(sets.size());
            for (String string : sets) ByteBufUtils.writeUTF8String(buf, string);

            buf.writeInt(bonuses.size());
            for (String string : bonuses) ByteBufUtils.writeUTF8String(buf, string);

            buf.writeInt(attributeMods.size());
            for (String string : attributeMods) ByteBufUtils.writeUTF8String(buf, string);

            buf.writeInt(potions.size());
            for (String string : potions) ByteBufUtils.writeUTF8String(buf, string);

            buf.writeInt(enchantments.size());
            for (String string : enchantments) ByteBufUtils.writeUTF8String(buf, string);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            for (int i = buf.readInt(); i > 0; i--)
            {
                equipment.add(ByteBufUtils.readUTF8String(buf));
            }

            for (int i = buf.readInt(); i > 0; i--)
            {
                sets.add(ByteBufUtils.readUTF8String(buf));
            }


            for (int i = buf.readInt(); i > 0; i--)
            {
                bonuses.add(ByteBufUtils.readUTF8String(buf));
            }


            for (int i = buf.readInt(); i > 0; i--)
            {
                attributeMods.add(ByteBufUtils.readUTF8String(buf));
            }

            for (int i = buf.readInt(); i > 0; i--)
            {
                potions.add(ByteBufUtils.readUTF8String(buf));
            }

            for (int i = buf.readInt(); i > 0; i--)
            {
                enchantments.add(ByteBufUtils.readUTF8String(buf));
            }
        }
    }

    public static class AllDiscoveredBonusesPacketHandler implements IMessageHandler<AllDiscoveredBonusesPacket, IMessage>
    {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(AllDiscoveredBonusesPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> SetBonusData.setClientFromPacket(packet));
            return null;
        }
    }


    public static class HPFixPacket implements IMessage
    {
        public float hp;

        public HPFixPacket() //Required; probably for when the packet is received
        {
        }

        public HPFixPacket(EntityPlayerMP player)
        {
            hp = player.getHealth();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeFloat(hp);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            hp = buf.readFloat();
        }
    }

    public static class HPFixPacketHandler implements IMessageHandler<HPFixPacket, IMessage>
    {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(HPFixPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().player.setHealth(packet.hp));
            return null;
        }
    }


    public static class PotionFixPacket implements IMessage
    {
        public Potion potion;

        public PotionFixPacket() //Required; probably for when the packet is received
        {
        }

        public PotionFixPacket(Potion potion)
        {
            this.potion = potion;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, potion.getRegistryName().toString());
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(ByteBufUtils.readUTF8String(buf)));
        }
    }

    public static class PotionFixPacketHandler implements IMessageHandler<PotionFixPacket, IMessage>
    {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(PotionFixPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() ->
            {
                EntityPlayer player = Minecraft.getMinecraft().player;
                PotionEffect potionEffect = player.getActivePotionEffect(packet.potion);
                if (potionEffect != null)
                {
                    player.removePotionEffect(potionEffect.getPotion());
                    potionEffect = new FantasticPotionEffect(potionEffect.getPotion(), potionEffect.getDuration(), potionEffect.getAmplifier(), potionEffect.getIsAmbient(), potionEffect.doesShowParticles());
                    potionEffect.setPotionDurationMax(potionEffect.getDuration() >= FantasticPotionEffect.MAX_DURATION_THRESHOLD);
                    player.addPotionEffect(potionEffect);
                }
            });
            return null;
        }
    }


    public static class RequestServerDataPacket implements IMessage
    {
        public RequestServerDataPacket()
        {
            //Required
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
        }
    }

    public static class RequestServerDataPacketHandler implements IMessageHandler<RequestServerDataPacket, IMessage>
    {
        @Override
        public IMessage onMessage(RequestServerDataPacket packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
            {
                EntityPlayerMP player = ctx.getServerHandler().player;
                if (MCTools.isOP(player)) WRAPPER.sendTo(new ServerDataPacket(SetBonusData.SERVER_DATA), player);
                else WRAPPER.sendTo(new ServerDataRequestDeniedPacket(), player);
            });
            return null;
        }
    }


    public static class ServerDataPacket implements IMessage
    {
        public SetBonusData data;

        public ServerDataPacket() //Required; probably for when the packet is received
        {
        }

        public ServerDataPacket(SetBonusData data)
        {
            this.data = data.clone();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(data.equipment.size());
            for (Equip equip : data.equipment) ByteBufUtils.writeUTF8String(buf, equip.toString());

            buf.writeInt(data.sets.size());
            for (Set set : data.sets) ByteBufUtils.writeUTF8String(buf, set.toString());


            ArrayList<BonusElementAttributeModifier> attributeMods = new ArrayList<>();
            ArrayList<BonusElementPotionEffect> potions = new ArrayList<>();
            ArrayList<BonusElementEnchantment> enchantments = new ArrayList<>();

            buf.writeInt(data.bonuses.size());
            for (Bonus bonus : data.bonuses)
            {
                ByteBufUtils.writeUTF8String(buf, bonus.toString());
                for (ABonusElement element : bonus.bonusElements)
                {
                    if (element instanceof BonusElementAttributeModifier) attributeMods.add((BonusElementAttributeModifier) element);
                    if (element instanceof BonusElementPotionEffect) potions.add((BonusElementPotionEffect) element);
                    if (element instanceof BonusElementEnchantment) enchantments.add((BonusElementEnchantment) element);
                }
            }

            buf.writeInt(attributeMods.size());
            for (BonusElementAttributeModifier element : attributeMods) ByteBufUtils.writeUTF8String(buf, element.toString());

            buf.writeInt(potions.size());
            for (BonusElementPotionEffect element : potions) ByteBufUtils.writeUTF8String(buf, element.toString());

            buf.writeInt(enchantments.size());
            for (BonusElementEnchantment element : enchantments) ByteBufUtils.writeUTF8String(buf, element.toString());
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            data = new SetBonusData();

            for (int i = buf.readInt(); i > 0; i--)
            {
                data.equipment.add(Equip.getInstance(ByteBufUtils.readUTF8String(buf)));
            }

            for (int i = buf.readInt(); i > 0; i--)
            {
                data.sets.add(Set.getInstance(ByteBufUtils.readUTF8String(buf), data));
            }


            for (int i = buf.readInt(); i > 0; i--)
            {
                data.bonuses.add(Bonus.getInstance(ByteBufUtils.readUTF8String(buf), data));
            }


            for (int i = buf.readInt(); i > 0; i--)
            {
                BonusElementAttributeModifier.getInstance(ByteBufUtils.readUTF8String(buf), data);
            }

            for (int i = buf.readInt(); i > 0; i--)
            {
                BonusElementPotionEffect.getInstance(ByteBufUtils.readUTF8String(buf), data);
            }

            for (int i = buf.readInt(); i > 0; i--)
            {
                BonusElementEnchantment.getInstance(ByteBufUtils.readUTF8String(buf), data);
            }
        }
    }

    public static class ServerDataPacketHandler implements IMessageHandler<ServerDataPacket, IMessage>
    {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(ServerDataPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() ->
            {
                GuiScreen screen = Minecraft.getMinecraft().currentScreen;
                if (screen instanceof ServerConfigGUI)
                {
                    ServerConfigGUI gui = (ServerConfigGUI) screen;
                    gui.addPostClosedActions(() -> new ServerConfigGUI(packet.data));
                    gui.close();
                }
            });
            return null;
        }
    }


    public static class ServerDataRequestDeniedPacket implements IMessage
    {
        public ServerDataRequestDeniedPacket() //Required; probably for when the packet is received
        {
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
        }
    }

    public static class ServerDataRequestDeniedPacketHandler implements IMessageHandler<ServerDataRequestDeniedPacket, IMessage>
    {
        @SideOnly(Side.CLIENT)
        @Override
        public IMessage onMessage(ServerDataRequestDeniedPacket packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() ->
            {
                GuiScreen screen = Minecraft.getMinecraft().currentScreen;
                if (screen instanceof ServerConfigGUI)
                {
                    new MessageGUI(GUIScreen.reformat(MODID + ".requestDenied"), GUIScreen.reformat(MODID + ".requestDenied.needOP"));
                }
            });
            return null;
        }
    }
}
