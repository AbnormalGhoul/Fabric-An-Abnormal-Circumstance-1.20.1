package net.abnormal.anabnormalcircumstance.brewing;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.potion.PotionUtil;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomBrewingRecipes {

    private static final Map<RecipeKey, Identifier> RECIPES = new HashMap<>();
    private static boolean registered = false;

    public static void registerAll() {
        if (registered) return;
        registered = true;

        // Water to Water Spark
        addPotionRecipe(Potions.WATER, Items.ENDER_PEARL, "water_spark");
        addPotionRecipe(Potions.AWKWARD, Items.ENDER_PEARL, "awkward_spark");

        // Awkward Spark paths
        addItemRecipe("awkward_spark", Items.SUGAR, "spark_of_swiftness");
        addPotionRecipe(Potions.SWIFTNESS, Items.ENDER_PEARL, "spark_of_swiftness");
        addPotionRecipe(Potions.LONG_SWIFTNESS, Items.ENDER_PEARL, "spark_of_long_swiftness");
        addPotionRecipe(Potions.STRONG_SWIFTNESS, Items.ENDER_PEARL, "spark_of_strong_swiftness");
        addItemRecipe("spark_of_swiftness", Items.GLOWSTONE_DUST, "spark_of_strong_swiftness");
        addItemRecipe("spark_of_swiftness", Items.REDSTONE, "spark_of_long_swiftness");

        // Strength
        addItemRecipe("awkward_spark", Items.BLAZE_POWDER, "spark_of_strength");
        addPotionRecipe(Potions.STRENGTH, Items.ENDER_PEARL, "spark_of_strength");
        addPotionRecipe(Potions.LONG_STRENGTH, Items.ENDER_PEARL, "spark_of_long_strength");
        addPotionRecipe(Potions.STRONG_STRENGTH, Items.ENDER_PEARL, "spark_of_strong_strength");
        addItemRecipe("spark_of_strength", Items.GLOWSTONE_DUST, "spark_of_strong_strength");
        addItemRecipe("spark_of_strength", Items.REDSTONE, "spark_of_long_strength");

        // Regeneration
        addItemRecipe("awkward_spark", Items.GHAST_TEAR, "spark_of_regeneration");
        addPotionRecipe(Potions.REGENERATION, Items.ENDER_PEARL, "spark_of_regeneration");
        addPotionRecipe(Potions.LONG_REGENERATION, Items.ENDER_PEARL, "spark_of_long_regeneration");
        addPotionRecipe(Potions.STRONG_REGENERATION, Items.ENDER_PEARL, "spark_of_strong_regeneration");
        addItemRecipe("spark_of_regeneration", Items.GLOWSTONE_DUST, "spark_of_strong_regeneration");
        addItemRecipe("spark_of_regeneration", Items.REDSTONE, "spark_of_long_regeneration");

        // Slow Falling
        addItemRecipe("awkward_spark", Items.PHANTOM_MEMBRANE, "spark_of_slow_falling");
        addPotionRecipe(Potions.SLOW_FALLING, Items.ENDER_PEARL, "spark_of_slow_falling");
        addPotionRecipe(Potions.LONG_SLOW_FALLING, Items.ENDER_PEARL, "spark_of_long_slow_falling");
        addItemRecipe("spark_of_slow_falling", Items.REDSTONE, "spark_of_long_slow_falling");

        // Jump Boost
        addItemRecipe("awkward_spark", Items.RABBIT_FOOT, "spark_of_jump_boost");
        addPotionRecipe(Potions.LEAPING, Items.ENDER_PEARL, "spark_of_jump_boost");
        addPotionRecipe(Potions.LONG_LEAPING, Items.ENDER_PEARL, "spark_of_long_jump_boost");
        addPotionRecipe(Potions.STRONG_LEAPING, Items.ENDER_PEARL, "spark_of_strong_jump_boost");
        addItemRecipe("spark_of_jump_boost", Items.GLOWSTONE_DUST, "spark_of_strong_jump_boost");
        addItemRecipe("spark_of_jump_boost", Items.REDSTONE, "spark_of_long_jump_boost");

        // Night Vision → Invisibility
        addItemRecipe("awkward_spark", Items.GOLDEN_CARROT, "night_vision_potion");
        addPotionRecipe(Potions.NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, "invisibility_potion");
        addPotionRecipe(Potions.INVISIBILITY, Items.ENDER_PEARL, "spark_of_invisibility");
        addPotionRecipe(Potions.LONG_INVISIBILITY, Items.ENDER_PEARL, "spark_of_long_invisibility");
        addItemRecipe("spark_of_invisibility", Items.REDSTONE, "spark_of_long_invisibility");

        // Night Vision Sparks
        addItemRecipe("awkward_spark", Items.GOLDEN_CARROT, "spark_of_night_vision");
        addPotionRecipe(Potions.NIGHT_VISION, Items.ENDER_PEARL, "spark_of_night_vision");
        addPotionRecipe(Potions.LONG_NIGHT_VISION, Items.ENDER_PEARL, "spark_of_long_night_vision");
        addItemRecipe("spark_of_night_vision", Items.REDSTONE, "spark_of_long_night_vision");

        // Water Breathing Sparks
        addItemRecipe("awkward_spark", Items.PUFFERFISH, "spark_of_water_breathing");
        addPotionRecipe(Potions.WATER_BREATHING, Items.ENDER_PEARL, "spark_of_water_breathing");
        addPotionRecipe(Potions.LONG_WATER_BREATHING, Items.ENDER_PEARL, "spark_of_long_water_breathing");
        addItemRecipe("spark_of_water_breathing", Items.REDSTONE, "spark_of_long_water_breathing");
    }

    // Recipe registration helpers

    private static void addPotionRecipe(Potion inputPotion, Item ingredient, String resultId) {
        RECIPES.put(new RecipeKey(inputPotion, ingredient),
                new Identifier("anabnormalcircumstance", resultId));
    }

    private static void addItemRecipe(String inputItemId, Item ingredient, String resultId) {
        RECIPES.put(new RecipeKey(new Identifier("anabnormalcircumstance", inputItemId), ingredient),
                new Identifier("anabnormalcircumstance", resultId));
    }

    // Runtime lookup

    public static Identifier getResult(ItemStack input, ItemStack ingredient) {
        if (input == null || ingredient == null) return null;

        // Potion recipes
        var potionKey = new RecipeKey(PotionUtil.getPotion(input), ingredient.getItem());
        if (RECIPES.containsKey(potionKey)) {
            return RECIPES.get(potionKey);
        }

        // Item recipes
        var inputId = Registries.ITEM.getId(input.getItem());
        var itemKey = new RecipeKey(inputId, ingredient.getItem());
        if (RECIPES.containsKey(itemKey)) {
            return RECIPES.get(itemKey);
        }

        return null;
    }

    public static boolean usesIngredient(Item ingredient) {
        if (ingredient == null) return false;
        for (RecipeKey key : RECIPES.keySet()) {
            if (Objects.equals(key.ingredient(), ingredient)) return true;
        }
        return false;
    }

    // Inner key type
    private record RecipeKey(Object input, Item ingredient) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RecipeKey other)) return false;
            return Objects.equals(input, other.input) && Objects.equals(ingredient, other.ingredient);
        }

        @Override
        public int hashCode() {
            return Objects.hash(input, ingredient);
        }

        @Override
        public @NotNull String toString() {
            return "RecipeKey{" + input + " + " + Registries.ITEM.getId(ingredient) + "}";
        }
    }
}
