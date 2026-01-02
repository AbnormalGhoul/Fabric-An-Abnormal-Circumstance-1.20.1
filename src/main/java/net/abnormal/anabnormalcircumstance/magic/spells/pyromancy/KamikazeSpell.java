package net.abnormal.anabnormalcircumstance.magic.spells.pyromancy;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
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

public class KamikazeSpell extends Spell {

    private static final int STUN_DURATION = 40; // player is stunned for 2s
    private static final int CHARGE_DURATION = 35; // detonate after 35 ticks
    private static final int DAMAGE = 20;
    private static final double RADIUS = 3.0;

    public KamikazeSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.PYROMANCY, SpellTier.TIER_3, 100, (60 * 15), icon,
                "Kamikaze", "Sacrifice yourself in a fiery explosion that devastates nearby foes.");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Apply stun to self
        caster.addStatusEffect(new StatusEffectInstance(ModEffects.STUN, STUN_DURATION, 0, false, true, true));

        // Initial charge sound
        world.playSound(null, caster.getBlockPos(),
                SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1.8f, 1.5f);

        // Begin charge-up tracker
        KamikazeTracker.start(world, caster);

        return true;
    }

    private static class KamikazeTracker {
        private static final Map<UUID, Integer> active = new HashMap<>();

        static {
            ServerTickEvents.END_SERVER_TICK.register(server -> {
                Iterator<Map.Entry<UUID, Integer>> it = active.entrySet().iterator();

                while (it.hasNext()) {
                    Map.Entry<UUID, Integer> entry = it.next();
                    UUID id = entry.getKey();
                    int ticks = entry.getValue();

                    ServerPlayerEntity player = server.getPlayerManager().getPlayer(id);
                    if (player == null || player.isRemoved() || !player.isAlive()) {
                        it.remove();
                        continue;
                    }

                    ServerWorld world = player.getServerWorld();

                    // Every 5 ticks: play charge sound + particles
                    if (ticks % 5 == 0) {
                        spawnChargeParticles(world, player);
                        world.playSound(null, player.getBlockPos(),
                                SoundEvents.ENTITY_ARROW_HIT_PLAYER,
                                SoundCategory.PLAYERS,
                                1.2f, 2.0f
                        );
                    }

                    // Detonate after 35 ticks
                    if (ticks >= CHARGE_DURATION) {
                        detonate(world, player);
                        it.remove();
                        continue;
                    }

                    active.put(id, ticks + 1);
                }
            });
        }

        static void start(ServerWorld world, ServerPlayerEntity caster) {
            active.put(caster.getUuid(), 0);
        }

        private static void spawnChargeParticles(ServerWorld world, ServerPlayerEntity player) {
            Vec3d pos = player.getPos().add(0, 1.0, 0);
            for (int i = 0; i < 20; i++) {
                double dx = (world.random.nextDouble() - 0.5) * 0.6;
                double dy = world.random.nextDouble();
                double dz = (world.random.nextDouble() - 0.5) * 0.6;
                world.spawnParticles(ParticleTypes.END_ROD,
                        pos.x + dx, pos.y + dy, pos.z + dz,
                        1, 0, 0, 0, 0);
            }
        }

        private static void detonate(ServerWorld world, ServerPlayerEntity player) {
            Vec3d pos = player.getPos();

            // Explosion particles
            world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, pos.x, pos.y + 1, pos.z, 1, 0, 0, 0, 0);
            world.spawnParticles(ParticleTypes.FLAME, pos.x, pos.y + 0.2, pos.z, 80, 0.8, 0.4, 0.8, 0.05);
            world.spawnParticles(ParticleTypes.SMOKE, pos.x, pos.y + 0.3, pos.z, 60, 0.8, 0.5, 0.8, 0.03);

            // Ground circle to show AoE
            spawnGroundRing(world, pos, RADIUS);

            // Explosion sound
            world.playSound(null, player.getBlockPos(),
                    SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 2.0f, 1.0f);
            world.playSound(null, player.getBlockPos(),
                    SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1.2f, 1.3f);

            // Damage nearby enemies
            Box area = new Box(
                    pos.x - RADIUS, pos.y - 1, pos.z - RADIUS,
                    pos.x + RADIUS, pos.y + 2, pos.z + RADIUS
            );

            List<Entity> entities = world.getOtherEntities(player, area,
                    e -> e instanceof LivingEntity && e.isAlive() && !player.isTeammate(e));

            for (Entity e : entities) {
                if (e instanceof LivingEntity target) {
                    target.damage(world.getDamageSources().outOfWorld(), DAMAGE); // void damage
                }
            }

            // Kill the caster
            player.damage(world.getDamageSources().outOfWorld(), Float.MAX_VALUE);
        }

        private static void spawnGroundRing(ServerWorld world, Vec3d pos, double radius) {
            int points = 48;
            for (int i = 0; i < points; i++) {
                double angle = 2 * Math.PI * i / points;
                double x = pos.x + Math.cos(angle) * radius;
                double z = pos.z + Math.sin(angle) * radius;
                double y = pos.y + 0.05;
                world.spawnParticles(
                        new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.FIRE.getDefaultState()),
                        x, y, z,
                        1, 0, 0, 0, 0.01
                );
            }
        }
    }
}
