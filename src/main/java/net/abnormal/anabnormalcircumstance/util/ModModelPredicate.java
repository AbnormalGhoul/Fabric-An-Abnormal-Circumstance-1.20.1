package net.abnormal.anabnormalcircumstance.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public final class ModModelPredicate {
    private ModModelPredicate() {}

    public static void registerBow(Item item) {
        ModelPredicateProviderRegistry.register(item, new Identifier("pulling"),
                (ItemStack stack, ClientWorld world, LivingEntity entity, int seed) ->
                        (entity != null && entity.isUsingItem() && entity.getActiveItem() == stack) ? 1.0F : 0.0F
        );

        ModelPredicateProviderRegistry.register(item, new Identifier("pull"),
                (ItemStack stack, ClientWorld world, LivingEntity entity, int seed) -> {
                    if (entity == null) return 0.0F;
                    if (entity.isUsingItem() && entity.getActiveItem() == stack) {
                        int useTime = stack.getMaxUseTime() - entity.getItemUseTimeLeft();
                        return Math.min(useTime / 20.0F, 1.0F);
                    }
                    return 0.0F;
                }
        );
    }
}