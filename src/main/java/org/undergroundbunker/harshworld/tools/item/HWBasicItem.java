package org.undergroundbunker.harshworld.tools.item;

import net.minecraft.item.Item;
import org.undergroundbunker.harshworld.HarshWorld;
import org.undergroundbunker.harshworld.library.HWRegistry;
import org.undergroundbunker.harshworld.library.Util;

public class HWBasicItem extends Item {
    public HWBasicItem() {
        super();
    }

    public HWBasicItem(String unlocalisedName) {
        super();

        setItemName(this, unlocalisedName);
        this.setCreativeTab(HWRegistry.tabGeneral);
    }

    public static void setItemName(Item item, String itemName) {
        item.setRegistryName(itemName);
        item.setUnlocalizedName(item.getRegistryName().toString());
    }
}
