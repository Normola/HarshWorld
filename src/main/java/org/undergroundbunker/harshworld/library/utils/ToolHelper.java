package org.undergroundbunker.harshworld.library.utils;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import org.undergroundbunker.harshworld.common.network.HWNetwork;
import org.undergroundbunker.harshworld.tools.item.ToolCore;
import org.undergroundbunker.harshworld.tools.network.ToolBreakAnimationPacket;

import java.util.List;

public class ToolHelper {
    private ToolHelper() {
    }

    public static float getActualDamage(ItemStack stack, EntityPlayer player) {
        float damage = (float)player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        damage += ToolHelper.getActualAttack(stack);

        return damage;
    }

    public static float getActualAttack(ItemStack stack) {
        return getAttackSpeedStat(stack);
        // This is here so we can do some fancy stuff later
    }

    public static float getActualAttackSpeed(ItemStack stack) {
        float speed = getAttackSpeedStat(stack);
        if(stack != null && stack.getItem() instanceof ToolCore) {
            speed *= ((ToolCore) stack.getItem()).attackSpeed();
        }
        return speed;
    }

    public static float getActualMiningSpeed(ItemStack stack) {
        float speed = getMiningSpeedStat(stack);
        if(stack != null && stack.getItem() instanceof ToolCore) {
            speed *= ((ToolCore) stack.getItem()).miningSpeedModifier();
        }
        return speed;
    }

    public static int getFreeModifiers(ItemStack stack) {
        return getIntTag(stack, Tags.FREE_MODIFIERS);
    }

    private static float getAttackSpeedStat(ItemStack stack) {
        return getFloatTag(stack, Tags.ATTACK_SPEED_MULTIPLIER);
    }

    public static float getMiningSpeedStat(ItemStack stack) {
        return getFloatTag(stack, Tags.MINING_SPEED);
    }

    static int getIntTag(ItemStack stack, String key) {
        NBTTagCompound tag = TagUtil.getToolTag(stack);

        return tag.getInteger(key);
    }

    static float getFloatTag(ItemStack stack, String key) {
        NBTTagCompound tag = TagUtil.getToolTag(stack);

        return tag.getFloat(key);
    }

    public static int getDurabilityStat(ItemStack stack) {
        return getIntTag(stack, Tags.DURABILITY);
    }



    // Tool Durability

    public static void damageTool(ItemStack stack, int amount, EntityLivingBase entity) {

        if (amount == 0 || isBroken(stack)) {
            return;
        }

        int actualAmount = amount;
        actualAmount = Math.min(actualAmount, getCurrentDurability(stack));
        stack.setItemDamage(stack.getItemDamage() + actualAmount);

        if (getCurrentDurability(stack) == 0) {
            breakTool(stack, entity);
        }
    }

    public static int getCurrentDurability(ItemStack stack) {
        return stack.getMaxDamage() - stack.getItemDamage();
    }

    public static boolean isBroken(ItemStack stack) {
        return TagUtil.getToolTag(stack).getBoolean(Tags.BROKEN);
    }

    public static void breakTool(ItemStack stack, EntityLivingBase entity) {

        NBTTagCompound tag = TagUtil.getToolTag(stack);

        tag.setBoolean(Tags.BROKEN, true);
        TagUtil.setToolTag(stack, tag);

        if (entity instanceof EntityPlayerMP) {
            HWNetwork.sendTo(new ToolBreakAnimationPacket(stack), (EntityPlayerMP) entity);
        }
    }

    public static void breakExtraBlock(ItemStack stack, World world, EntityPlayer player, BlockPos pos, BlockPos refPos) {

        if (world.isAirBlock(pos))
            return;

        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if(!isToolEffective2(stack, state)) {
            return;
        }

        IBlockState refState = world.getBlockState(refPos);
        float refStrength = ForgeHooks.blockStrength(refState, player, world, refPos);
        float strength = ForgeHooks.blockStrength(state, player, world, pos);

        if (!ForgeHooks.canHarvestBlock(block, player, world, pos) || refStrength/strength > 10f)
            return;

        if (player.capabilities.isCreativeMode) {
            block.onBlockHarvested(world, pos, state, player);
            if (block.removedByPlayer(state, world, pos, player, false))
                block.onBlockDestroyedByPlayer(world, pos, state);

            if (!world.isRemote) {
                ((EntityPlayerMP)player).connection.sendPacket(new SPacketBlockChange(world, pos));
            }
            return;
        }

        stack.onBlockDestroyed(world, state, pos, player);

        if (!world.isRemote) {
            int xp = ForgeHooks.onBlockBreakEvent(world, ((EntityPlayerMP) player).interactionManager.getGameType(), (EntityPlayerMP) player, pos);
            if(xp == -1) {
                return;
            }

            block.onBlockHarvested(world, pos, state, player);

            if(block.removedByPlayer(state, world, pos, player, true)) // boolean is if block can be harvested, checked above
            {
                block.onBlockDestroyedByPlayer(world, pos, state);
                block.harvestBlock(world, player, pos, state, world.getTileEntity(pos), stack);
                block.dropXpOnBlockBreak(world, pos, xp);
            }

            EntityPlayerMP mpPlayer = (EntityPlayerMP) player;
            mpPlayer.connection.sendPacket(new SPacketBlockChange(world, pos));
        }
        else {
            PlayerControllerMP pcmp = Minecraft.getMinecraft().playerController;

            world.playBroadcastSound(2001, pos, Block.getStateId(state));
            if(block.removedByPlayer(state, world, pos, player, true))
            {
                block.onBlockDestroyedByPlayer(world, pos, state);
            }
            stack.onBlockDestroyed(world, state, pos, player);

            if (stack.stackSize == 0 && stack == player.getHeldItemMainhand())
            {
                ForgeEventFactory.onPlayerDestroyItem(player, stack, EnumHand.MAIN_HAND);
                player.setHeldItem(EnumHand.MAIN_HAND, null);
            }

            Minecraft.getMinecraft().getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, Minecraft
                    .getMinecraft().objectMouseOver.sideHit));
        }
    }


    public static boolean isToolEffective(ItemStack stack, IBlockState state) {

        for (String type : stack.getItem().getToolClasses(stack)) {
            if (state.getBlock().isToolEffective(type, state)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isToolEffective2(ItemStack stack, IBlockState state) {

        return isToolEffective(stack, state)
                || stack.getItem() instanceof ToolCore
                && ((ToolCore) stack.getItem()).isEffective(state);

    }

    public static int getHarvestLevelStat(ItemStack stack) {
        return getIntTag(stack, Tags.HARVEST_LEVEL);
    }

    public static float calcDigSpeed(ItemStack stack, IBlockState blockState) {
        if(blockState == null) {
            return 0f;
        }

        if(!stack.hasTagCompound()) {
            return 1f;
        }

        // check if the tool has the correct class and harvest level
        if(!canHarvest(stack, blockState)) {
            return 0f;
        }

        if(isBroken(stack)) {
            return 0.3f;
        }

        // calculate speed depending on stats
        NBTTagCompound tag = TagUtil.getToolTag(stack);
        float speed = tag.getFloat(Tags.MINING_SPEED);

        if(stack.getItem() instanceof ToolCore) {
            speed *= ((ToolCore) stack.getItem()).miningSpeedModifier();
        }

        return speed;
    }

    public static boolean attackEntity(ItemStack stack, ToolCore tool, EntityLivingBase attacker, Entity targetEntity) {
        return attackEntity(stack, tool, attacker, targetEntity, false);
    }

    public static boolean attackEntity(ItemStack stack, ToolCore tool, EntityLivingBase attacker, Entity targetEntity, boolean isProjectile) {
        // nothing to do, no target?
        if(targetEntity == null
                || !targetEntity.canBeAttackedWithItem()
                || targetEntity.hitByEntity(attacker)
                || !stack.hasTagCompound()) {
            return false;
        }

        if(!(targetEntity instanceof EntityLivingBase)) {
            return false;
        }

        if(isBroken(stack)) {
            return false;
        }

        if(attacker == null) {
            return false;
        }

        EntityLivingBase target = (EntityLivingBase) targetEntity;

        EntityPlayer player = null;

        if(attacker instanceof EntityPlayer) {
            player = (EntityPlayer) attacker;
        }

        float baseDamage = (float)attacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        float baseKnockback = attacker.isSprinting() ? 1 : 0;

        boolean isCritical = attacker.fallDistance > 0.0F
                && !attacker.onGround
                && !attacker.isOnLadder()
                && !attacker.isInWater()
                && !attacker.isPotionActive(MobEffects.BLINDNESS)
                && !attacker.isRiding();

        float damage = baseDamage;

        // apply critical damage
        if(isCritical) {
            damage *= 1.5f;
        }

        // calculate cutoff
        damage = calcCutoffDamage(damage, tool.damageCutOff());

        float knockback = baseKnockback;
        float oldHP = target.getHealth();

        double oldVelX = target.motionX;
        double oldVelY = target.motionY;
        double oldVelZ = target.motionZ;

        if(player != null) {
            float f2 = player.getCooledAttackStrength(0.5F);
            damage *= (0.2F + f2 * f2 * 0.8F);
        }

        int hurtResistantTime = target.hurtResistantTime;
        boolean hit = tool.dealDamage(stack, attacker, target, damage);

        if(hit) {
            float damageDealt = oldHP - target.getHealth();

            oldVelX = target.motionX = oldVelX + (target.motionX - oldVelX)*tool.knockback();
            oldVelY = target.motionY = oldVelY + (target.motionY - oldVelY)*tool.knockback()/3f;
            oldVelZ = target.motionZ = oldVelZ + (target.motionZ - oldVelZ)*tool.knockback();

            if(knockback > 0f) {
                double velX = -MathHelper.sin(attacker.rotationYaw * (float) Math.PI / 180.0F) * knockback * 0.5F;
                double velZ = MathHelper.cos(attacker.rotationYaw * (float)Math.PI / 180.0F) * knockback * 0.5F;
                targetEntity.addVelocity(velX, 0.1d, velZ);

                attacker.motionX *= 0.6f;
                attacker.motionZ *= 0.6f;
                attacker.setSprinting(false);
            }

            if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged)
            {
                ((EntityPlayerMP)targetEntity).connection.sendPacket(new SPacketEntityVelocity(targetEntity));
                targetEntity.velocityChanged = false;
                targetEntity.motionX = oldVelX;
                targetEntity.motionY = oldVelY;
                targetEntity.motionZ = oldVelZ;
            }

            if(player != null) {

                if(isCritical) {
                    player.onCriticalHit(target);
                }


                if(damage > baseDamage) {
                    player.onEnchantmentCritical(targetEntity);
                }

                if(damage >= 18f) {
                    player.addStat(AchievementList.OVERKILL);
                }
            }

            attacker.setLastAttacker(target);

            if(player != null) {
                stack.hitEntity(target, player);
                if(!player.capabilities.isCreativeMode && !isProjectile) {
                    tool.reduceDurabilityOnHit(stack, player, damage);
                }

                player.addStat(StatList.DAMAGE_DEALT, Math.round(damageDealt * 10f));
                player.addExhaustion(0.3f);

                if(player.worldObj instanceof WorldServer && damageDealt > 2f) {
                    int k = (int)(damageDealt * 0.5);
                    ((WorldServer)player.worldObj).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, targetEntity.posX, targetEntity.posY + (double)(targetEntity.height * 0.5F), targetEntity.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D);
                }

                if(!isProjectile) {
                    player.resetCooldown();
                }
            }
            else if(!isProjectile) {
                tool.reduceDurabilityOnHit(stack, null, damage);
            }
        }

        return true;
    }

    public static float calcCutoffDamage(float damage, float cutoff) {
        float p = 1f;
        float d = damage;
        damage = 0f;
        while(d > cutoff) {
            damage += p * cutoff;

            if(p > 0.001f) {
                p *= 0.9f;
            }
            else {
                damage += p * cutoff * ((d/cutoff) - 1f);
                return damage;
            }
            d -= cutoff;
        }

        damage += p*d;

        return damage;
    }


    @SuppressWarnings("deprecation")
    public static boolean canHarvest(ItemStack stack, IBlockState state) {
        Block block = state.getBlock();

        // doesn't require a tool
        if(block.getMaterial(state).isToolNotRequired()) {
            return true;
        }

        String type = block.getHarvestTool(state);
        int level = block.getHarvestLevel(state);

        return stack.getItem().getHarvestLevel(stack, type) >= level;
    }


}
