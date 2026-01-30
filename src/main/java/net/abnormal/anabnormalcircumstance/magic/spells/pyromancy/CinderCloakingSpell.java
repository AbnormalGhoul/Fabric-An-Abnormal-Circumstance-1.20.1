package net.abnormal.anabnormalcircumstance.magic.spells.pyromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;

import net.minecraft.entity.Entity;
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

public class CinderCloakingSpell extends Spell {

    public CinderCloakingSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.PYROMANCY, SpellTier.TIER_1, 20, 120, icon, "Cinder Cloaking", "Marks all nearby enemies with a glowing ember effect, making them easy to track");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Visual and audio feedback
        playCastEffects(world, caster);

        // Apply glowing to enemies
        applyGlowingToEnemies(world, caster);

        return true;
    }

    private void applyGlowingToEnemies(ServerWorld world, ServerPlayerEntity caster) {

        double radius = 30.0;

        Box area = new Box(
                caster.getX() - radius, caster.getY() - radius, caster.getZ() - radius,
                caster.getX() + radius, caster.getY() + radius, caster.getZ() + radius
        );

        List<LivingEntity> entities = world.getEntitiesByClass(
                LivingEntity.class,
                area,
                e -> e != caster && isEnemy(caster, e)
        );

        for (LivingEntity target : entities) {
            target.addStatusEffect(
                    new StatusEffectInstance(
                            StatusEffects.GLOWING,
                            30 * 20, // 30 seconds
                            0,
                            false,
                            false,
                            true
                    )
            );
        }
    }

    private boolean isEnemy(ServerPlayerEntity caster, Entity target) {
        // Minecraft teams = allies / enemies
        if (caster.isTeammate(target)) return false;
        return target instanceof LivingEntity;
    }

    private void playCastEffects(ServerWorld world, ServerPlayerEntity caster) {

        // Sound when cast
        world.playSound(
                null,
                caster.getBlockPos(),
                SoundEvents.ENTITY_BLAZE_SHOOT,
                SoundCategory.PLAYERS,
                2.0f,
                0.8f
        );

        // Spiral flame particles (cosmetic effect)
        spawnFlameSpiral(world, caster);
    }

    private void spawnFlameSpiral(ServerWorld world, ServerPlayerEntity caster) {
        Vec3d center = caster.getPos().add(0, 1.2, 0);

        for (int i = 0; i < 40; i++) {
            double angle = (i / 40.0) * Math.PI * 2;
            double radius = 0.5 + (i * 0.03);
            double height = center.y + (i * 0.03);

            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;

            world.spawnParticles(
                    ParticleTypes.FLAME,
                    x, height, z,
                    1,
                    0.01, 0.01, 0.01,
                    0.01
            );
        }
    }
}
