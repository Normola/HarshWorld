package org.undergroundbunker.harshworld.tools.item;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.undergroundbunker.harshworld.library.Category;
import org.undergroundbunker.harshworld.library.utils.ToolHelper;

import javax.annotation.Nonnull;

public class HWWrench extends ToolCore {

    public static final ImmutableSet<Material> effectiveMaterials =
            ImmutableSet.of();

    public HWWrench() {
        super();

        addCategory(Category.TOOL);
        setHarvestLevel("tool", 0);
    }

    @Override
    public boolean isEffective(IBlockState state) {
        return false;
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (ToolHelper.isBroken(stack)) {
            return EnumActionResult.FAIL;
        }

        return EnumActionResult.FAIL;
    }

    @Override
    public float attackSpeed() {
        return 1f;
    }

    @Override
    public float damagePotential() {
        return 0.9f;
    }

}
