package net.abnormal.anabnormalcircumstance.util;

import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public class FirstLeafBondUtil {

    public static final double LINK_RADIUS = 15.0;

    public static void handleBondedRegen(ServerWorld world, PlayerEntity player, boolean isHoldingSword) {
        // Base regen amplifier
        int regenLevel = 0;

        // Check for nearby player with the complementary item
        List<PlayerEntity> nearby = world.getEntitiesByClass(PlayerEntity.class,
                player.getBoundingBox().expand(LINK_RADIUS),
                other -> other != player && other.isAlive());

        for (PlayerEntity other : nearby) {
            boolean otherHasSword = other.getMainHandStack().isOf(ModItems.LAST_LEAF)
                    || other.getOffHandStack().isOf(ModItems.LAST_LEAF);
            boolean otherHasBow = other.getMainHandStack().isOf(ModItems.FIRST_LEAF)
                    || other.getOffHandStack().isOf(ModItems.FIRST_LEAF);

            // If this player is holding sword and the other holds bow (or vice versa)
            if ((isHoldingSword && otherHasBow) || (!isHoldingSword && otherHasSword)) {
                regenLevel = 2; // Regen III
                break;
            }
        }

        StatusEffectInstance current = player.getStatusEffect(StatusEffects.REGENERATION);

        // Only refresh when missing or almost expired
        if (current == null || current.getAmplifier() != regenLevel || current.getDuration() <= 20) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.REGENERATION,
                    105,
                    regenLevel,
                    true,
                    false,
                    true
            ));
        }
    }
}
