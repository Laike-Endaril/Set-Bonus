package com.fantasticsource.setbonus.common;

import com.fantasticsource.setbonus.config.SyncedConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
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
    }


    public static class ConfigPacket implements IMessage
    {
        private String[] equipment, sets, bonuses, attributeMods, potions;

        public ConfigPacket() //Required; probably for when the packet is received
        {
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(serverSettings.equipment.length);
            for (String string : serverSettings.equipment) ByteBufUtils.writeUTF8String(buf, string);
            buf.writeInt(serverSettings.sets.length);
            for (String string : serverSettings.sets) ByteBufUtils.writeUTF8String(buf, string);

            buf.writeInt(serverSettings.bonuses.length);
            for (String string : serverSettings.bonuses) ByteBufUtils.writeUTF8String(buf, string);

            buf.writeInt(serverSettings.attributeMods.length);
            for (String string : serverSettings.attributeMods) ByteBufUtils.writeUTF8String(buf, string);
            buf.writeInt(serverSettings.potions.length);
            for (String string : serverSettings.potions) ByteBufUtils.writeUTF8String(buf, string);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            ArrayList<String> list = new ArrayList<>();
            for (int i = buf.readInt(); i > 0; i--)
            {
                list.add(ByteBufUtils.readUTF8String(buf));
            }
            equipment = list.toArray(new String[list.size()]);

            list = new ArrayList<>();
            for (int i = buf.readInt(); i > 0; i--)
            {
                list.add(ByteBufUtils.readUTF8String(buf));
            }
            sets = list.toArray(new String[list.size()]);


            list = new ArrayList<>();
            for (int i = buf.readInt(); i > 0; i--)
            {
                list.add(ByteBufUtils.readUTF8String(buf));
            }
            bonuses = list.toArray(new String[list.size()]);


            list = new ArrayList<>();
            for (int i = buf.readInt(); i > 0; i--)
            {
                list.add(ByteBufUtils.readUTF8String(buf));
            }
            attributeMods = list.toArray(new String[list.size()]);

            list = new ArrayList<>();
            for (int i = buf.readInt(); i > 0; i--)
            {
                list.add(ByteBufUtils.readUTF8String(buf));
            }
            potions = list.toArray(new String[list.size()]);
        }
    }

    public static class ConfigPacketHandler implements IMessageHandler<ConfigPacket, IMessage>
    {
        @Override
        public IMessage onMessage(ConfigPacket packet, MessageContext ctx)
        {
            if (ctx.side == Side.CLIENT)
            {
                Minecraft.getMinecraft().addScheduledTask(() ->
                {
                    SyncedConfig.equipment = packet.equipment;
                    SyncedConfig.sets = packet.sets;

                    SyncedConfig.bonuses = packet.bonuses;

                    SyncedConfig.attributeMods = packet.attributeMods;
                    SyncedConfig.potions = packet.potions;

                    Data.update();
                });
            }

            return null;
        }
    }
}
