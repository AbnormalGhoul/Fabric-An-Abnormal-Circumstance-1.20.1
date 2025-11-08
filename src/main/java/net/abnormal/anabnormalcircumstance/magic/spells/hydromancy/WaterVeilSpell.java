package net.abnormal.anabnormalcircumstance.magic.spells.hydromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.particle.ParticleTypes;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class WaterVeilSpell extends Spell {

    private static final int RADIUS = 10;

    public WaterVeilSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.HYDROMANCY, SpellTier.TIER_1, 15, 30, icon, "Water Veil", "Forms a protective layer of moisture that shields you from fire and heat.");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();
        Vec3d pos = caster.getPos();

        // Play hydromancy sound
        world.playSound(
                null,
                caster.getBlockPos(),
                SoundEvents.AMBIENT_UNDERWATER_EXIT,
                SoundCategory.PLAYERS,
                2.0f,
                1.1f
        );

        // Water swirl particle effect
        spawnWaterSwirl(world, pos);

        // Apply fire resistance to caster + allies
        applyFireResToAllies(world, caster);

        return true;
    }

    private void applyFireResToAllies(ServerWorld world, ServerPlayerEntity caster) {

        Box area = new Box(
                caster.getX() - RADIUS, caster.getY() - RADIUS, caster.getZ() - RADIUS,
                caster.getX() + RADIUS, caster.getY() + RADIUS, caster.getZ() + RADIUS
        );

        // EXACT SAME FILTER LOGIC AS ROCKPOLISHSPELL
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, area,
                e -> e.isAlive() && (e == caster || caster.isTeammate(e)));

        for (LivingEntity ally : entities) {
            ally.addStatusEffect(
                    new StatusEffectInstance(
                            StatusEffects.FIRE_RESISTANCE,
                            30 * 20,
                            0,
                            false,
                            true,
                            true
                    )
            );

            // Mini water puff on each ally
            world.spawnParticles(
                    ParticleTypes.SPLASH,
                    ally.getX(), ally.getY() + 1.2, ally.getZ(),
                    20,
                    0.4, 0.4, 0.4,
                    0.03
            );
        }
    }

    private void spawnWaterSwirl(ServerWorld world, Vec3d pos) {
        for (int i = 0; i < 40; i++) {
            double angle = (i / 40.0) * Math.PI * 2;
            double radius = 0.8;
            double height = (i / 40.0) * 1.2;

            double x = pos.x + Math.cos(angle) * radius;
            double y = pos.y + 0.2 + height;
            double z = pos.z + Math.sin(angle) * radius;

            world.spawnParticles(
                    ParticleTypes.SPLASH,
                    x, y, z,
                    1,
                    0.03, 0.03, 0.03,
                    0.02
            );
        }
    }
}
