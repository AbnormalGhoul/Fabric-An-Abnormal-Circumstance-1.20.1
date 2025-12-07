package net.abnormal.anabnormalcircumstance.recipe;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public final class SpellScrollRecipe implements CraftingRecipe {

    private final ShapedRecipe inner;
    private final ItemStack overrideOutput; // may be null

    public SpellScrollRecipe(ShapedRecipe inner) {
        this(inner, null);
    }

    public SpellScrollRecipe(ShapedRecipe inner, ItemStack overrideOutput) {
        this.inner = inner;
        this.overrideOutput = overrideOutput;
    }

    @Override
    public boolean matches(RecipeInputInventory inv, World world) {
        return inner.matches(inv, world);
    }

    @Override
    public ItemStack craft(RecipeInputInventory inv, DynamicRegistryManager registryManager) {
        // Return a fresh copy of the effective output
        return getOutput(registryManager).copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return inner.fits(width, height);
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        // If an override was supplied by the serializer, return it; otherwise delegate to inner.
        return overrideOutput != null ? overrideOutput : inner.getOutput(registryManager);
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        // Delegate ingredient list to the inner ShapedRecipe so REI and other consumers can read them.
        return inner.getIngredients();
    }

    @Override
    public Identifier getId() {
        return inner.getId();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SpellScrollRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    @Override
    public CraftingRecipeCategory getCategory() {
        return CraftingRecipeCategory.MISC;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return inner.isIgnoredInRecipeBook();
    }

    public ShapedRecipe getInner() {
        return inner;
    }
}