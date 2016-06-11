package org.undergroundbunker.harshworld.library.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class TagUtil {

    public static int TAG_TYPE_STRING = (new NBTTagString()).getId();
    public static int TAG_TYPE_COMPOUND = (new NBTTagCompound()).getId();


    public static NBTTagCompound getToolTag(ItemStack stack) {
        return getToolTag(getTagSafe(stack));
    }

    public static NBTTagCompound getToolTag(NBTTagCompound root) {
        return getTagSafe(root, Tags.TOOL_DATA);
    }

    public static NBTTagCompound getTagSafe(ItemStack stack) {
        if(stack == null || stack.getItem() == null || !stack.hasTagCompound()) {
            return new NBTTagCompound();
        }

        return stack.getTagCompound();
    }

    public static NBTTagCompound getTagSafe(NBTTagCompound tag, String key) {
        if(tag == null || !tag.hasKey(key)) {
            return new NBTTagCompound();
        }

        return tag.getCompoundTag(key);
    }

    public static NBTTagList getTagListSafe(NBTTagCompound tag, String key, int type) {
        if(tag == null || !tag.hasKey(key)) {
            return new NBTTagList();
        }

        return tag.getTagList(key, type);
    }

    public static NBTTagList getModifiersTagList(ItemStack stack) {
        return getModifiersTagList(getTagSafe(stack));
    }

    public static NBTTagList getModifiersTagList(NBTTagCompound root) {
        return getTagListSafe(root, Tags.TOOL_MODIFIERS, TAG_TYPE_COMPOUND);
    }

    public static void setModifiersTagList(NBTTagCompound root, NBTTagList tagList) {
        if(root != null) {
            root.setTag(Tags.TOOL_MODIFIERS, tagList);
        }
    }

    public static void setModifiersTagList(ItemStack stack, NBTTagList tagList) {
        NBTTagCompound root = TagUtil.getTagSafe(stack);
        setModifiersTagList(root, tagList);

        stack.setTagCompound(root);
    }

}
