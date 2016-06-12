package org.undergroundbunker.harshworld.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.undergroundbunker.harshworld.library.Util;

public class HWNetwork extends NetworkWrapper {

    public static HWNetwork instance = new HWNetwork();

    public HWNetwork() {
        super(Util.ModID);
    }

    public void Setup() {

        //TODO: This will need looking at at some point
    }


    public static void sendTo (AbstractPacket packet, EntityPlayerMP player) {
        instance.network.sendTo(packet, player);
    }

    public static void sentToAll(AbstractPacket packet) {
        instance.network.sendToAll(packet);
    }

    public static void sentToAllAround(AbstractPacket packet, NetworkRegistry.TargetPoint point) {
        instance.network.sendToAllAround(packet, point);
    }

    public static void sentToDimension(AbstractPacket packet, int dimensionId) {
        instance.network.sendToDimension(packet, dimensionId);
    }

    public static void sendToServer(AbstractPacket packet) {
        instance.network.sendToServer(packet);
    }

    public static void sendToClients(WorldServer world, BlockPos pos, AbstractPacket packet) {

        Chunk chunk = world.getChunkFromBlockCoords(pos);

        for (EntityPlayer player : world.playerEntities) {
            if (!(player instanceof EntityPlayerMP)) {
                continue;
            }

            EntityPlayerMP playerMP = (EntityPlayerMP)player;

            if (world.getPlayerChunkMap().isPlayerWatchingChunk(playerMP, chunk.xPosition, chunk.zPosition)) {
                HWNetwork.sendTo(packet, playerMP);
            }

        }
    }
}
