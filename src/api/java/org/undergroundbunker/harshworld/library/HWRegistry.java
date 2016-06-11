package org.undergroundbunker.harshworld.library;

import gnu.trove.map.hash.THashMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.undergroundbunker.harshworld.client.CreativeTab;
import org.undergroundbunker.harshworld.library.modifiers.IModifier;

import java.util.Map;

public class HWRegistry {

    // Creative Tabs
    public static CreativeTab tabGeneral = new CreativeTab("HWGeneral", new ItemStack(Items.SLIME_BALL));

    // Modifiers
    private static final Map<String, IModifier> modifiers = new THashMap<String, IModifier>();

    public static IModifier getModifier(String identifier) {
        return modifiers.get(identifier);
    }
}
