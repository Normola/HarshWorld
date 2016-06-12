package org.undergroundbunker.harshworld.client;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class CreativeTab extends CreativeTabs{

    private ItemStack icon;

    public CreativeTab(String label, ItemStack backupIcon) {
        super(label);

        this.icon = backupIcon;
    }

    public void getDisplayIcon(ItemStack displayIcon) {
        if (displayIcon != null) {
            this.icon = displayIcon;
        }
    }

    @Nonnull
    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack getIconItemStack() {
        return icon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getIconItemDamage() {
        return icon.getItemDamage();
    }

    @Nonnull
    @SideOnly(Side.CLIENT)
    @Override
    public Item getTabIconItem() {
        return icon.getItem();
    }
}
