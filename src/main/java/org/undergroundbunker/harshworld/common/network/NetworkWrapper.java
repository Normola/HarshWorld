package org.undergroundbunker.harshworld.common.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkWrapper {

    public final SimpleNetworkWrapper network;
    protected final AbstractPacketHandler handler;
    private int id = 0;

    public NetworkWrapper(String channelName) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
        handler = new AbstractPacketHandler();
    }

    public void registerPacket(Class<? extends AbstractPacket> aPacketClass) {
        registerPacketClient(aPacketClass);
        registerPacketServer(aPacketClass);
    }

    public void registerPacketClient(Class<? extends AbstractPacket> aPacketClass) {
        registerPacketImpl(aPacketClass, Side.CLIENT);
    }

    public void registerPacketServer(Class<? extends AbstractPacket> aPacketClass) {
        registerPacketImpl(aPacketClass, Side.SERVER);
    }

    private void registerPacketImpl(Class<? extends AbstractPacket> aPacketClass, Side side) {
        network.registerMessage(handler, aPacketClass, id++, side);
    }

    public static class AbstractPacketHandler implements IMessageHandler<AbstractPacket, IMessage> {

        @Override
        public IMessage onMessage(AbstractPacket packet, MessageContext context) {
            if (context.side == Side.SERVER) {
                return packet.handleServer(context.getServerHandler());
            }
            else {
                return packet.handleClient(context.getClientHandler());
            }
        }
    }
}
