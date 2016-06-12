package org.undergroundbunker.harshworld.library.tools;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IAoeTool {

    ImmutableList<BlockPos> getAoeBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos pos);
    boolean isAoeHarvestTool();
}
