package org.undergroundbunker.harshworld;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;
import org.undergroundbunker.harshworld.common.HWCommonProxy;
import org.undergroundbunker.harshworld.library.Util;

import java.util.Map;


@Mod(modid = HarshWorld.ModID, name = HarshWorld.ModName, version = HarshWorld.Version)
public class HarshWorld {
    public static final String ModID = Util.ModID;
    public static final String ModName = Util.ModName;
    public static final String Version = "${version}";

    public static final Logger log = Util.getLogger(Util.ModName);

    @Instance
    public static HarshWorld instance = new HarshWorld();

    @SidedProxy(clientSide = "org.undergroundbunker.harshworld.client.HWClientProxy", serverSide = "org.undergroundbunker.harshworld.server.HWServerProxy")
    public static HWCommonProxy proxy;

    public HarshWorld() {
        if (Loader.isModLoaded("TConstruct")) {
            log.info(" *Waves to TConstruct* ");
        }
        else {
            log.info(" *So Ronery* ");
        }
    }

    @NetworkCheckHandler()
    public boolean matchModVersions(Map<String, String> remoteVersions, Side side) {
        return remoteVersions.containsKey(Util.ModID) && Version.equals(remoteVersions.get(Util.ModID));
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        proxy.preInit(e);
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }
}

