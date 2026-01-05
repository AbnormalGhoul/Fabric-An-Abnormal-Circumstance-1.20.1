package net.abnormal.anabnormalcircumstance.recipe;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipeTypes {

    public static final RecipeType<SpellScrollRecipe> SPELL_SCROLL_RECIPE =
            Registry.register(
                    Registries.RECIPE_TYPE,
                    new Identifier("anabnormalcircumstance", "spell_scroll"),
                    new RecipeType<SpellScrollRecipe>() {
                        public String toString() { return "spell_scroll"; }
                    }
            );

    public static final RecipeSerializer<SpellScrollRecipe> SPELL_SCROLL_SERIALIZER =
            Registry.register(
                    Registries.RECIPE_SERIALIZER,
                    SpellScrollRecipeSerializer.ID,
                    SpellScrollRecipeSerializer.INSTANCE
            );

    public static void register() {
    }
}

