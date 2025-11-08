package net.abnormal.anabnormalcircumstance.magic.spells.pyromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import net.minecraft.particle.ParticleTypes;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class PyroclasmWaveSpell extends Spell {

    public PyroclasmWaveSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.PYROMANCY, SpellTier.TIER_3, 35, 50, icon, "Pyroclasm Wave", "Creates expanding rings of fire at your feet that damage enemies close to you.");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Main activation sound
        world.playSound(
                null, caster.getBlockPos(),
                SoundEvents.ENTITY_GENERIC_EXPLODE,
                SoundCategory.PLAYERS,
                1.4f, 1.2f
        );

        // Burst particles at feet
        world.spawnParticles(
                ParticleTypes.LAVA,
                caster.getX(), caster.getY() + 0.1, caster.getZ(),
                30,
                0.4, 0.1, 0.4,
                0.05
        );

        // Schedule the 5 waves
        for (int wave = 0; wave < 5; wave++) {
            int delay = wave * 10;      // 5 ticks between waves
            int waveNumber = wave;

            Delayed.schedule(world, delay, () ->
                    spawnWave(world, caster, waveNumber)
            );
        }

        return true;
    }

    private void spawnWave(ServerWorld world, ServerPlayerEntity caster, int waveIndex) {
        double maxRadius = 10.0;
        double waves = 5.0;

        // Each wave increases its radius portion
        double radius = (maxRadius / waves) * (waveIndex + 1);

        Vec3d center = new Vec3d(caster.getX(), caster.getY() + 0.1, caster.getZ());

        // Particle ring around the player
        int points = 40;
        for (int i = 0; i < points; i++) {

            double angle = (2 * Math.PI * i) / points;
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);

            world.spawnParticles(
                    ParticleTypes.FLAME,
                    x, center.y, z,
                    3,
                    0.04, 0.01, 0.04,
                    0.02
            );

            world.spawnParticles(
                    ParticleTypes.SMOKE,
                    x, center.y, z,
                    2,
                    0.06, 0.02, 0.06,
                    0.01
            );
        }

        // Damage / burn enemies within radius
        Box area = new Box(
                center.x - radius, center.y - 1, center.z - radius,
                center.x + radius, center.y + 2, center.z + radius
        );

        List<LivingEntity> targets = world.getEntitiesByClass(
                LivingEntity.class,
                area,
                e -> e != caster && !caster.isTeammate(e)
        );

        for (LivingEntity target : targets) {
            target.damage(world.getDamageSources().magic(), 20.0f);
            target.setOnFireFor(5);
        }

        // Small wave sound
        world.playSound(
                null, caster.getBlockPos(),
                SoundEvents.BLOCK_LAVA_EXTINGUISH,
                SoundCategory.PLAYERS,
                0.8f, 1.4f
        );
    }

    public static class Delayed {

        private static final List<Task> TASKS = new ArrayList<>();

        static {
            ServerTickEvents.END_SERVER_TICK.register(server -> {
                Iterator<Task> iterator = TASKS.iterator();

                while (iterator.hasNext()) {
                    Task t = iterator.next();

                    t.ticks--;
                    if (t.ticks <= 0) {
                        t.run.run();
                        iterator.remove();
                    }
                }
            });
        }

        public static void schedule(ServerWorld world, int delayTicks, Runnable run) {
            TASKS.add(new Task(delayTicks, run));
        }

        private static class Task {
            int ticks;
            final Runnable run;

            Task(int ticks, Runnable run) {
                this.ticks = ticks;
                this.run = run;
            }
        }
    }
}
