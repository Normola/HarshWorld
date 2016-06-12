package org.undergroundbunker.harshworld.library.utils;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class RecipeMatchRegistry {

    protected final PriorityQueue<RecipeMatch> items = new PriorityQueue<RecipeMatch>(1, RecipeComparator.instance);

    public RecipeMatch.Match matches(Collection<ItemStack> stacks) {
        return matches(stacks.toArray(new ItemStack[stacks.size()]));
    }

    public RecipeMatch.Match matches(ItemStack... stacks) {
        for(RecipeMatch recipe : items) {
            RecipeMatch.Match match = recipe.matches(stacks);
            if(match != null) {
                return match;
            }
        }

        return null;
    }

    public RecipeMatch.Match matches(ItemStack[] stacks, int minAmount) {

        stacks = copyItemStackArray(stacks);

        List<RecipeMatch.Match> matches = Lists.newLinkedList();

        RecipeMatch.Match match;
        int sum = 0;

        while(sum < minAmount && (match = matches(stacks)) != null) {
            matches.add(match);
            RecipeMatch.removeMatch(stacks, match);

            sum += match.amount;
        }

        if(sum < minAmount) {
            return null;
        }

        List<ItemStack> foundStacks = Lists.newLinkedList();

        for(RecipeMatch.Match m : matches) {
            foundStacks.addAll(m.stacks);
        }

        return new RecipeMatch.Match(foundStacks, sum);
    }

    public RecipeMatch.Match matchesRecursively(ItemStack[] stacks) {
        stacks = copyItemStackArray(stacks);

        List<RecipeMatch.Match> matches = Lists.newLinkedList();

        RecipeMatch.Match match;
        int sum = 0;

        while((match = matches(stacks)) != null) {
            matches.add(match);
            RecipeMatch.removeMatch(stacks, match);

            sum += match.amount;
        }

        List<ItemStack> foundStacks = Lists.newLinkedList();

        for(RecipeMatch.Match m : matches) {
            foundStacks.addAll(m.stacks);
        }

        return new RecipeMatch.Match(foundStacks, sum);
    }

    public void addItem(String oredictItem, int amountNeeded, int amountMatched) {
        items.add(new RecipeMatch.Oredict(oredictItem, amountNeeded, amountMatched));
    }

    public void addItem(String oredictItem) {

        addItem(oredictItem, 1, 1);
    }

    public void addItem(Block block, int amountMatched) {
        items.add(new RecipeMatch.Item(new ItemStack(block), 1, amountMatched));
    }

    public void addItem(Item item, int amountNeeded, int amountMatched) {
        items.add(new RecipeMatch.Item(new ItemStack(item), amountNeeded, amountMatched));
    }

    public void addItem(ItemStack item, int amountNeeded, int amountMatched) {
        items.add(new RecipeMatch.Item(item, amountNeeded, amountMatched));
    }

    public void addItem(Item item) {
        addItem(item, 1, 1);
    }

    public void addRecipeMatch(RecipeMatch match) {

        items.add(match);
    }

    public static ItemStack[] copyItemStackArray(ItemStack[] in) {

        ItemStack[] stacksCopy = new ItemStack[in.length];

        for(int i = 0; i < in.length; i++) {
            if(in[i] != null) {
                stacksCopy[i] = in[i].copy();
            }
        }

        return stacksCopy;
    }

    private static class RecipeComparator implements Comparator<RecipeMatch> {

        public static RecipeComparator instance = new RecipeComparator();

        private RecipeComparator() {
        }

        @Override
        public int compare(RecipeMatch o1, RecipeMatch o2) {
            return o2.amountMatched - o1.amountMatched;
        }
    }

}
