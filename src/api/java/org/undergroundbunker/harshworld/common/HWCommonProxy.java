package org.undergroundbunker.harshworld.common;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.undergroundbunker.harshworld.shared.item.HWModItems;

public class HWCommonProxy {

    public void preInit(FMLPreInitializationEvent e) {
        HWModItems.createItems();
    }

    public void init(FMLInitializationEvent e) {

    }

    public void postInit(FMLPostInitializationEvent e) {

    }
}
