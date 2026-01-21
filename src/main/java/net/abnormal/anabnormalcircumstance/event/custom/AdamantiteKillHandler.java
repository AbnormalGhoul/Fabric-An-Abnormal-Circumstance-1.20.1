package net.abnormal.anabnormalcircumstance.event.custom;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public final class AdamantiteKillHandler {

    private AdamantiteKillHandler() {}

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {

            Entity attacker = source.getAttacker();
            if (!(attacker instanceof ServerPlayerEntity player)) return;

            // Must have Adamantite Power active
            if (!player.hasStatusEffect(ModEffects.ADAMANTITE_POWER)) return;

            // Only trigger on players or hostile mobs
            if (!(entity instanceof ServerPlayerEntity) &&
                    !(entity instanceof HostileEntity)) {
                return;
            }

            triggerEmpower(player);
        });
    }

    private static void triggerEmpower(ServerPlayerEntity player) {

        // Play a powerful sound
        player.getWorld().playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ENTITY_WITHER_HURT,
                SoundCategory.PLAYERS,
                1.3f,
                1.66f
        );

        // Apply buffs (7.5s = 150 ticks)
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.STRENGTH,
                150,
                2, // Strength III
                false,
                true,
                true
        ));

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.REGENERATION,
                150,
                2, // Regen III
                false,
                true,
                true
        ));

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.ABSORPTION,
                150,
                1, // Absorption II
                false,
                true,
                true
        ));

        // Consume the proc
        player.removeStatusEffect(ModEffects.ADAMANTITE_POWER);
    }
}
