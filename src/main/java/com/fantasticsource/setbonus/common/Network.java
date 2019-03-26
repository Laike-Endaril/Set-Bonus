package com.fantasticsource.setbonus.common;

import com.fantasticsource.setbonus.SetBonus;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.setbonus.common.bonuselements.ModifierBonus;
import com.fantasticsource.setbonus.common.bonuselements.PotionBonus;
import com.fantasticsource.setbonus.config.SyncedConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


    public static class DiscoverBonusPacket implements IMessage
    {
        private Bonus bonus;

        private String bonusString;

        private ArrayList<String> attributeMods = new ArrayList<>();
        private ArrayList<String> potions = new ArrayList<>();


        public DiscoverBonusPacket() //Required; probably for when the packet is received
        {
        }

        public DiscoverBonusPacket(Bonus bonus)
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
                    List<String> list = Arrays.asList(SyncedConfig.bonuses);
                    list.add(packet.bonusString);
                    SyncedConfig.bonuses = list.toArray(new String[list.size()]);


                    list = Arrays.asList(SyncedConfig.attributeMods);
                    list.addAll(packet.attributeMods);
                    SyncedConfig.attributeMods = list.toArray(new String[list.size()]);

                    list = Arrays.asList(SyncedConfig.potions);
                    list.addAll(packet.potions);
                    SyncedConfig.potions = list.toArray(new String[list.size()]);


                    Data.update();
                });
            }

            return null;
        }
    }


    public static class ConfigPacket implements IMessage
    {
        private EntityPlayer player;

        private ArrayList<String> equipment = new ArrayList<>();
        private ArrayList<String> sets = new ArrayList<>();

        private ArrayList<String> bonuses = new ArrayList<>();

        private ArrayList<String> attributeMods = new ArrayList<>();
        private ArrayList<String> potions = new ArrayList<>();


        public ConfigPacket() //Required; probably for when the packet is received
        {
        }

        public ConfigPacket(EntityPlayer player)
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


            for (Bonus bonus : Data.bonuses.values())
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
                Minecraft.getMinecraft().addScheduledTask(() ->
                {
                    SyncedConfig.equipment = packet.equipment.toArray(new String[packet.equipment.size()]);
                    SyncedConfig.sets = packet.sets.toArray(new String[packet.sets.size()]);

                    SyncedConfig.bonuses = packet.bonuses.toArray(new String[packet.bonuses.size()]);

                    SyncedConfig.attributeMods = packet.attributeMods.toArray(new String[packet.attributeMods.size()]);
                    SyncedConfig.potions = packet.potions.toArray(new String[packet.potions.size()]);

                    Data.update();
                });
            }

            return null;
        }
    }
}
