package net.abnormal.anabnormalcircumstance.magic.spells.pyromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FireballSpell extends Spell {

    public FireballSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.PYROMANCY, SpellTier.TIER_3, 65, 60, icon,
                "Fireball",
                "Charges for a short time before launching a powerful fireball."
        );
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Apply a brief slowness effect
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 35, 4, false, false, true));

        // Play charging sound
        world.playSound(null, caster.getBlockPos(),
                SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.PLAYERS,
                1.0f, 1.2f);

        // Schedule 35 ticks of red charge particles
        for (int i = 0; i < 35; i++) {
            int delay = i;
            Delayed.schedule(world, delay, () -> {
                if (caster.isAlive()) {
                    world.spawnParticles(
                            new DustParticleEffect(new Vector3f(1.0f, 0.1f, 0.1f), 1.0f),
                            caster.getX(), caster.getBodyY(0.5), caster.getZ(),
                            12, 0.4, 0.7, 0.4, 0.01
                    );
                }
            });
        }

        // After 35 ticks, cast the fireball
        Delayed.schedule(world, 35, () -> {
            if (caster.isAlive()) {
                Vec3d look = caster.getRotationVec(1.0F);
                FireballEntity fireball = new FireballEntity(world, caster, look.x, look.y, look.z, 5);
                fireball.setPosition(caster.getX(), caster.getEyeY(), caster.getZ());
                world.spawnEntity(fireball);

                world.playSound(null, caster.getBlockPos(),
                        SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS,
                        2.0f, 1.0f);
            }
        });

        return true;
    }

    // Reuse the simple delayed task scheduler (same as your PyroclasmWaveSpell)
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
