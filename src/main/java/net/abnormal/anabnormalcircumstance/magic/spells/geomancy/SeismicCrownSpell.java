package net.abnormal.anabnormalcircumstance.magic.spells.geomancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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

public class SeismicCrownSpell extends Spell {
    private static final double RADIUS = 1.0;
    private static final int DAMAGE = 10;
    private static final int DURATION_TICKS = 10 * 20; // 15 seconds
    private static final double KNOCKBACK_STRENGTH = 1;

    public SeismicCrownSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.GEOMANCY, SpellTier.TIER_3, 60, 120, icon, "Seismic Crown");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Activation sound
        world.playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 1.8f, 0.6f);
        world.playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_IRON_GOLEM_ATTACK, SoundCategory.PLAYERS, 1.2f, 1.0f);

        // Start orbit effect
        SeismicCrownTracker.startTracking(world, caster);

        return true;
    }

    private static class SeismicCrownTracker {
        private static final Map<UUID, Integer> tracked = new HashMap<>();
        private static final Map<UUID, Float> rotationAngles = new HashMap<>();

        static {
            ServerTickEvents.END_SERVER_TICK.register(server -> {
                Iterator<Map.Entry<UUID, Integer>> it = tracked.entrySet().iterator();

                while (it.hasNext()) {
                    Map.Entry<UUID, Integer> entry = it.next();
                    UUID uuid = entry.getKey();
                    int tickCount = entry.getValue();
                    ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);

                    if (player == null || player.isRemoved() || !player.isAlive()) {
                        it.remove();
                        rotationAngles.remove(uuid);
                        continue;
                    }

                    ServerWorld world = player.getServerWorld();

                    // Spawn rotating stone particles
                    spawnOrbitingParticles(world, player, rotationAngles.computeIfAbsent(uuid, id -> 0f));

                    // Damage and knockback nearby players
                    applyAuraEffect(world, player);

                    // Increment rotation and time
                    rotationAngles.put(uuid, rotationAngles.get(uuid) + 10f); // degrees per tick for orbit
                    if (rotationAngles.get(uuid) >= 360f) {
                        rotationAngles.put(uuid, rotationAngles.get(uuid) - 360f);
                    }

                    // End after 25 seconds
                    if (tickCount >= DURATION_TICKS) {
                        it.remove();
                        rotationAngles.remove(uuid);
                        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_STONE_BREAK, SoundCategory.PLAYERS, 1.0f, 1.0f);
                        continue;
                    }

                    tracked.put(uuid, tickCount + 1);
                }
            });
        }

        static void startTracking(ServerWorld world, ServerPlayerEntity player) {
            tracked.put(player.getUuid(), 0);
            rotationAngles.put(player.getUuid(), 0f);
        }

        private static void spawnOrbitingParticles(ServerWorld world, ServerPlayerEntity player, float rotationBase) {
            Vec3d center = player.getPos().add(0, 1.5, 0);
            int particleCount = 10;
            double orbitRadius = 1.5;

            for (int i = 0; i < particleCount; i++) {
                double angle = Math.toRadians(rotationBase + (i * (360.0 / particleCount)));
                double x = center.x + Math.cos(angle) * orbitRadius;
                double z = center.z + Math.sin(angle) * orbitRadius;
                double y = center.y + Math.sin(angle / 2.0) * 0.3; // small bobbing motion

                world.spawnParticles(
                        new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.STONE.getDefaultState()),
                        x, y, z,
                        1, 0, 0, 0, 0
                );
            }
        }

        private static void applyAuraEffect(ServerWorld world, ServerPlayerEntity caster) {
            Vec3d center = caster.getPos().add(0, 1, 0);
            Box area = new Box(
                    center.x - RADIUS, center.y - 1, center.z - RADIUS,
                    center.x + RADIUS, center.y + 2, center.z + RADIUS
            );

            List<Entity> entities = world.getOtherEntities(caster, area, e -> e instanceof LivingEntity && e.isAlive() && !caster.isTeammate(e));
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity target) {
                    // Damage
                    target.damage(world.getDamageSources().playerAttack(caster), DAMAGE);

                    // Knockback
                    Vec3d dir = target.getPos().subtract(caster.getPos()).normalize().multiply(KNOCKBACK_STRENGTH);
                    target.addVelocity(dir.x, 0.3, dir.z);
                    target.velocityModified = true;

                    // Impact sound and debris burst
                    world.playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_STONE_HIT, SoundCategory.PLAYERS, 1.0f, 0.8f);
                    world.spawnParticles(
                            new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.COBBLESTONE.getDefaultState()),
                            target.getX(), target.getY() + 0.5, target.getZ(),
                            15, 0.3, 0.3, 0.3, 0.1
                    );
                }
            }
        }
    }
}
