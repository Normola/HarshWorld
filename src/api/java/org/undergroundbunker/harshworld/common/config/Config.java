package org.undergroundbunker.harshworld.common.config;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;
import org.undergroundbunker.harshworld.library.Util;

public class Config {
    public static Config instance = new Config();
    public static Logger log = Util.getLogger("Config");

    private Config() {}

    static Configuration configFile;

    static ConfigCategory Gameplay;

    public static void load(FMLPreInitializationEvent e) {
        configFile = new Configuration(e.getSuggestedConfigurationFile(), "0.1", false);

        MinecraftForge.EVENT_BUS.register(instance);

        syncConfig();
    }

    @SubscribeEvent
    public void update(ConfigChangedEvent.OnConfigChangedEvent e) {
        if(e.getModID().equals(Util.ModID)) {
            syncConfig();
        }
    }

    public static boolean syncConfig() {


        return false;
    }

}
