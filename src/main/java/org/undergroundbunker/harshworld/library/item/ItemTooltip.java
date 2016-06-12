package org.undergroundbunker.harshworld.library.item;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.undergroundbunker.harshworld.library.utils.LocaleUtils;

import java.util.List;

public class ItemTooltip extends Item{

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        addOptionalToolTip(stack, tooltip);
        super.addInformation(stack, player, tooltip, advanced);
    }

    @SuppressWarnings("deprecation")
    public static void addOptionalToolTip(ItemStack stack, List<String> tooltip) {
        if (net.minecraft.util.text.translation.I18n.canTranslate(stack.getUnlocalizedName() + ".tooltip")) {
            String grey = TextFormatting.GRAY.toString();
            String unlocalisedName = stack.getUnlocalizedName() + ".tooltip";
            tooltip.addAll(LocaleUtils.getTooltips(grey + LocaleUtils.translateRecursive(unlocalisedName)));
        }}
}
