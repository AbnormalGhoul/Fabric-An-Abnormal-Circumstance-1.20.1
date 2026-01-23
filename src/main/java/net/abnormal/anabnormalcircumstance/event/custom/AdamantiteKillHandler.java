package net.abnormal.anabnormalcircumstance.event.custom;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public final class AdamantiteKillHandler {

    private AdamantiteKillHandler() {}

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {

            if (!(source.getAttacker() instanceof ServerPlayerEntity player)) {
                return;
            }

            Entity src = source.getSource();
            if (src instanceof ProjectileEntity) {
                return;
            }

            if (!player.hasStatusEffect(ModEffects.ADAMANTITE_POWER)) return;

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

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.REGENERATION,
                100,
                2, // Regen III
                false,
                true,
                true
        ));

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.ABSORPTION,
                100,
                1, // Absorption II
                false,
                true,
                true
        ));

        // Consume the proc
        player.removeStatusEffect(ModEffects.ADAMANTITE_POWER);
    }
}
