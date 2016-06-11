package org.undergroundbunker.harshworld.library.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.undergroundbunker.harshworld.library.utils.RecipeMatch;

public interface IModifier extends IToolMod {

    RecipeMatch.Match matches(ItemStack[] stacks);

    boolean canApply(ItemStack stack, ItemStack original);

    void apply(ItemStack stack);

    void apply(NBTTagCompound root);

    void updateNBT(NBTTagCompound modifierTag);

    void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag);

    String getTooltip(NBTTagCompound modifierTag, boolean detailed);

    @SideOnly(Side.CLIENT)
    boolean hasTexturePerMaterial();

    boolean equalModifier(NBTTagCompound modifierTag1, NBTTagCompound modifierTag2);
}
