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

public class EarthquakeSpell extends Spell {
    private static final int DURATION_TICKS = 200; // 10 seconds
    private static final int DAMAGE_INTERVAL = 20; // every 1 second
    private static final double RADIUS = 15.0;
    private static final float DAMAGE = 5.0f;

    public EarthquakeSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.GEOMANCY, SpellTier.TIER_3, 50, 120, icon, "Earthquake", "Shakes the ground in a wide area, damaging enemies standing on solid blocks.");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        Vec3d quakeCenter = caster.getPos(); // fixed point of origin

        // Initial rumble sound
        world.playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS, 1.5f, 0.8f);
        world.playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.6f, 1.0f);

        EarthquakeTracker.start(world, caster, quakeCenter);

        return true;
    }

    // --- Earthquake Logic ---
    private static class EarthquakeTracker {
        private static final List<EarthquakeInstance> ACTIVE = new ArrayList<>();

        static {
            ServerTickEvents.END_SERVER_TICK.register(server -> {
                Iterator<EarthquakeInstance> it = ACTIVE.iterator();

                while (it.hasNext()) {
                    EarthquakeInstance quake = it.next();
                    quake.ticks++;

                    ServerPlayerEntity caster = server.getPlayerManager().getPlayer(quake.casterId);
                    if (caster == null || quake.ticks > DURATION_TICKS) {
                        it.remove();
                        continue;
                    }

                    ServerWorld world = quake.world;

                    // show floor-wide dust particles
                    quake.spawnGroundParticles();

                    // apply effects every second
                    if (quake.ticks % DAMAGE_INTERVAL == 0) {
                        quake.affectEnemies();
                        world.playSound(null, BlockPos.ofFloored(quake.center), SoundEvents.BLOCK_STONE_BREAK, SoundCategory.PLAYERS, 1.0f, 0.9f);
                    }

                    if (quake.ticks >= DURATION_TICKS) {
                        world.playSound(null, BlockPos.ofFloored(quake.center), SoundEvents.BLOCK_DEEPSLATE_BREAK, SoundCategory.PLAYERS, 1.0f, 0.6f);
                        it.remove();
                    }
                }
            });
        }

        public static void start(ServerWorld world, ServerPlayerEntity caster, Vec3d center) {
            ACTIVE.add(new EarthquakeInstance(world, caster.getUuid(), center));
        }

        // One quake event anchored at a fixed world position
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
                int density = 40; // more = denser particle field

                for (int i = 0; i < density; i++) {
                    double angle = world.random.nextDouble() * 2 * Math.PI;
                    double dist = world.random.nextDouble() * RADIUS;
                    double x = center.x + Math.cos(angle) * dist;
                    double z = center.z + Math.sin(angle) * dist;

                    // find ground height at this spot
                    BlockPos pos = new BlockPos((int) x, (int) (center.y - 1), (int) z);
                    while (!world.getBlockState(pos).isSolidBlock(world, pos) && pos.getY() > world.getBottomY()) {
                        pos = pos.down();
                    }

                    world.spawnParticles(
                            new BlockStateParticleEffect(ParticleTypes.BLOCK, stone),
                            x, pos.getY() + 1.0, z,
                            3, 0.2, 0.05, 0.2, 0.05
                    );

                    // occasional dirt bursts
                    if (world.random.nextFloat() < 0.1f) {
                        world.spawnParticles(
                                new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()),
                                x, pos.getY() + 1.0, z,
                                2, 0.1, 0.05, 0.1, 0.02
                        );
                    }
                }
            }

            void affectEnemies() {
                ServerPlayerEntity caster = world.getServer().getPlayerManager().getPlayer(casterId);
                if (caster == null) return;

                Box area = new Box(
                        center.x - RADIUS, center.y - 2, center.z - RADIUS,
                        center.x + RADIUS, center.y + 4, center.z + RADIUS
                );

                List<LivingEntity> enemies = world.getEntitiesByClass(LivingEntity.class, area,
                        e -> e.isAlive() && !caster.isTeammate(e) && e.isOnGround() && e != caster);

                for (LivingEntity target : enemies) {
                    target.damage(world.getDamageSources().playerAttack(caster), DAMAGE);
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1, false, true, true));

                    // small dirt splash at target
                    world.spawnParticles(
                            new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState()),
                            target.getX(), target.getY(), target.getZ(),
                            10, 0.3, 0.1, 0.3, 0.05
                    );
                }
            }
        }
    }
}
