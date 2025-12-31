package net.abnormal.anabnormalcircumstance.magic.spells.aeromancy;

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
 * Fleet Foot (Aeromancy, Tier 2)
 * Cost: 40 mana | Cooldown: 45 seconds
 *
 * Grants Speed III to yourself and nearby allies.
 */
public class FleetFootSpell extends Spell {

    private static final int DURATION_TICKS = 15 * 20;
    private static final int SPEED_LEVEL = 2; // Speed III
    private static final double RADIUS = 5.0;

    public FleetFootSpell(Identifier id, Identifier icon) {
        super(
                id,
                SpellElement.AEROMANCY,
                SpellTier.TIER_2,
                40,
                45,
                icon,
                "Fleet Foot",
                "A rush of wind hastens you and nearby allies."
        );
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Sound cue
        world.playSound(
                null,
                caster.getBlockPos(),
                SoundEvents.ENTITY_PHANTOM_FLAP,
                SoundCategory.PLAYERS,
                1.5f,
                1.6f
        );

        // Wind burst particles
        spawnFleetFootParticles(world, caster);

        StatusEffectInstance speed =
                new StatusEffectInstance(
                        StatusEffects.SPEED,
                        DURATION_TICKS,
                        SPEED_LEVEL,
                        false,
                        true,
                        true
                );

        // Apply to self
        caster.addStatusEffect(speed);

        // Apply to nearby team allies
        Vec3d pos = caster.getPos();
        Box area = new Box(
                pos.x - RADIUS, pos.y - RADIUS, pos.z - RADIUS,
                pos.x + RADIUS, pos.y + RADIUS, pos.z + RADIUS
        );

        List<LivingEntity> allies = world.getEntitiesByClass(
                LivingEntity.class,
                area,
                entity -> entity != caster && caster.isTeammate(entity)
        );

        for (LivingEntity ally : allies) {
            ally.addStatusEffect(new StatusEffectInstance(speed));
        }

        return true;
    }

    private void spawnFleetFootParticles(ServerWorld world, ServerPlayerEntity caster) {
        Vec3d pos = caster.getPos().add(0, 1.0, 0);

        // Soft cloud burst
        world.spawnParticles(
                ParticleTypes.CLOUD,
                pos.x, pos.y, pos.z,
                40,
                0.6, 0.4, 0.6,
                0.02
        );

        // Airy sweep trails
        world.spawnParticles(
                ParticleTypes.SWEEP_ATTACK,
                pos.x, pos.y, pos.z,
                15,
                0.4, 0.2, 0.4,
                0.0
        );
    }
}
