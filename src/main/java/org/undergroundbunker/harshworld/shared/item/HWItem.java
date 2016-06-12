package org.undergroundbunker.harshworld.shared.item;

import gnu.trove.set.hash.THashSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.undergroundbunker.harshworld.library.Category;
import org.undergroundbunker.harshworld.library.HWRegistry;
import org.undergroundbunker.harshworld.library.Util;
import org.undergroundbunker.harshworld.library.modifiers.IModifier;
import org.undergroundbunker.harshworld.library.modifiers.ModifierNBT;
import org.undergroundbunker.harshworld.library.utils.TagUtil;
import org.undergroundbunker.harshworld.library.utils.ToolHelper;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class HWItem extends Item {

    protected final Set<Category> categories = new THashSet<Category>();

    public HWItem() {

        this.setMaxStackSize(1);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Nonnull
    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemStack) {

        EntityItem entity = new EntityItem(world, location.posX, location.prevPosY, location.posZ);

        if (location instanceof EntityItem) {
            NBTTagCompound tag = new NBTTagCompound();
            location.writeToNBT(tag);
            entity.setPickupDelay(tag.getShort("PickupDelay"));
        }

        entity.motionX = location.motionX;
        entity.motionY = location.motionY;
        entity.motionZ = location.motionZ;

        return entity;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip ,boolean advanced) {

        boolean shift = Util.isShiftKeyDown();
        boolean ctrl = Util.isCtrlKeyDown();

        if (!shift && !ctrl) {
            getSimpleData(stack, player, tooltip);
        }
        else if(shift) {
            // Ready for advanced tooltips
            getSimpleData(stack, player, tooltip);
        }
        else {
            // Ready for advanced tooltips
            getSimpleData(stack, player, tooltip);
        }
    }

    private void getSimpleData(ItemStack stack, EntityPlayer player, List<String> tooltip) {
        DecimalFormat df = new DecimalFormat("#.##");
        getTooltip(stack, tooltip);

        tooltip.add("");

        tooltip.add(Util.translate("tooltip.tool.holdShift"));
        tooltip.add(Util.translate("tooltip.tool.holdCtrl"));

        String format = df.format(ToolHelper.getActualDamage(stack, player));
        String attackDamageLocal = Util.translateToLocal("attribute.name.generic.attackDamage");
        String modifierFormatted = Util.translateToLocal("attribute.modifier.plus.0", format, attackDamageLocal);

        tooltip.add(TextFormatting.BLUE + modifierFormatted);
    }

    public void getTooltip(ItemStack stack, List<String> tooltips) {
        NBTTagList tagList = TagUtil.getModifiersTagList(stack);

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            ModifierNBT data = ModifierNBT.readTag(tag);

            IModifier modifier = HWRegistry.getModifier(data.identifier);
            if (modifier != null && !modifier.isHidden()) {
                tooltips.add(data.getColourString() + modifier.getTooltip(tag, false));
            }
        }
    }

    protected void addCategory(Category... categories) {
        Collections.addAll(this.categories, categories);
    }

    public boolean hasCategory(Category category) {
        return categories.contains(category);
    }

    protected Category[] getCategories() {
        Category[] out = new Category[categories.size()];
        int i = 0;
        for(Category category : categories) {
            out[i++] = category;
        }

        return out;
    }

}
