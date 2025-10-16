package net.abnormal.anabnormalcircumstance.recipe;

import java.util.Map;
import java.util.HashMap;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class AltarRecipe implements Recipe<Inventory> {
    private final Identifier id;
    private final ItemStack output;
    private final Map<Item, Integer> inputCounts; // Item -> required count

    public AltarRecipe(Identifier id, Map<Item, Integer> inputCounts, ItemStack output) {
        this.id = id;
        this.inputCounts = inputCounts;
        this.output = output;
    }

    @Override
    public boolean matches(Inventory inv, World world) {
        Map<Item, Integer> found = new HashMap<>();
        int totalFound = 0;
        for (int i = 0; i < inv.size(); i++) {
            if (i == 4) continue; // skip center slot
            ItemStack stack = inv.getStack(i);
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                found.put(item, found.getOrDefault(item, 0) + stack.getCount());
                totalFound += stack.getCount();
            }
        }
        int requiredTotal = inputCounts.values().stream().mapToInt(Integer::intValue).sum();
        if (totalFound != requiredTotal) return false;
        // Check required items and counts
        for (Map.Entry<Item, Integer> entry : inputCounts.entrySet()) {
            if (found.getOrDefault(entry.getKey(), 0) != entry.getValue()) return false;
        }
        // No extra items
        if (found.size() != inputCounts.size()) return false;
        return true;
    }

    @Override
    public ItemStack craft(Inventory inv, DynamicRegistryManager registryManager) {
        return output.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    public ItemStack getOutput() {
        return output;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output.copy();
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ALTAR_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ALTAR_RECIPE_TYPE;
    }

    // Builder for easy recipe creation
    public static class Builder {
        private final Identifier id;
        private final Map<Item, Integer> inputCounts = new HashMap<>();
        private ItemStack output;

        public Builder(Identifier id) {
            this.id = id;
        }

        public Builder addInput(Item item, int count) {
            inputCounts.put(item, count);
            return this;
        }

        public Builder setOutput(ItemStack output) {
            this.output = output;
            return this;
        }

        public AltarRecipe build() {
            return new AltarRecipe(id, inputCounts, output);
        }
    }
}