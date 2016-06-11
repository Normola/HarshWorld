package org.undergroundbunker.harshworld.library.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public interface IToolMod {

    String getIdentifier();
    String getLocalisedName();
    String getLocalisedDesc();
    List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag);
    boolean isHidden();
}
