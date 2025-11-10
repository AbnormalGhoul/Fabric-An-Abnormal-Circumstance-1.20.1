package net.abnormal.anabnormalcircumstance.magic.spells.aeromancy;

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

public class HurricanesCallSpell extends Spell {

    private static final int DURATION_TICKS = 10 * 20; // 15 seconds
    private static final double AURA_RADIUS = 3.0;
    private static final double KNOCKBACK_STRENGTH = 1.5;

    public HurricanesCallSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.AEROMANCY, SpellTier.TIER_3, 50, 90, icon, "Hurricane's Call", "Forms a small whirlwind around you that pushes enemies away.");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Activation sound
        world.playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.PLAYERS, 1.5f, 1.0f);

        // Start tracking the hurricane aura
        HurricaneTracker.startTracking(world, caster);

        return true;
    }

    private static class HurricaneTracker {
        private static final Map<UUID, Integer> tracked = new HashMap<>();

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
                        continue;
                    }

                    ServerWorld world = player.getServerWorld();

                    // Spawn cloud/air particles around the player (aura)
                    spawnAuraParticles(world, player);

                    // Launch enemy entities that get too close
                    applyAuraKnockback(world, player);

                    // Increment time
                    tickCount++;
                    if (tickCount >= DURATION_TICKS) {
                        it.remove();
                        continue;
                    }

                    tracked.put(uuid, tickCount);
                }
            });
        }

        static void startTracking(ServerWorld world, ServerPlayerEntity player) {
            tracked.put(player.getUuid(), 0);
        }

        private static void spawnAuraParticles(ServerWorld world, ServerPlayerEntity player) {
            Vec3d pos = player.getPos().add(0, 1.0, 0);
            for (int i = 0; i < 12; i++) {
                double angle = Math.random() * Math.PI * 2;
                double radius = AURA_RADIUS * Math.random();
                double x = pos.x + Math.cos(angle) * radius;
                double z = pos.z + Math.sin(angle) * radius;
                double y = pos.y + (Math.random() - 0.5) * 1.0;

                world.spawnParticles(
                        ParticleTypes.CLOUD,
                        x, y, z,
                        1,
                        0.0, 0.0, 0.0,
                        0.0
                );
            }
        }

        private static void applyAuraKnockback(ServerWorld world, ServerPlayerEntity caster) {
            Vec3d center = caster.getPos();
            Box area = new Box(
                    center.x - AURA_RADIUS, center.y - 1.0, center.z - AURA_RADIUS,
                    center.x + AURA_RADIUS, center.y + 2.0, center.z + AURA_RADIUS
            );

            List<Entity> targets = world.getOtherEntities(caster, area,
                    e -> e instanceof LivingEntity && !caster.isTeammate(e));

            for (Entity entity : targets) {
                Vec3d dir = entity.getPos().subtract(caster.getPos()).normalize().multiply(KNOCKBACK_STRENGTH);
                entity.addVelocity(dir.x, 0.5, dir.z);
                entity.velocityModified = true;
            }
        }
    }
}
