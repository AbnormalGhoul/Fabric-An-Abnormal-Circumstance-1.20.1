package net.abnormal.anabnormalcircumstance.util;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public final class WitchDropModifier {

    private WitchDropModifier() {
        // Utility class, prevent instantiation
    }

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register(WitchDropModifier::onEntityDrop);
    }

    private static void onEntityDrop(LivingEntity entity, DamageSource source) {
        if (!(entity.getWorld() instanceof ServerWorld world)) return;
        if (entity.getType() != EntityType.WITCH) return;

        List<ItemEntity> items = world.getEntitiesByClass(
                ItemEntity.class,
                entity.getBoundingBox().expand(2.0),
                item -> item.getStack().isOf(Items.POTION)
        );

        for (ItemEntity item : items) {
            item.discard();
        }
    }
}