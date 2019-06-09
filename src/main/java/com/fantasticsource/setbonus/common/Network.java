package com.fantasticsource.setbonus.common;

import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.client.ClientData;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.setbonus.common.bonuselements.ModifierBonus;
import com.fantasticsource.setbonus.common.bonuselements.PotionBonus;
import com.fantasticsource.setbonus.server.ServerBonus;
import com.fantasticsource.setbonus.server.ServerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;

import static com.fantasticsource.setbonus.config.SetBonusConfig.serverSettings;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(SetBonus.MODID);

    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(ConfigPacketHandler.class, ConfigPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(DiscoverBonusPacketHandler.class, DiscoverBonusPacket.class, discriminator++, Side.CLIENT);
    }

    public static void updateConfig(EntityPlayerMP player)
    {
        Network.WRAPPER.sendTo(new Network.ConfigPacket(player), player);
        ServerBonus.updateBonuses(player);
    }

    public static class DiscoverBonusPacket implements IMessage
    {
        public String bonusString;

        public ArrayList<String> attributeMods = new ArrayList<>();
        public ArrayList<String> potions = new ArrayList<>();


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
            ByteBufUtils.writeUTF8String(buf, bonus.parsedString);

            ArrayList<String> attributeMods = new ArrayList<>();
            ArrayList<String> potions = new ArrayList<>();
            for (ABonusElement element : bonus.bonusElements)
            {
                if (element instanceof ModifierBonus)
                {
                    attributeMods.add(element.parsedString);
                }
                else if (element instanceof PotionBonus)
                {
                    potions.add(element.parsedString);
                }
            }

            buf.writeInt(attributeMods.size());
            for (String string : attributeMods) ByteBufUtils.writeUTF8String(buf, string);
            buf.writeInt(potions.size());
            for (String string : potions) ByteBufUtils.writeUTF8String(buf, string);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            bonusString = ByteBufUtils.readUTF8String(buf);


            for (int i = buf.readInt(); i > 0; i--)
            {
                attributeMods.add(ByteBufUtils.readUTF8String(buf));
            }

            for (int i = buf.readInt(); i > 0; i--)
            {
                potions.add(ByteBufUtils.readUTF8String(buf));
            }
        }
    }

    public static class DiscoverBonusPacketHandler implements IMessageHandler<DiscoverBonusPacket, IMessage>
    {
        @Override
        public IMessage onMessage(DiscoverBonusPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft.getMinecraft().addScheduledTask(() ->
                {
                    ClientData.update(packet);
                });
            }

            return null;
        }
    }

    public static class ConfigPacket implements IMessage
    {
        public ArrayList<String> equipment = new ArrayList<>();
        public ArrayList<String> sets = new ArrayList<>();

        public ArrayList<String> bonuses = new ArrayList<>();

        public ArrayList<String> attributeMods = new ArrayList<>();
        public ArrayList<String> potions = new ArrayList<>();


        private EntityPlayer player;


        public ConfigPacket() //Required; probably for when the packet is received
        {
        }

        private ConfigPacket(EntityPlayer player)
        {
            this.player = player;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(serverSettings.equipment.length);
            for (String string : serverSettings.equipment) ByteBufUtils.writeUTF8String(buf, string);
            buf.writeInt(serverSettings.sets.length);
            for (String string : serverSettings.sets) ByteBufUtils.writeUTF8String(buf, string);


            for (ServerBonus bonus : ServerData.bonuses.values())
            {
                if (bonus.discoveryMode == Bonus.MODE_GLOBALLY_KNOWN || bonus.getBonusInstance(player).discovered)
                {
                    bonuses.add(bonus.parsedString);

                    for (ABonusElement element : bonus.bonusElements)
                    {
                        if (element instanceof ModifierBonus)
                        {
                            attributeMods.add(element.parsedString);
                        }
                        else if (element instanceof PotionBonus)
                        {
                            potions.add(element.parsedString);
                        }
                    }
                }
            }


            buf.writeInt(bonuses.size());
            for (String string : bonuses) ByteBufUtils.writeUTF8String(buf, string);

            buf.writeInt(attributeMods.size());
            for (String string : attributeMods) ByteBufUtils.writeUTF8String(buf, string);
            buf.writeInt(potions.size());
            for (String string : potions) ByteBufUtils.writeUTF8String(buf, string);
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
        }
    }

    public static class ConfigPacketHandler implements IMessageHandler<ConfigPacket, IMessage>
    {
        @Override
        public IMessage onMessage(ConfigPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft.getMinecraft().addScheduledTask(() -> ClientData.update(packet));
            }

            return null;
        }
    }
}
