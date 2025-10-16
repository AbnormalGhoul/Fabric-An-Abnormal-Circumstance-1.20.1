package net.abnormal.anabnormalcircumstance.recipe;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

public class ModRecipes {
    public static RecipeType<AltarRecipe> ALTAR_RECIPE_TYPE;
    public static RecipeSerializer<AltarRecipe> ALTAR_RECIPE_SERIALIZER;

    // Store altar recipes here
    public static final List<AltarRecipe> ALTAR_RECIPES = new ArrayList<>();

    public static void registerRecipes() {
        ALTAR_RECIPES.clear();
        ALTAR_RECIPES.add(createNetherStarRecipe());
        ALTAR_RECIPES.add(createTotemRecipe());
    }

    // Example: Register recipes in your mod initializer
    public static AltarRecipe createNetherStarRecipe() {
        return new AltarRecipe.Builder(new Identifier("anabnormalcircumstance", "netherstar"))
            .addInput(Items.DIAMOND, 2)
            .addInput(Items.GOLD_INGOT, 2)
            .addInput(Items.IRON_INGOT, 2)
            .addInput(Items.REDSTONE, 2)
            .setOutput(new ItemStack(Items.NETHER_STAR))
            .build();
    }

    public static AltarRecipe createTotemRecipe() {
        return new AltarRecipe.Builder(new Identifier("anabnormalcircumstance", "totem_of_undying"))
            .addInput(Items.DIAMOND, 4)
            .addInput(Items.GOLD_INGOT, 4)
            .setOutput(new ItemStack(Items.TOTEM_OF_UNDYING))
            .build();
    }

}
