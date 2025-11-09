package net.abnormal.anabnormalcircumstance.event;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class PhoenixFireHandler {

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {
            if (!(entity instanceof ServerPlayerEntity player)) return true;

            if (player.hasStatusEffect(ModEffects.PHOENIX_FIRE)) {
                StatusEffectInstance inst = player.getStatusEffect(ModEffects.PHOENIX_FIRE);
                if (inst != null) {
                    handlePhoenixRebirth(player, inst);
                    return false; // cancel death
                }
            }
            return true; // allow normal death
        });
    }

    private static void handlePhoenixRebirth(ServerPlayerEntity player, StatusEffectInstance inst) {
        var world = player.getServerWorld();

        // Restore 6 hearts
        player.setHealth(12.0F);
        player.clearStatusEffects();

        // Buffs
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 3 * 60 * 20, 2));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 15 * 20, 1));

        // Sound + particles (totem-like)
        world.playSound(null, player.getBlockPos(),
                SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.5f, 1.0f);
        world.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING,
                player.getX(), player.getY() + 1.0, player.getZ(),
                100, 0.6, 1.0, 0.6, 0.2);

        // Decrease amplifier (lives remaining)
        int amp = inst.getAmplifier();
        if (amp > 0) {
            player.addStatusEffect(new StatusEffectInstance(ModEffects.PHOENIX_FIRE,
                    inst.getDuration(), amp - 1, false, false, true));
        } else {
            player.removeStatusEffect(ModEffects.PHOENIX_FIRE);
        }
    }
}
