package net.abnormal.anabnormalcircumstance.recipe;

import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

import static net.abnormal.anabnormalcircumstance.recipe.AltarRecipe.spellScroll;

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
        ALTAR_RECIPES.add(createBerserkersVial());
        ALTAR_RECIPES.add(createPrismaticStaff());
        ALTAR_RECIPES.add(createControlWeatherScroll());
        ALTAR_RECIPES.add(createStormlordsWillScroll());
        ALTAR_RECIPES.add(createPhoenixRebirthScroll());
        ALTAR_RECIPES.add(createImmovableObject());
    }

    public static AltarRecipe createHydoBlade() {
        return new AltarRecipe.Builder(new Identifier("anabnormalcircumstance", "hydro_blade"))
            .addInput(ModItems.HYDRO_CATALYST, 1)
            .addInput(ModItems.MANA_CORE, 2)
            .addInput(Items.DIAMOND_BLOCK, 8)
            .addInput(ModItems.ARCANE_BLADE, 1)
            .addInput(ModItems.BROODMOTHER_EGG, 2)
            .addInput(ModItems.ORC_CHAMPION_FANG, 2)
            .addInput(Items.GOLD_BLOCK, 16)
            .addInput(Items.HEART_OF_THE_SEA, 1)
            .setOutput(new ItemStack(ModItems.HYDRO_BLADE))
            .build();
    }

    public static AltarRecipe createPyroBlade() {
        return new AltarRecipe.Builder(new Identifier("anabnormalcircumstance", "pyro_blade"))
                .addInput(ModItems.PYRO_CATALYST, 1)
                .addInput(ModItems.MANA_CORE, 2)
                .addInput(Items.DIAMOND_BLOCK, 8)
                .addInput(ModItems.ARCANE_BLADE, 1)
                .addInput(ModItems.BROODMOTHER_EGG, 2)
                .addInput(ModItems.ORC_CHAMPION_FANG, 2)
                .addInput(Items.GOLD_BLOCK, 16)
                .addInput(Items.BLAZE_ROD, 64)
                .setOutput(new ItemStack(ModItems.PYRO_BLADE))
                .build();
    }

    public static AltarRecipe createGeoBlade() {
        return new AltarRecipe.Builder(new Identifier("anabnormalcircumstance", "geo_blade"))
                .addInput(ModItems.GEO_CATALYST, 1)
                .addInput(ModItems.MANA_CORE, 2)
                .addInput(Items.DIAMOND_BLOCK, 8)
                .addInput(ModItems.ARCANE_BLADE, 1)
                .addInput(ModItems.BROODMOTHER_EGG, 2)
                .addInput(ModItems.ORC_CHAMPION_FANG, 2)
                .addInput(Items.GOLD_BLOCK, 16)
                .addInput(Items.ECHO_SHARD, 16)
                .setOutput(new ItemStack(ModItems.GEO_BLADE))
                .build();
    }

    public static AltarRecipe createAeroBlade() {
        return new AltarRecipe.Builder(new Identifier("anabnormalcircumstance", "aero_blade"))
                .addInput(ModItems.AERO_CATALYST, 1)
                .addInput(ModItems.MANA_CORE, 2)
                .addInput(Items.DIAMOND_BLOCK, 8)
                .addInput(ModItems.ARCANE_BLADE, 1)
                .addInput(ModItems.BROODMOTHER_EGG, 2)
                .addInput(ModItems.ORC_CHAMPION_FANG, 2)
                .addInput(Items.GOLD_BLOCK, 16)
                .addInput(Items.PHANTOM_MEMBRANE, 32)
                .setOutput(new ItemStack(ModItems.AERO_BLADE))
                .build();
    }

    public static AltarRecipe createBerserkersVial() {
        return new AltarRecipe.Builder(new Identifier("anabnormalcircumstance", "berserkers_vial"))
                .addInput(ModItems.MANA_CLUSTER, 4)
                .addInput(Items.DIAMOND_BLOCK, 8)
                .addInput(Items.GOLD_BLOCK, 16)
                .addInput(Items.CRYING_OBSIDIAN, 16)
                .addInput(Items.EMERALD_BLOCK, 32)
                .addInput(Items.NETHER_STAR, 4)
                .addInput(ModItems.ORC_HIDE, 32)
                .addInput(ModItems.ORC_CHAMPION_FANG, 1)
                .setOutput(new ItemStack(ModItems.BERSERKERS_VIAL))
                .build();
    }

    public static AltarRecipe createPrismaticStaff() {
        return new AltarRecipe.Builder(new Identifier("anabnormalcircumstance", "prismatic_staff"))
                .addInput(ModItems.MANA_CLUSTER, 4)
                .addInput(Items.DIAMOND_BLOCK, 8)
                .addInput(Items.GOLD_BLOCK, 16)
                .addInput(Items.CRYING_OBSIDIAN, 16)
                .addInput(Items.EMERALD_BLOCK, 32)
                .addInput(Items.NETHER_STAR, 4)
                .addInput(ModItems.ARACHNID_SILK, 32)
                .addInput(ModItems.BROODMOTHER_EGG, 1)
                .setOutput(new ItemStack(ModItems.PRISMATIC_STAFF))
                .build();
    }

    public static AltarRecipe createControlWeatherScroll() {
        return new AltarRecipe.Builder(
                new Identifier("anabnormalcircumstance", "hydro_control_weather"))
                .addInput(ModItems.SPELL_SCROLL, 1)
                .addInput(Items.CRYING_OBSIDIAN, 16)
                .addInput(Items.NETHER_STAR, 8)
                .addInput(ModItems.MANA_CORE, 2)
                .addInput(ModItems.BROODMOTHER_EGG, 1)
                .addInput(ModItems.ORC_CHAMPION_FANG, 1)
                .addInput(Items.HEART_OF_THE_SEA, 1)
                .addInput(ModItems.HYDRO_CATALYST, 1)
                .setOutput(spellScroll("anabnormalcircumstance:hydro_control_weather"))
                .build();
    }
    public static AltarRecipe createPhoenixRebirthScroll() {
        return new AltarRecipe.Builder(
                new Identifier("anabnormalcircumstance", "pyro_phoenix_rebirth"))
                .addInput(ModItems.SPELL_SCROLL, 1)
                .addInput(Items.CRYING_OBSIDIAN, 16)
                .addInput(Items.NETHER_STAR, 8)
                .addInput(ModItems.MANA_CORE, 2)
                .addInput(ModItems.BROODMOTHER_EGG, 1)
                .addInput(ModItems.ORC_CHAMPION_FANG, 1)
                .addInput(Items.FIRE_CHARGE, 64)
                .addInput(ModItems.PYRO_CATALYST, 1)
                .setOutput(spellScroll("anabnormalcircumstance:pyro_phoenix_rebirth"))
                .build();
    }
    public static AltarRecipe createImmovableObject() {
        return new AltarRecipe.Builder(
                new Identifier("anabnormalcircumstance", "geo_immovable_object"))
                .addInput(ModItems.SPELL_SCROLL, 1)
                .addInput(Items.CRYING_OBSIDIAN, 16)
                .addInput(Items.NETHER_STAR, 8)
                .addInput(ModItems.MANA_CORE, 2)
                .addInput(ModItems.BROODMOTHER_EGG, 1)
                .addInput(ModItems.ORC_CHAMPION_FANG, 1)
                .addInput(Items.ECHO_SHARD, 32)
                .addInput(ModItems.GEO_CATALYST, 1)
                .setOutput(spellScroll("anabnormalcircumstance:geo_immovable_object"))
                .build();
    }
    public static AltarRecipe createStormlordsWillScroll() {
        return new AltarRecipe.Builder(
                new Identifier("anabnormalcircumstance", "aero_stormlords_will"))
                .addInput(ModItems.SPELL_SCROLL, 1)
                .addInput(Items.CRYING_OBSIDIAN, 16)
                .addInput(Items.NETHER_STAR, 8)
                .addInput(ModItems.MANA_CORE, 2)
                .addInput(ModItems.BROODMOTHER_EGG, 1)
                .addInput(ModItems.ORC_CHAMPION_FANG, 1)
                .addInput(Items.RABBIT_FOOT, 12)
                .addInput(ModItems.AERO_CATALYST, 1)
                .setOutput(spellScroll("anabnormalcircumstance:aero_stormlords_will"))
                .build();
    }

}
