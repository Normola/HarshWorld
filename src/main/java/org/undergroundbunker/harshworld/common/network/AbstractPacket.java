package org.undergroundbunker.harshworld.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class AbstractPacket implements IMessage {

    public abstract IMessage handleClient(NetHandlerPlayClient netHandler);
    public abstract IMessage handleServer(NetHandlerPlayServer netHandler);

    protected void writePos(BlockPos pos, ByteBuf buffer) {

        buffer.writeInt(pos.getX());
        buffer.writeInt(pos.getY());
        buffer.writeInt(pos.getZ());
    }

    protected BlockPos readPos(ByteBuf buffer) {
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();

        return new BlockPos(x, y, z);
    }
}
