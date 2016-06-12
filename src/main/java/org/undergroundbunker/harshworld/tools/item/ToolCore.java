package org.undergroundbunker.harshworld.tools.item;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.undergroundbunker.harshworld.client.HWClientProxy;
import org.undergroundbunker.harshworld.library.Category;
import org.undergroundbunker.harshworld.library.HWRegistry;
import org.undergroundbunker.harshworld.library.Util;
import org.undergroundbunker.harshworld.library.materials.Material;
import org.undergroundbunker.harshworld.library.tools.IAoeTool;
import org.undergroundbunker.harshworld.library.utils.HWUtil;
import org.undergroundbunker.harshworld.library.utils.TagUtil;
import org.undergroundbunker.harshworld.library.utils.ToolHelper;
import org.undergroundbunker.harshworld.library.utils.TooltipBuilder;
import org.undergroundbunker.harshworld.shared.item.HWItem;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ToolCore extends HWItem {

    public ToolCore() {
        super();

        this.setCreativeTab(HWRegistry.tabTools);

        HWRegistry.registerTool(this);
        addCategory(Category.TOOL);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return ToolHelper.getDurabilityStat(stack);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        super.setDamage(stack, damage);

        if (getDamage(stack) == getMaxDamage(stack)) {
            ToolHelper.breakTool(stack, null);
        }
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    public float miningSpeedModifier() {
        return 1f;
    }

    public abstract float damagePotential();

    public float damageCutOff() {
        return 15f;
    }

    public abstract float attackSpeed();

    public float knockback() {
        return 1.0f;
    }

    public boolean readyForSpecialAttack(EntityLivingBase player) {
        return player instanceof EntityPlayer && ((EntityPlayer) player).getCooledAttackStrength(0.5f) > 0.9f;
    }

    public void reduceDurabilityOnHit(ItemStack stack, EntityPlayer player, float damage) {
        damage = Math.max(1f, damage / 10f);

        if (!hasCategory(Category.WEAPON)) {
            damage *= 2;
        }
        ToolHelper.damageTool(stack, (int) damage, player);
    }

    @Override
    public float getStrVsBlock(ItemStack stack, IBlockState state) {
        if (isEffective(state) || ToolHelper.isToolEffective(stack, state)) {
            return ToolHelper.calcDigSpeed(stack, state);
        }
        return super.getStrVsBlock(stack, state);
    }

    public boolean isEffective(IBlockState state) {
        return false;
    }

    @Override
    public boolean canHarvestBlock(@Nonnull IBlockState state, ItemStack stack) {
        return isEffective(state);
    }

    public boolean dealDamage(ItemStack stack, EntityLivingBase player, EntityLivingBase entity, float damage) {
        if(player instanceof EntityPlayer) {
            return entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), damage);
        }
        return entity.attackEntityFrom(DamageSource.causeMobDamage(player), damage);
    }


    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {

        if (!ToolHelper.isBroken(stack) && this instanceof IAoeTool && ((IAoeTool) this).isAoeHarvestTool() && ToolHelper.isToolEffective2(stack, player.worldObj.getBlockState(pos))) {
            for (BlockPos extraPos : ((IAoeTool) this).getAoeBlocks(stack, player.worldObj, player, pos)) {
                ToolHelper.breakExtraBlock(stack, player.worldObj, player, extraPos, pos);
            }
        }

        return super.onBlockStartBreak(stack, pos, player);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        return ToolHelper.attackEntity(stack, this, player, entity);
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack) {
        return super.onEntitySwing(entity, stack);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {

        float speed = ToolHelper.getActualAttackSpeed(stack);
        int time = Math.round(20f / speed);

        if (time < target.hurtResistantTime / 2) {
            target.hurtResistantTime = (target.hurtResistantTime + time) / 2;
            target.hurtTime = (target.hurtTime + time) / 2;
        }

        return super.hitEntity(stack, target, attacker);
    }

    @Nonnull
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EntityEquipmentSlot slot, ItemStack stack) {

        Multimap<String, AttributeModifier> multiMap = super.getAttributeModifiers(slot, stack);

        if (slot == EntityEquipmentSlot.MAINHAND && !ToolHelper.isBroken(stack)) {
            multiMap.put(
                    SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(),
                    new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier",
                            (double) ToolHelper.getActualAttack(stack), 0));
            multiMap.put(
                    SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName(),
                    new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier",
                            (double) ToolHelper.getActualAttackSpeed(stack) - 4d, 0));
        }

        return multiMap;
    }

//    @Override
//    public List<String> getInformation(ItemStack stack) {
//        return getInformation(stack, true);
//    }

    @Override
    public void getTooltip(ItemStack stack, List<String> tooltips) {
        if (ToolHelper.isBroken(stack)) {
            tooltips.add("" + TextFormatting.DARK_RED + TextFormatting.BOLD + getBrokenTooltip(stack));
        }
        super.getTooltip(stack, tooltips);
    }

    protected String getBrokenTooltip(ItemStack stack) {
        return Util.translate(TooltipBuilder.LOC_Broken);
    }

//    @Override
//    public void getTooltipDetailed(ItemStack stack, List<String> tooltips) {
//        tooltips.addAll(getInformation(stack, false));
//    }
//
//    public List<String> getInformation(ItemStack stack, boolean detailed) {
//        TooltipBuilder info = new TooltipBuilder(stack);
//
//        info.addDurability(!detailed);
//
//        if(hasCategory(Category.HARVEST)) {
//            info.addHarvestLevel();
//            info.addMiningSpeed();
//        }
//        info.addAttack();
//
//        if (Tool.getFreeModifiers(stack) > 0) {
//            info.addFreeModifiers();
//        }
//
//        if (detailed) {
//            info.addModifierInfo();
//        }
//
//        return info.getTooltip();
//    }

    @Nonnull
    @SideOnly(Side.CLIENT)
    @Override
    public FontRenderer getFontRenderer(ItemStack stack) {
        return HWClientProxy.fontRenderer;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return TagUtil.hasEnchantEffect(stack);
    }

    @Nonnull
    @Override
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {

        List<Material> materials = HWUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(stack));
        Set<Material> nameMaterials = Sets.newLinkedHashSet();

        String itemName = super.getItemStackDisplayName(stack);


        return itemName;

        // TODO: Not sure this is right at the mo.
    }

    @Override
    public void getSubItems(@Nonnull Item item, CreativeTabs tab, List<ItemStack> subItems) {
        addDefaultSubItems(subItems);
    }

    protected void addDefaultSubItems(List<ItemStack> subItems) {
        // TODO: Not sure this is right at the mo.

    }

    @Override
    public int getHarvestLevel(ItemStack stack, @Nonnull String toolClass) {

        if (this.getToolClasses(stack).contains(toolClass)) {
            NBTTagCompound tag = TagUtil.getToolTag(stack);

            return ToolHelper.getHarvestLevelStat(stack);
        }

        return super.getHarvestLevel(stack, toolClass);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entity, itemSlot, isSelected);
    }

    @Nonnull
    @Override
    public RayTraceResult rayTrace(World world, EntityPlayer player, boolean useLiquids) {
        return super.rayTrace(world, player, useLiquids);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, @Nonnull ItemStack newStack, boolean slotChanged) {

        if (oldStack == newStack) {
            return false;
        }

        if (slotChanged) {
            return true;
        }

        if (oldStack.hasEffect() != newStack.hasEffect()) {
            return true;
        }

        Multimap<String, AttributeModifier> attributes = newStack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);

        for (Map.Entry<String, AttributeModifier> entry : oldStack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).entries()) {
            if (!attributes.containsKey(entry.getKey())) {
                return true;
            }
            if (!attributes.get(entry.getKey()).equals(entry.getValue())) {
                return true;
            }
        }

        return !ItemStack.areItemStacksEqual(oldStack, newStack);
    }
}
