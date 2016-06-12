package org.undergroundbunker.harshworld.library;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Maps;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import gnu.trove.set.hash.TLinkedHashSet;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.Logger;
import org.undergroundbunker.harshworld.client.CreativeTab;
import org.undergroundbunker.harshworld.library.events.MaterialEvent;
import org.undergroundbunker.harshworld.library.materials.Material;
import org.undergroundbunker.harshworld.library.modifiers.IModifier;
import org.undergroundbunker.harshworld.tools.item.ToolCore;

import java.util.Map;
import java.util.Set;

public class HWRegistry {

    private static final Logger log = Util.getLogger("HWRegistry");

    // Creative Tabs
    public static CreativeTab tabGeneral = new CreativeTab("HWGeneral", new ItemStack(Items.SLIME_BALL));
    public static CreativeTabs tabTools = new CreativeTab("HWTools", new ItemStack(Items.WOODEN_SHOVEL));

    // Materials
    private static final Map<String, Material> materials = Maps.newLinkedHashMap();
    private static final Map<String, String> materialRegisteredByMod = new THashMap<String, String>();
    private static final Set<String> cancelledMaterials = new THashSet<String>();



    public static void addMaterial(Material material) {
        if (CharMatcher.WHITESPACE.matchesAnyOf(material.getIdentifier())) {
            error("Could not register Material \"%s\": Material identifier must not contain any spaces.", material.identifier);
            return;
        }
        if (CharMatcher.JAVA_UPPER_CASE.matchesAnyOf(material.getIdentifier())) {
            error("Could not register Material \"%s\": Material identifier must be completely lowercase.", material.identifier);
            return;
        }
        if (materials.containsKey(material.identifier)) {
            String registeredBy = materialRegisteredByMod.get(material.identifier);
            error(String.format(
                    "Could not register Material \"%s\": It was already registered by %s",
                    material.identifier,
                    registeredBy));
            return;
        }

        MaterialEvent.MaterialRegisterEvent event = new MaterialEvent.MaterialRegisterEvent(material);

        if(MinecraftForge.EVENT_BUS.post(event)) {
            log.trace("Addition of material {} cancelled by event", material.getIdentifier());
            cancelledMaterials.add(material.getIdentifier());
            return;
        }

        materials.put(material.identifier, material);
        putMaterialTrace(material.identifier);

    }

    public static Material getMaterial(String identifier) {
        return materials.containsKey(identifier) ? materials.get(identifier) : Material.UNKNOWN;
    }

    // Tools, Weapons and Crafting

    private static final Set<ToolCore> tools = new TLinkedHashSet<ToolCore>();

    public static void registerTool(ToolCore tool) {
        tools.add(tool);
    }


    // Modifiers
    private static final Map<String, IModifier> modifiers = new THashMap<String, IModifier>();

    public static IModifier getModifier(String identifier) {
        return modifiers.get(identifier);

    }


    // Logging

    static void putMaterialTrace(String materialIdentifier) {
        String activeMod = Loader.instance().activeModContainer().getName();
        materialRegisteredByMod.put(materialIdentifier, activeMod);
    }

    private static void error(String message, Object... params) {
        throw new HWAPIException(String.format(message, params));
    }
}
