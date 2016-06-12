package org.undergroundbunker.harshworld.library.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import org.undergroundbunker.harshworld.library.Category;

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
        if (stack == null || stack.getItem() == null || !stack.hasTagCompound()) {
            return new NBTTagCompound();
        }

        return stack.getTagCompound();
    }

    public static NBTTagCompound getTagSafe(NBTTagCompound tag, String key) {
        if (tag == null || !tag.hasKey(key)) {
            return new NBTTagCompound();
        }

        return tag.getCompoundTag(key);
    }

    public static NBTTagList getTagListSafe(NBTTagCompound tag, String key, int type) {
        if (tag == null || !tag.hasKey(key)) {
            return new NBTTagList();
        }

        return tag.getTagList(key, type);
    }

    public static NBTTagCompound getBaseTag(ItemStack stack) {
        return getBaseTag(getTagSafe(stack));
    }

    public static NBTTagCompound getBaseTag(NBTTagCompound root) {
        return getTagSafe(root, Tags.BASE_DATA);
    }

    public static void setBaseTag(ItemStack stack, NBTTagCompound tag) {
        NBTTagCompound root = TagUtil.getTagSafe(stack);
        setBaseTag(root, tag);

        stack.setTagCompound(root);
    }

    public static void setBaseTag(NBTTagCompound root, NBTTagCompound tag) {
        if (root != null) {
            root.setTag(Tags.BASE_DATA, tag);
        }
    }

    public static NBTTagList getModifiersTagList(ItemStack stack) {
        return getModifiersTagList(getTagSafe(stack));
    }

    public static void setBaseModifiersTagList(NBTTagCompound root, NBTTagList tagList) {
        getBaseTag(root).setTag(Tags.BASE_MODIFIERS, tagList);
    }

    public static NBTTagList getModifiersTagList(NBTTagCompound root) {
        return getTagListSafe(root, Tags.TOOL_MODIFIERS, TAG_TYPE_COMPOUND);
    }

    public static void setModifiersTagList(NBTTagCompound root, NBTTagList tagList) {
        if (root != null) {
            root.setTag(Tags.TOOL_MODIFIERS, tagList);
        }
    }

    public static NBTTagList getBaseModifiersTagList(ItemStack stack) {
        return getBaseModifiersTagList(getTagSafe(stack));
    }

    public static NBTTagList getBaseModifiersTagList(NBTTagCompound root) {
        return getTagListSafe(getBaseTag(root), Tags.BASE_MODIFIERS, TAG_TYPE_STRING);
    }

    public static void setBaseModifiersTagList(ItemStack stack, NBTTagList tagList) {
        NBTTagCompound root = TagUtil.getTagSafe(stack);
        setBaseModifiersTagList(root, tagList);

        stack.setTagCompound(root);
    }

    public static NBTTagList getBaseMaterialsTagList(ItemStack stack) {
        return getBaseMaterialsTagList(getTagSafe(stack));
    }

    public static NBTTagList getBaseMaterialsTagList(NBTTagCompound root) {
        return getTagListSafe(getBaseTag(root), Tags.BASE_MATERIALS, TAG_TYPE_STRING);
    }

    public static void setBaseMaterialsTagList(ItemStack stack, NBTTagList tagList) {
        NBTTagCompound root = TagUtil.getTagSafe(stack);
        setBaseMaterialsTagList(root, tagList);

        stack.setTagCompound(root);
    }

    public static void setBaseMaterialsTagList(NBTTagCompound root, NBTTagList tagList) {
        getBaseTag(root).setTag(Tags.BASE_MATERIALS, tagList);
    }

    public static int getBaseModifiersUsed(NBTTagCompound root) {
        return getBaseTag(root).getInteger(Tags.BASE_USED_MODIFIERS);
    }

    public static void setBaseModifiersUsed(NBTTagCompound root, int count) {
        getBaseTag(root).setInteger(Tags.BASE_USED_MODIFIERS, count);
    }

    public static void setModifiersTagList(ItemStack stack, NBTTagList tagList) {
        NBTTagCompound root = TagUtil.getTagSafe(stack);
        setModifiersTagList(root, tagList);

        stack.setTagCompound(root);
    }

    public static void setToolTag(ItemStack stack, NBTTagCompound tag) {
        NBTTagCompound root = TagUtil.getTagSafe(stack);
        setToolTag(root, tag);

        stack.setTagCompound(root);
    }

    public static void setToolTag(NBTTagCompound root, NBTTagCompound tag) {
        if (root != null) {
            root.setTag(Tags.TOOL_DATA, tag);
        }
    }

    // Extra data
    public static NBTTagCompound getExtraTag(ItemStack stack) {
        return getExtraTag(getTagSafe(stack));
    }

    public static NBTTagCompound getExtraTag(NBTTagCompound root) {
        return getTagSafe(root, Tags.TINKER_EXTRA);
    }

    public static void setExtraTag(ItemStack stack, NBTTagCompound tag) {
        NBTTagCompound root = getTagSafe(stack);
        setExtraTag(root, tag);
        stack.setTagCompound(root);
    }

    public static void setExtraTag(NBTTagCompound root, NBTTagCompound tag) {
        root.setTag(Tags.TINKER_EXTRA, tag);
    }

    public static Category[] getCategories(NBTTagCompound root) {
        NBTTagList categories = getTagListSafe(getExtraTag(root), Tags.EXTRA_CATEGORIES, 8);
        Category[] out = new Category[categories.tagCount()];
        for(int i = 0; i < out.length; i++) {
            out[i] = Category.categories.get(categories.getStringTagAt(i));
        }

        return out;
    }

    public static void setCategories(ItemStack stack, Category[] categories) {
        NBTTagCompound root = getTagSafe(stack);
        setCategories(root, categories);
        stack.setTagCompound(root);
    }

    public static void setCategories(NBTTagCompound root, Category[] categories) {
        NBTTagList list = new NBTTagList();
        for(Category category : categories) {
            list.appendTag(new NBTTagString(category.name));
        }

        NBTTagCompound extra = getExtraTag(root);
        extra.setTag(Tags.EXTRA_CATEGORIES, list);
        setExtraTag(root, extra);
    }

    public static void setEnchantEffect(ItemStack stack, boolean active) {
        NBTTagCompound root = getTagSafe(stack);
        setEnchantEffect(root, active);
        stack.setTagCompound(root);
    }

    public static void setEnchantEffect(NBTTagCompound root, boolean active) {
        if(active) {
            root.setBoolean(Tags.ENCHANT_EFFECT, true);
        }
        else {
            root.removeTag(Tags.ENCHANT_EFFECT);
        }
    }

    public static boolean hasEnchantEffect(ItemStack stack) {
        return hasEnchantEffect(getTagSafe(stack));
    }

    public static boolean hasEnchantEffect(NBTTagCompound root) {
        return root.getBoolean(Tags.ENCHANT_EFFECT);
    }


}
