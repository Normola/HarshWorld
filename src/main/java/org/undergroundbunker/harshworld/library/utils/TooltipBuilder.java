package org.undergroundbunker.harshworld.library.utils;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.undergroundbunker.harshworld.library.HWRegistry;
import org.undergroundbunker.harshworld.library.modifiers.IModifier;
import org.undergroundbunker.harshworld.library.modifiers.ModifierNBT;

import java.util.List;
import java.util.stream.Collectors;

public class TooltipBuilder {

    public final static String LOC_FreeModifiers = "tooltip.tool.modifiers";
    public final static String LOC_Ammo = "stat.projectile.ammo.name";

    public static final String LOC_Broken = "tooltip.tool.broken";
    public static final String LOC_Empty = "tooltip.tool.empty";

    private final List<String> tips = Lists.newLinkedList();
    private final ItemStack stack;

    public TooltipBuilder(ItemStack stack) {
        this.stack = stack;
    }

    public List<String> getTooltip() {
        return tips;
    }

    public TooltipBuilder add(String text) {
        tips.add(text);

        return this;
    }

    @SuppressWarnings("deprecation")
    public TooltipBuilder addFreeModifiers() {
        tips.add(String.format("%s: %d", net.minecraft.util.text.translation.I18n.translateToLocal(LOC_FreeModifiers),
                ToolHelper.getFreeModifiers(stack)));

        return this;
    }

    public TooltipBuilder addModifierInfo() {
        NBTTagList tagList = TagUtil.getModifiersTagList(stack);
        for(int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            ModifierNBT data = ModifierNBT.readTag(tag);

            IModifier modifier = HWRegistry.getModifier(data.identifier);
            if(modifier == null || modifier.isHidden()) {
                continue;
            }

            tips.addAll(modifier.getExtraInfo(stack, tag).stream()
                    .filter(string -> !string.isEmpty())
                    .map(string -> data.getColourString() + string)
                    .collect(Collectors.toList()));
        }

        return this;
    }
}
