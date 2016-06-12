package org.undergroundbunker.harshworld.client;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.undergroundbunker.harshworld.common.HWCommonProxy;
import org.undergroundbunker.harshworld.library.client.CustomFontRenderer;

public class HWClientProxy extends HWCommonProxy {

    public static CustomFontRenderer fontRenderer;

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }
}
