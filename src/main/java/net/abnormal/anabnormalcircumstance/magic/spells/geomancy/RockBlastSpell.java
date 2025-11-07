package net.abnormal.anabnormalcircumstance.magic.spells.geomancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class RockBlastSpell extends Spell {

    public RockBlastSpell(Identifier id, Identifier icon) {
        super(
                id,
                SpellElement.GEOMANCY,
                SpellTier.TIER_1,
                30,          // mana
                120,         // cooldown (seconds)
                icon,
                "Rock Blast"
        );
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Sound (earth rumble / stone break)
        world.playSound(
                null,
                caster.getBlockPos(),
                SoundEvents.BLOCK_ANVIL_PLACE,
                SoundCategory.PLAYERS,
                2.0f,
                0.7f
        );

        // Particle burst around player
        spawnRockParticles(world, caster);

        // Apply knockback to ENEMIES only
        applyKnockback(world, caster);

        return true;
    }

    /** Spawns stone-block explosion particles around player */
    private void spawnRockParticles(ServerWorld world, ServerPlayerEntity caster) {

        world.spawnParticles(
                new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.STONE.getDefaultState()),
                caster.getX(), caster.getY(), caster.getZ(),
                80,           // amount
                1.2, 0.5, 1.2, // spread
                0.12          // speed
        );

        world.spawnParticles(
                ParticleTypes.POOF,
                caster.getX(),
                caster.getY() + 1,
                caster.getZ(),
                20,
                0.5, 0.3, 0.5,
                0.02
        );
    }

    /** Pushes away enemy entities */
    private void applyKnockback(ServerWorld world, ServerPlayerEntity caster) {

        double radius = 5.0;

        Box area = new Box(
                caster.getX() - radius, caster.getY() - radius, caster.getZ() - radius,
                caster.getX() + radius, caster.getY() + radius, caster.getZ() + radius
        );

        List<LivingEntity> nearby = world.getEntitiesByClass(
                LivingEntity.class,
                area,
                entity -> entity != caster && isEnemy(caster, entity)
        );

        for (LivingEntity target : nearby) {

            // knockback vector away from player
            Vec3d direction = target.getPos().subtract(caster.getPos()).normalize();

            double strength = 1.8;
            target.addVelocity(direction.x * strength, 0.8, direction.z * strength);

            target.velocityModified = true; // Ensure client sees velocity change
        }
    }

    /** Allies are ignored */
    private boolean isEnemy(ServerPlayerEntity caster, Entity target) {
        if (caster.isTeammate(target)) return false;
        return target instanceof LivingEntity;
    }
}
