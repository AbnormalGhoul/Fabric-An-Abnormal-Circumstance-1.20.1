package net.abnormal.anabnormalcircumstance.util;

import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellRegistry;
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

    // For Bows
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

    // For Magic Scrolls
    public static void registerSpellScrollPredicate() {
        ModelPredicateProviderRegistry.register(ModItems.SPELL_SCROLL,
                new Identifier("anabnormalcircumstance", "spell_element"),
                        (stack, world, entity, seed) -> {
                    if (!stack.hasNbt() || !stack.getNbt().contains("spell_id")) {
                        return 0.0F;
                    }

                    String rawId = stack.getNbt().getString("spell_id");
                    Identifier id = Identifier.tryParse(rawId);
                    Spell spell = SpellRegistry.get(id);
                    if (spell == null) {
                        return 0.0F;
                    }

                    return switch (spell.getElement()) {
                        case HYDROMANCY -> 0.2F;
                        case PYROMANCY -> 0.4F;
                        case GEOMANCY  -> 0.6F;
                        case AEROMANCY -> 0.8F;
                        default -> 0.0F;
                    };
                });
     }
}