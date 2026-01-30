package net.abnormal.anabnormalcircumstance.magic.spells.pyromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import net.minecraft.util.Identifier;

import java.util.*;

public class FireAspectSpell extends Spell {
    private static final Map<UUID, Integer> ACTIVE = new HashMap<>();
    private static final Set<UUID> FIRE_ARROWS = new HashSet<>();

    public FireAspectSpell(Identifier id, Identifier icon) {
        super(
                id,
                SpellElement.PYROMANCY,
                SpellTier.TIER_1,
                30,
                25,
                icon,
                "Fire Aspect",
                "Sets your melee attacks and arrows ablaze, burning enemies for a short duration."
        );
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Apply buff for 20 seconds (400 ticks)
        ACTIVE.put(caster.getUuid(), 400);

        // Sound feedback
        world.playSound(
                null,
                caster.getBlockPos(),
                SoundEvents.ENTITY_BLAZE_SHOOT,
                SoundCategory.PLAYERS,
                2.0f,
                1.0f
        );

        // Visual burst
        spawnFlameBurst(world, caster);

        return true;
    }

    // Called from ModEvents
    public static void onEntityAttacked(ServerPlayerEntity attacker, LivingEntity target) {
        if (!ACTIVE.containsKey(attacker.getUuid())) return;

        // Ignite for 4 seconds
        target.setOnFireFor(4);

        if (attacker.getWorld() instanceof ServerWorld world) {
            world.spawnParticles(
                    ParticleTypes.FLAME,
                    target.getX(),
                    target.getY() + 1.0,
                    target.getZ(),
                    10,
                    0.2, 0.3, 0.2,
                    0.01
            );
        }
    }

     // Called when a projectile is fired by a player
     public static void onArrowFired(ServerPlayerEntity shooter, Entity projectile) {
         if (!ACTIVE.containsKey(shooter.getUuid())) return;
         if (!(projectile instanceof PersistentProjectileEntity arrow)) return;

         // fire arrow
         arrow.setOnFireFor(100);

         FIRE_ARROWS.add(arrow.getUuid());

         if (shooter.getWorld() instanceof ServerWorld world) {
             world.spawnParticles(
                     ParticleTypes.FLAME,
                     arrow.getX(),
                     arrow.getY(),
                     arrow.getZ(),
                     5,
                     0.05, 0.05, 0.05,
                     0.01
             );
         }
     }


    // Called when a living entity is damaged by a projectile
    public static void onArrowHit(Entity projectile, LivingEntity target) {
        UUID arrowId = projectile.getUuid();

        if (!FIRE_ARROWS.contains(arrowId)) return;

        target.setOnFireFor(4);

        if (target.getWorld() instanceof ServerWorld world) {
            world.spawnParticles(
                    ParticleTypes.FLAME,
                    target.getX(),
                    target.getY() + 1.0,
                    target.getZ(),
                    12,
                    0.2, 0.3, 0.2,
                    0.01
            );
        }

        // Cleanup immediately after hit
        FIRE_ARROWS.remove(arrowId);
    }

    // Called every tick for each player
    public static void tick(ServerPlayerEntity player) {
        UUID id = player.getUuid();

        Integer remaining = ACTIVE.get(id);
        if (remaining == null) return;

        remaining--;

        if (remaining <= 0) {
            ACTIVE.remove(id);
        } else {
            ACTIVE.put(id, remaining);
        }
    }

    // Safety cleanup
    public static void cleanup(ServerWorld world) {
        FIRE_ARROWS.removeIf(uuid -> world.getEntity(uuid) == null);
    }

    // Visual flame burst around caster
    private static void spawnFlameBurst(ServerWorld world, ServerPlayerEntity caster) {
        for (int i = 0; i < 60; i++) {
            double angle = (Math.PI * 2) * (i / 60.0);
            double radius = 1.0;

            double x = caster.getX() + Math.cos(angle) * radius;
            double z = caster.getZ() + Math.sin(angle) * radius;
            double y = caster.getY() + 1.0;

            world.spawnParticles(
                    ParticleTypes.FLAME,
                    x, y, z,
                    1,
                    0.1, 0.1, 0.1,
                    0.01
            );
        }
    }
}
