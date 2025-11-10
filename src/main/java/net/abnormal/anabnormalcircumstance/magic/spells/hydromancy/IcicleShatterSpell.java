package net.abnormal.anabnormalcircumstance.magic.spells.hydromancy;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IcicleShatterSpell extends Spell {

    public IcicleShatterSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.HYDROMANCY, SpellTier.TIER_2, 30, 30, icon, "Icicle Shatter", "Creates a ring of sharp ice around you that damages and briefly freezes nearby enemies.");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Show floating icicles first
        spawnFloatingIcicles(world, caster);

        // Register a delayed shatter effect (after 10 ticks = 0.5s)
        DelayedTaskScheduler.schedule(world, 10, () -> shatterIcicles(world, caster));

        return true;
    }

    private void spawnFloatingIcicles(ServerWorld world, ServerPlayerEntity caster) {
        Vec3d pos = caster.getPos().add(0, 2.2, 0);
        double radius = 1.2;
        for (int i = 0; i < 3; i++) {
            double angle = (2 * Math.PI / 3) * i;
            double x = pos.x + radius * Math.cos(angle);
            double z = pos.z + radius * Math.sin(angle);
            double y = pos.y;

            world.spawnParticles(
                    new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.ICE.getDefaultState()),
                    x, y, z,
                    8, 0.05, 0.05, 0.05, 0.0
            );
        }

        world.playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_GLASS_HIT, SoundCategory.PLAYERS, 1.5f, 1.2f);
    }

    private void shatterIcicles(ServerWorld world, ServerPlayerEntity caster) {
        world.playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 2.0f, 0.9f);

        world.spawnParticles(
                new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.ICE.getDefaultState()),
                caster.getX(), caster.getY() + 1.2, caster.getZ(),
                60, 1.0, 1.0, 1.0, 0.1
        );

        double range = 5.0;
        Box area = new Box(
                caster.getX() - range, caster.getY() - range, caster.getZ() - range,
                caster.getX() + range, caster.getY() + range, caster.getZ() + range
        );

        List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, area,
                entity -> entity != caster && isEnemy(caster, entity));

        for (LivingEntity target : targets) {
            target.setFrozenTicks(target.getFrozenTicks() + 400);
            target.damage(world.getDamageSources().magic(), 15.0f);
        }
    }

    private boolean isEnemy(ServerPlayerEntity caster, Entity target) {
        if (caster.isTeammate(target)) return false;
        return target instanceof LivingEntity;
    }

//     Small inner static helper for delayed tick-based tasks.
    public static class DelayedTaskScheduler {
        private static final List<Task> TASKS = new ArrayList<>();

        static {
            // Register one listener to handle all delayed tasks
            ServerTickEvents.END_SERVER_TICK.register(server -> {
                Iterator<Task> iterator = TASKS.iterator();
                while (iterator.hasNext()) {
                    Task t = iterator.next();
                    t.ticks--; // we can now mutate ticks safely
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

        // Use a normal class instead of a record so 'ticks' can change
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
