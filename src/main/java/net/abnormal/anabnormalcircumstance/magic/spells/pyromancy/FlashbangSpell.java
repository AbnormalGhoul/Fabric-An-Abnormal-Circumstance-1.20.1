package net.abnormal.anabnormalcircumstance.magic.spells.pyromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import net.minecraft.particle.ParticleTypes;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * Flashbang (Pyromancy, Tier 2)
 * Cost: 65 mana | Cooldown: 90 seconds
 *
 * Emits a blinding burst of fire and light that briefly blinds nearby enemies.
 */
public class FlashbangSpell extends Spell {

    private static final double RADIUS = 5.0;
    private static final int DURATION_TICKS = 2 * 20; // 2 seconds

    public FlashbangSpell(Identifier id, Identifier icon) {
        super(
                id,
                SpellElement.PYROMANCY,
                SpellTier.TIER_2,
                65,
                90,
                icon,
                "Flashbang",
                "A sudden explosion of light blinds nearby enemies."
        );
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Explosion-like flash sound
        world.playSound(
                null,
                caster.getBlockPos(),
                SoundEvents.ENTITY_GENERIC_EXPLODE,
                SoundCategory.PLAYERS,
                1.8f,
                1.6f
        );

        // High-intensity flash particles
        spawnFlashParticles(world, caster);

        // Blindness effect
        StatusEffectInstance blindness =
                new StatusEffectInstance(
                        StatusEffects.BLINDNESS,
                        DURATION_TICKS,
                        0,
                        false,
                        true,
                        true
                );

        Vec3d pos = caster.getPos();
        Box area = new Box(
                pos.x - RADIUS, pos.y - RADIUS, pos.z - RADIUS,
                pos.x + RADIUS, pos.y + RADIUS, pos.z + RADIUS
        );

        List<LivingEntity> targets = world.getEntitiesByClass(
                LivingEntity.class,
                area,
                entity -> entity != caster && !caster.isTeammate(entity)
        );

        for (LivingEntity target : targets) {
            target.addStatusEffect(new StatusEffectInstance(blindness));
        }

        return true;
    }

    private void spawnFlashParticles(ServerWorld world, ServerPlayerEntity caster) {
        Vec3d pos = caster.getPos().add(0, 1.2, 0);

        // Intense white flash
        world.spawnParticles(
                ParticleTypes.FLASH,
                pos.x, pos.y, pos.z,
                1,
                0, 0, 0,
                0
        );

        // Fire sparks
        world.spawnParticles(
                ParticleTypes.FLAME,
                pos.x, pos.y, pos.z,
                40,
                0.6, 0.6, 0.6,
                0.04
        );

        // Smoke burst
        world.spawnParticles(
                ParticleTypes.CLOUD,
                pos.x, pos.y, pos.z,
                25,
                0.5, 0.4, 0.5,
                0.02
        );
    }
}
