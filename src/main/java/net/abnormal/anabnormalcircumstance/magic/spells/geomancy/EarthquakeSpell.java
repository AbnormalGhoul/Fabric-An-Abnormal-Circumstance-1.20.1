package net.abnormal.anabnormalcircumstance.magic.spells.geomancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class EarthquakeSpell extends Spell {
    private static final int DURATION_TICKS = 300; // 15s (300 ticks)
    private static final int DAMAGE_INTERVAL = 20; // every 1s
    private static final double RADIUS = 15.0;
    private static final float DAMAGE = 5.0f;

    public EarthquakeSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.GEOMANCY, SpellTier.TIER_3, 50, 90, icon, "Earthquake", "Shakes the ground in a wide area, damaging enemies standing on solid blocks.");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        if (caster == null || caster.getServerWorld() == null) return false;

        ServerWorld world = caster.getServerWorld();
        Vec3d center = caster.getPos();

        // Initial rumble sounds visible to nearby clients
        world.playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 1.5f, 0.8f);
        world.playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.6f, 1.0f);

        EarthquakeTracker.start(world, caster.getUuid(), center);

        return true;
    }

    // --- Earthquake Logic ---
    private static class EarthquakeTracker {
        // thread-safe list to iterate and remove safely from server tick handler
        private static final CopyOnWriteArrayList<EarthquakeInstance> ACTIVE = new CopyOnWriteArrayList<>();

        static {
            ServerTickEvents.END_SERVER_TICK.register(server -> {
                List<EarthquakeInstance> toRemove = new ArrayList<>();

                for (EarthquakeInstance quake : ACTIVE) {
                    quake.ticks++;

                    // Try to find caster; if absent -> end quake
                    ServerPlayerEntity caster = server.getPlayerManager().getPlayer(quake.casterId);
                    if (caster == null) {
                        toRemove.add(quake);
                        continue;
                    }

                    ServerWorld world = quake.world;
                    // spawn floor dust/particles each tick
                    quake.spawnGroundParticles();

                    // damage & effects on interval
                    if (quake.ticks % DAMAGE_INTERVAL == 0) {
                        quake.affectEnemies(caster);
                        world.playSound(null, BlockPos.ofFloored(quake.center), SoundEvents.BLOCK_STONE_BREAK, SoundCategory.PLAYERS, 1.0f, 0.9f);
                    }

                    if (quake.ticks >= DURATION_TICKS) {
                        world.playSound(null, BlockPos.ofFloored(quake.center), SoundEvents.BLOCK_DEEPSLATE_BREAK, SoundCategory.PLAYERS, 1.0f, 0.6f);
                        toRemove.add(quake);
                    }
                }

                if (!toRemove.isEmpty()) {
                    ACTIVE.removeAll(toRemove);
                }
            });
        }

        public static void start(ServerWorld world, UUID casterId, Vec3d center) {
            // avoid duplicate quakes by same caster in same world
            ACTIVE.removeIf(q -> q.casterId.equals(casterId) && q.world.getRegistryKey().equals(world.getRegistryKey()));
            ACTIVE.add(new EarthquakeInstance(world, casterId, center));
        }

        private static class EarthquakeInstance {
            final ServerWorld world;
            final UUID casterId;
            final Vec3d center;
            int ticks = 0;

            EarthquakeInstance(ServerWorld world, UUID casterId, Vec3d center) {
                this.world = world;
                this.casterId = casterId;
                this.center = center;
            }

            void spawnGroundParticles() {
                BlockState stone = Blocks.STONE.getDefaultState();
                int density = 40;

                for (int i = 0; i < density; i++) {
                    double angle = world.random.nextDouble() * 2 * Math.PI;
                    double dist = world.random.nextDouble() * RADIUS;
                    double x = center.x + Math.cos(angle) * dist;
                    double z = center.z + Math.sin(angle) * dist;

                    // find ground height: move down until non-air or bottom reached
                    int y = Math.max((int) (center.y - 1), world.getBottomY());
                    BlockPos pos = new BlockPos((int) x, y, (int) z);
                    while (world.getBlockState(pos).isAir() && pos.getY() > world.getBottomY()) {
                        pos = pos.down();
                    }

                    world.spawnParticles(
                            new BlockStateParticleEffect(ParticleTypes.BLOCK, stone),
                            x, pos.getY() + 1.0, z,
                            3, 0.2, 0.05, 0.2, 0.05
                    );

                    if (world.random.nextFloat() < 0.1f) {
                        world.spawnParticles(
                                new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()),
                                x, pos.getY() + 1.0, z,
                                2, 0.1, 0.05, 0.1, 0.02
                        );
                    }
                }
            }

            void affectEnemies(ServerPlayerEntity caster) {
                Box area = new Box(
                        center.x - RADIUS, center.y - 2, center.z - RADIUS,
                        center.x + RADIUS, center.y + 4, center.z + RADIUS
                );

                // filter living entities: alive, on ground, not teammate, not the caster
                List<LivingEntity> enemies = world.getEntitiesByClass(LivingEntity.class, area,
                        e -> e.isAlive() && e.isOnGround() && !isTeammateOrCaster(caster, e));

                for (LivingEntity target : enemies) {
                    target.damage(world.getDamageSources().playerAttack(caster), DAMAGE);
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1, false, true, true));

                    world.spawnParticles(
                            new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()),
                            target.getX(), target.getY(), target.getZ(),
                            10, 0.3, 0.1, 0.3, 0.05
                    );
                }
            }

            private boolean isTeammateOrCaster(ServerPlayerEntity caster, LivingEntity e) {
                if (e.getUuid().equals(caster.getUuid())) return true;
                return caster.isTeammate(e);
            }
        }
    }
}