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

    public static void registerSpellScrollPredicate() {
        ModelPredicateProviderRegistry.register(ModItems.SPELL_SCROLL,
                    new Identifier("anabnormalcircumstance", "spell_element"), (stack, world, entity, seed) -> {
                    // Default: no spell bound (blank scroll)
                    if (!stack.hasNbt() || !stack.getNbt().contains("spell_id")) {
                        return 0.0F;
                    }

                    Identifier id = Identifier.tryParse(stack.getNbt().getString("spell_id"));
                    Spell spell = SpellRegistry.get(id);
                    if (spell == null) return 0.0F;

                    // Map element types to float values used in model overrides
                    return switch (spell.getElement().toString().toLowerCase()) {
                        case "hydromancy" -> 1.0F;
                        case "pyromancy" -> 2.0F;
                        case "geomancy" -> 3.0F;
                        case "aeromancy" -> 4.0F;
                        default -> 0.0F;
                    };
                });
    }
}