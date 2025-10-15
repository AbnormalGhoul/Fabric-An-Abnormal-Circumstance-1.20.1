package net.abnormal.anabnormalcircumstance.mixin;

import net.abnormal.anabnormalcircumstance.brewing.CustomBrewingRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingRecipeRegistry.class)
public abstract class BrewingRecipeRegistryMixin {

    // Allow custom ingredients
    @Inject(method = "isValidIngredient", at = @At("HEAD"), cancellable = true)
    private static void anabnormalcircumstance$isValidIngredient(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        CustomBrewingRecipes.registerAll(); // make sure recipes exist

        Item item = stack.getItem();
        if (CustomBrewingRecipes.usesIngredient(item)) {
            cir.setReturnValue(true);
        }
    }

    // Check for recipe existence
    @Inject(method = "hasRecipe", at = @At("HEAD"), cancellable = true)
    private static void anabnormalcircumstance$hasRecipe(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<Boolean> cir) {
        if (CustomBrewingRecipes.getResult(input, ingredient) != null) {
            cir.setReturnValue(true);
        }
    }

    // Handle actual brewing output
    @Inject(method = "craft", at = @At("HEAD"), cancellable = true)
    private static void anabnormalcircumstance$craft(ItemStack ingredient, ItemStack input, CallbackInfoReturnable<ItemStack> cir) {
        var resultId = CustomBrewingRecipes.getResult(input, ingredient);
        if (resultId != null) {
            var item = Registries.ITEM.get(resultId);
            if (item != Items.AIR) {
                cir.setReturnValue(new ItemStack(item));
            } else {
                System.err.println("[AnAbnormalCircumstance] Unknown result item: " + resultId);
            }
        }
    }
}
