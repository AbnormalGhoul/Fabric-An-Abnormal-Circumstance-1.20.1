package net.abnormal.anabnormalcircumstance.magic.spells.geomancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.abnormal.anabnormalcircumstance.effect.ModEffects;
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

    private static final int DURATION_TICKS = 20 * 20;
    private static final int DAMAGE_INTERVAL = 20; // every 1s
    private static final double RADIUS = 12.0;
    private static final float DAMAGE = 20.0f;
    private static final int RING_POINTS = 48;
    private static final double RING_THICKNESS = 0.4;

    private static final double INNER_RADIUS = 4.0;
    private static final int INNER_PARTICLE_DENSITY = 80;
    private static final int VULNERABILITY_DURATION = 60; // 3 seconds
    private static final int VULNERABILITY_AMPLIFIER = 0;

    public EarthquakeSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.GEOMANCY, SpellTier.TIER_3, 60, 90, icon, "Earthquake", "Shakes the ground in a wide area, weakening enemies standing on solid blocks.");
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

    // Earthquake Logic
    private static class EarthquakeTracker {
        // thread-safe list to iterate and remove safely from server tick handler
        private static final CopyOnWriteArrayList<EarthquakeInstance> ACTIVE = new CopyOnWriteArrayList<>();

        static {
            ServerTickEvents.END_SERVER_TICK.register(server -> {
                List<EarthquakeInstance> toRemove = new ArrayList<>();

                for (EarthquakeInstance quake : ACTIVE) {
                    quake.ticks++;

                    // Try to find caster, if absent then end quake
                    ServerPlayerEntity caster = server.getPlayerManager().getPlayer(quake.casterId);
                    if (caster == null) {
                        toRemove.add(quake);
                        continue;
                    }

                    ServerWorld world = quake.world;
                    // spawn floor dust/particles each tick
                    quake.spawnGroundParticles();
                    quake.spawnInnerEpicenterParticles();

                    // damage & effects on interval
                    if (quake.ticks % DAMAGE_INTERVAL == 0) {
                        quake.affectEnemies(caster);
                        quake.affectInnerEnemies(caster);
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
                BlockState darkStoneA = Blocks.DEEPSLATE.getDefaultState();
                BlockState darkStoneB = Blocks.GRAY_CONCRETE.getDefaultState();

                int density = 40;

                for (int i = 0; i < density; i++) {
                    double angle = world.random.nextDouble() * 2 * Math.PI;
                    double dist = world.random.nextDouble() * RADIUS;
                    double x = center.x + Math.cos(angle) * dist;
                    double z = center.z + Math.sin(angle) * dist;

                    BlockPos pos = findGround(x, z);

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

                for (int i = 0; i < RING_POINTS; i++) {
                    double angle = (2 * Math.PI * i) / RING_POINTS;

                    double radius = RADIUS + world.random.nextDouble() * RING_THICKNESS;
                    double x = center.x + Math.cos(angle) * radius;
                    double z = center.z + Math.sin(angle) * radius;

                    BlockPos pos = findGround(x, z);

                    BlockState ringBlock = world.random.nextBoolean()
                            ? darkStoneA
                            : darkStoneB;

                    world.spawnParticles(
                            new BlockStateParticleEffect(ParticleTypes.BLOCK, ringBlock),
                            x, pos.getY() + 1.05, z,
                            2,
                            0.05, 0.02, 0.05,
                            0.01
                    );
                }
            }

            private BlockPos findGround(double x, double z) {
                int y = Math.max((int) (center.y - 1), world.getBottomY());
                BlockPos pos = new BlockPos((int) x, y, (int) z);

                while (world.getBlockState(pos).isAir() && pos.getY() > world.getBottomY()) {
                    pos = pos.down();
                }
                return pos;
            }

            void affectEnemies(ServerPlayerEntity caster) {
                Box area = new Box(
                        center.x - RADIUS, center.y - 2, center.z - RADIUS,
                        center.x + RADIUS, center.y + 4, center.z + RADIUS
                );

                // filter living entities: alive, on ground, not teammate, not the caster, not spectator/invulnerable
                List<LivingEntity> enemies = world.getEntitiesByClass(LivingEntity.class, area,
                        e -> e.isAlive()
                                && e.isOnGround()
                                && !caster.getUuid().equals(e.getUuid())
                                && !caster.isTeammate(e)
                                && !e.isSpectator()
                                && !e.isInvulnerable()
                );

                for (LivingEntity target : enemies) {
                    target.damage(world.getDamageSources().playerAttack(caster), DAMAGE);
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1, false, true, true));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 60, 0, false, true, true));
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 60, 1, false, true, true));

                    world.spawnParticles(
                            new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()),
                            target.getX(), target.getY(), target.getZ(),
                            10, 0.3, 0.1, 0.3, 0.05
                    );
                }
            }

            void spawnInnerEpicenterParticles() {
                BlockState dripstone = Blocks.POINTED_DRIPSTONE.getDefaultState();

                for (int i = 0; i < INNER_PARTICLE_DENSITY; i++) {
                    double angle = world.random.nextDouble() * 2 * Math.PI;
                    double dist = world.random.nextDouble() * INNER_RADIUS;

                    double x = center.x + Math.cos(angle) * dist;
                    double z = center.z + Math.sin(angle) * dist;

                    BlockPos pos = findGround(x, z);

                    world.spawnParticles(
                            new BlockStateParticleEffect(ParticleTypes.BLOCK, dripstone),
                            x,
                            pos.getY() + 1.1,
                            z,
                            4,
                            0.15, 0.1, 0.15,
                            0.02
                    );
                }
            }

            void affectInnerEnemies(ServerPlayerEntity caster) {
                Box innerArea = new Box(
                        center.x - INNER_RADIUS, center.y - 2, center.z - INNER_RADIUS,
                        center.x + INNER_RADIUS, center.y + 4, center.z + INNER_RADIUS
                );

                List<LivingEntity> targets = world.getEntitiesByClass(
                        LivingEntity.class,
                        innerArea,
                        e -> e.isAlive()
                                && e.isOnGround()
                                && !e.getUuid().equals(caster.getUuid())
                                && !caster.isTeammate(e)
                                && !e.isSpectator()
                                && !e.isInvulnerable()
                );

                for (LivingEntity target : targets) {
                    target.addStatusEffect(new StatusEffectInstance(
                            ModEffects.VULNERABILITY,
                            VULNERABILITY_DURATION,
                            VULNERABILITY_AMPLIFIER,
                            false,
                            true,
                            true
                    ));

                    world.spawnParticles(
                            new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.POINTED_DRIPSTONE.getDefaultState()),
                            target.getX(),
                            target.getY() + 0.5,
                            target.getZ(),
                            8,
                            0.2, 0.2, 0.2,
                            0.03
                    );
                }
            }

            private boolean isTeammateOrCaster(ServerPlayerEntity caster, LivingEntity e) {
                if (e.getUuid().equals(caster.getUuid())) return true;

                if (e instanceof ServerPlayerEntity) {
                    var casterTeam = caster.getScoreboardTeam();
                    var targetTeam = ((ServerPlayerEntity) e).getScoreboardTeam();
                    // Only consider teammates if the caster actually has a team and it matches the target's team
                    return casterTeam != null && casterTeam.equals(targetTeam);
                }

                return caster.isTeammate(e);
            }
        }
    }
}