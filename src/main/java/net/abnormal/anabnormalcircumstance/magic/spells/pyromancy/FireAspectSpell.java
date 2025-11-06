package net.abnormal.anabnormalcircumstance.magic.spells.pyromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import net.minecraft.util.Identifier;

import java.util.*;

/**
 * Fire Aspect (Pyromancy, Tier 1)
 * Gives the caster 20s of flaming melee attacks.
 */
public class FireAspectSpell extends Spell {

//    Players who have the buff active → ticks remaining */
    private static final Map<UUID, Integer> ACTIVE = new HashMap<>();

    public FireAspectSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.PYROMANCY, SpellTier.TIER_1, 30, 25, icon, "Fire Aspect");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Apply buff for 20s = 400 ticks
        ACTIVE.put(caster.getUuid(), 400);

        // Sound
        world.playSound(
                null,
                caster.getBlockPos(),
                SoundEvents.ENTITY_BLAZE_SHOOT,
                SoundCategory.PLAYERS,
                2.0f,
                1.0f
        );

        // Flame burst particles
        spawnFlameBurst(world, caster);

        return true;
    }

    // Particle effect for spell cast
    private void spawnFlameBurst(ServerWorld world, ServerPlayerEntity caster) {

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

//    Called from ModEvents -> AttackEntityCallback
    public static void onEntityAttacked(ServerPlayerEntity attacker, LivingEntity target) {
        UUID id = attacker.getUuid();

        if (!ACTIVE.containsKey(id)) return;

        // Apply fire for 4s
        target.setOnFireFor(4);

        // Hit particles
        if (!attacker.getWorld().isClient()) {
            ServerWorld world = (ServerWorld) attacker.getWorld();
            world.spawnParticles(
                    ParticleTypes.FLAME,
                    target.getX(), target.getY() + 1, target.getZ(),
                    10,
                    0.2, 0.3, 0.2,
                    0.01
            );
        }
    }

//    Called from ModEvents -> ServerTickEvents.END_WORLD_TICK
    public static void tick(ServerPlayerEntity player) {
        UUID id = player.getUuid();

        if (!ACTIVE.containsKey(id)) return;

        int remaining = ACTIVE.get(id) - 1;

        if (remaining <= 0) {
            ACTIVE.remove(id);
            return;
        }

        ACTIVE.put(id, remaining);
    }
}
