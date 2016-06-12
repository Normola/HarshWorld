package org.undergroundbunker.harshworld.tools.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import org.undergroundbunker.harshworld.common.network.AbstractPacketThreadSafe;

public class ToolBreakAnimationPacket extends AbstractPacketThreadSafe {

    public ItemStack breakingTool;

    public ToolBreakAnimationPacket() {

    }

    public ToolBreakAnimationPacket(ItemStack breakingTool) {
        this.breakingTool = breakingTool;
    }

    @Override
    public void handleClientSafe(NetHandlerPlayClient netHandler) {
        Minecraft.getMinecraft().thePlayer.renderBrokenItemStack(breakingTool);
    }

    @Override
    public void handleServerSafe(NetHandlerPlayServer netHandler) {
        throw new UnsupportedOperationException("Clientside Only");
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        breakingTool = ByteBufUtils.readItemStack(buffer);
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        ByteBufUtils.writeItemStack(buffer, breakingTool);
    }
}
