package org.undergroundbunker.harshworld.library.item;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.undergroundbunker.harshworld.library.utils.LocaleUtils;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class ItemMetaDynamic extends ItemTooltip {

    private static int MAX = (2 << 16) -1;

    protected boolean[] availabilityMask;
    protected TIntObjectHashMap<String> names;

    public ItemMetaDynamic() {
        availabilityMask = new boolean[1];
        names = new TIntObjectHashMap<String>();

        this.setHasSubtypes(true);
    }

    public ItemStack addMeta(int meta, String name) {

        if (meta > MAX) {
            throw new IllegalArgumentException(
                    String.format("Metadata is %s too high. Highest supported is %d. Meta was %d", name, MAX, meta));
        }
        else if (isValid(meta)) {
            throw new IllegalArgumentException(
                    String.format("Metadata for %s is already in use.  Meta %d is %s", name, meta, names.get(meta)));
        }

        while (meta >= availabilityMask.length) {
            availabilityMask = Arrays.copyOf(availabilityMask, availabilityMask.length * 2);
        }

        setValid(meta);
        names.put(meta, name);

        return new ItemStack(this, 1, meta);
    }

    @Nonnull
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = stack.getMetadata();
        if (isValid(meta)) {
            return super.getUnlocalizedName(stack) + "." + LocaleUtils.makeLocaleString(names.get(meta));
        }
        else {
            return super.getUnlocalizedName(stack);
        }
    }

    @Override
    public void getSubItems(@Nonnull Item item, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i <= availabilityMask.length; i++) {
            if (isValid(i)) {
                subItems.add(new ItemStack(item, 1, i));
            }
        }
    }

    @Override
    public int getMetadata(int damage) {
        int meta = super.getMetadata(damage);
        return isValid(meta) ? meta : 0;
    }

    protected void setValid(int meta) {
        availabilityMask[meta] = true;
    }

    protected boolean isValid(int meta) {
        return !(meta > MAX || meta >= availabilityMask.length) && availabilityMask[meta];
    }
}
