package net.abnormal.anabnormalcircumstance.magic.spells.geomancy;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class StompSpell extends Spell {
    private static final double RADIUS = 5.0;
    private static final int DAMAGE = 15;

    public StompSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.GEOMANCY, SpellTier.TIER_2, 45, 45, icon, "Stomp");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Initial charge sound & dirt particles
        world.playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_IRON_GOLEM_ATTACK, SoundCategory.PLAYERS, 1.8f, 0.8f);
        spawnChargeParticles(world, caster);

        // Launch upward
        caster.addVelocity(0, 1.0, 0);
        caster.velocityModified = true;

        // Start monitoring for ground impact
        LandingTracker.startTracking(world, caster);

        return true;
    }

    private static void doGroundImpact(ServerWorld world, ServerPlayerEntity caster) {
        if (caster.isRemoved()) return;

        // Explosion sounds
        world.playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 2.2f, 1.0f);
        world.playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_STONE_BREAK, SoundCategory.PLAYERS, 1.6f, 0.7f);

        spawnImpactParticles(world, caster);

        // Affect nearby enemies
        Box area = new Box(
                caster.getX() - RADIUS, caster.getY() - 2, caster.getZ() - RADIUS,
                caster.getX() + RADIUS, caster.getY() + 2, caster.getZ() + RADIUS
        );

        List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, area,
                e -> e.isAlive() && e != caster && !caster.isTeammate(e));

        for (LivingEntity target : targets) {
            target.damage(world.getDamageSources().playerAttack(caster), DAMAGE);
            target.addStatusEffect(new StatusEffectInstance(ModEffects.STUN, 4 * 20, 0));

            // Small debris burst at each hit entity
            world.spawnParticles(
                    new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.STONE.getDefaultState()),
                    target.getX(), target.getY(), target.getZ(),
                    15, 0.4, 0.4, 0.4, 0.1
            );
        }
    }

    private static void spawnChargeParticles(ServerWorld world, ServerPlayerEntity caster) {
        Vec3d pos = caster.getPos().add(0, 0.2, 0);
        for (int i = 0; i < 60; i++) {
            double angle = world.random.nextDouble() * 2 * Math.PI;
            double radius = 0.5 + world.random.nextDouble() * 0.8;
            double x = pos.x + Math.cos(angle) * radius;
            double z = pos.z + Math.sin(angle) * radius;
            double y = pos.y + world.random.nextDouble() * 1.0;

            world.spawnParticles(
                    new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()),
                    x, y, z, 1, 0, 0, 0, 0
            );
        }
    }

    private static void spawnImpactParticles(ServerWorld world, ServerPlayerEntity caster) {
        Vec3d pos = caster.getPos();
        for (int i = 0; i < 120; i++) {
            double angle = world.random.nextDouble() * 2 * Math.PI;
            double radius = 0.5 + world.random.nextDouble() * RADIUS;
            double speed = 0.2 + world.random.nextDouble() * 0.4;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;

            world.spawnParticles(
                    new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.STONE.getDefaultState()),
                    pos.x + x, pos.y + 0.1, pos.z + z,
                    1, 0.1, 0.1, 0.1, speed
            );
        }
    }

    /**
     * Detects when a player lands on the ground after casting the spell.
     */
    private static class LandingTracker {
        private static final Map<UUID, Integer> tracked = new HashMap<>();

        static {
            ServerTickEvents.END_SERVER_TICK.register(server -> {
                Iterator<Map.Entry<UUID, Integer>> it = tracked.entrySet().iterator();

                while (it.hasNext()) {
                    Map.Entry<UUID, Integer> entry = it.next();
                    UUID id = entry.getKey();
                    int ticks = entry.getValue();

                    ServerPlayerEntity player = server.getPlayerManager().getPlayer(id);
                    if (player == null) {
                        it.remove();
                        continue;
                    }

                    // Detect landing
                    if (player.isOnGround() || ticks >= 40) {
                        doGroundImpact(player.getServerWorld(), player);
                        it.remove();
                        continue;
                    }

                    tracked.put(id, ticks + 1);
                }
            });
        }

        static void startTracking(ServerWorld world, ServerPlayerEntity player) {
            tracked.put(player.getUuid(), 0);
        }
    }
}
