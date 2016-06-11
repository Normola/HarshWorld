package org.undergroundbunker.harshworld.library.utils;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ToolHelper {
    private ToolHelper() {
    }

    public static float getActualDamage(ItemStack stack, EntityPlayer player) {
        float damage = (float)player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        damage += ToolHelper.getActualAttack(stack);

        return damage;
    }

    private static float getActualAttack(ItemStack stack) {
        return getAttackSpeedStat(stack);
        // This is here so we can do some fancy stuff later
    }

    private static float getAttackSpeedStat(ItemStack stack) {
        return getFloatTag(stack, Tags.ATTACK_SPEED_MULTIPLIER);
    }

    static int getIntTag(ItemStack stack, String key) {
        NBTTagCompound tag = TagUtil.getToolTag(stack);

        return tag.getInteger(key);
    }

    static float getFloatTag(ItemStack stack, String key) {
        NBTTagCompound tag = TagUtil.getToolTag(stack);

        return tag.getFloat(key);
    }

}
