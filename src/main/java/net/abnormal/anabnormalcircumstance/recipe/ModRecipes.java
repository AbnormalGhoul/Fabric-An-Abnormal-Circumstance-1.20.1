package net.abnormal.anabnormalcircumstance.recipe;

import net.abnormal.anabnormalcircumstance.item.ModItems;
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
        ALTAR_RECIPES.add(createHydoBlade());
        ALTAR_RECIPES.add(createPyroBlade());
        ALTAR_RECIPES.add(createGeoBlade());
        ALTAR_RECIPES.add(createAeroBlade());
    }

    public static AltarRecipe createHydoBlade() {
        return new AltarRecipe.Builder(new Identifier("anabnormalcircumstance", "hydro_blade"))
            .addInput(ModItems.HYDRO_CATALYST, 1)
            .addInput(ModItems.MANA_CORE, 2)
            .addInput(Items.DIAMOND_BLOCK, 2)
            .addInput(Items.DIAMOND_SWORD, 1)
            .addInput(ModItems.BROODMOTHER_EGG, 2)
            .setOutput(new ItemStack(ModItems.HYDRO_BLADE))
            .build();
    }

    public static AltarRecipe createPyroBlade() {
        return new AltarRecipe.Builder(new Identifier("anabnormalcircumstance", "pyro_blade"))
                .addInput(ModItems.PYRO_CATALYST, 1)
                .addInput(ModItems.MANA_CORE, 2)
                .addInput(Items.DIAMOND_BLOCK, 2)
                .addInput(Items.DIAMOND_SWORD, 1)
                .addInput(ModItems.ORC_CHAMPION_FANG, 2)
                .setOutput(new ItemStack(ModItems.PYRO_BLADE))
                .build();
    }
    public static AltarRecipe createGeoBlade() {
        return new AltarRecipe.Builder(new Identifier("anabnormalcircumstance", "geo_blade"))
                .addInput(ModItems.GEO_CATALYST, 1)
                .addInput(ModItems.MANA_CORE, 2)
                .addInput(Items.DIAMOND_BLOCK, 2)
                .addInput(Items.DIAMOND_SWORD, 1)
                .addInput(ModItems.BROODMOTHER_EGG, 1)
                .addInput(ModItems.ORC_CHAMPION_FANG, 1)
                .setOutput(new ItemStack(ModItems.GEO_BLADE))
                .build();
    }
    public static AltarRecipe createAeroBlade() {
        return new AltarRecipe.Builder(new Identifier("anabnormalcircumstance", "aero_blade"))
                .addInput(ModItems.AERO_CATALYST, 1)
                .addInput(ModItems.MANA_CORE, 2)
                .addInput(Items.DIAMOND_BLOCK, 2)
                .addInput(Items.DIAMOND_SWORD, 1)
                .addInput(ModItems.BROODMOTHER_EGG, 1)
                .addInput(ModItems.ORC_CHAMPION_FANG, 1)
                .setOutput(new ItemStack(ModItems.AERO_BLADE))
                .build();
    }

}
