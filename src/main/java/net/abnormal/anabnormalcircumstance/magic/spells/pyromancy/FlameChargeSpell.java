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

public class FlameChargeSpell extends Spell {

    // Track active flame charges across players
    private static final Map<UUID, FlameChargeData> ACTIVE = new HashMap<>();

    public FlameChargeSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.PYROMANCY, SpellTier.TIER_2, 35, 45, icon, "Flame Charge", "Rushes you forward in a straight line, burning and damaging anything you collide with.");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // If already charging, do nothing
        if (ACTIVE.containsKey(caster.getUuid())) return false;

        // Calculate dash direction
        Vec3d direction = caster.getRotationVector().normalize().multiply(1.5); // dash speed

        // Store dash data
        ACTIVE.put(caster.getUuid(), new FlameChargeData(
                10,                 // dash for 10 ticks (~0.5 seconds)
                direction,
                new HashSet<>()
        ));

        // Play unique fire sound
        world.playSound(null, caster.getBlockPos(),
                SoundEvents.ITEM_FIRECHARGE_USE, // different sound than blaze shoot
                SoundCategory.PLAYERS,
                1.6f, 1.0f
        );

        return true;
    }
    static {
        ServerTickEvents.END_SERVER_TICK.register(server -> {

            Iterator<Map.Entry<UUID, FlameChargeData>> it = ACTIVE.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<UUID, FlameChargeData> entry = it.next();
                UUID playerId = entry.getKey();
                FlameChargeData data = entry.getValue();

                ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
                if (player == null || !player.isAlive()) {
                    it.remove();
                    continue;
                }

                ServerWorld world = player.getServerWorld();

                // Apply velocity each tick (server controlled dash)
                player.setVelocity(data.velocity);
                player.velocityModified = true;

                spawnDashParticles(world, player, data.velocity);

                // Damage enemies during collision
                damageEnemiesDuringCharge(world, player, data);

                // countdown
                data.remainingTicks--;
                if (data.remainingTicks <= 0) {
                    it.remove();
                }
            }
        });
    }

    private static void damageEnemiesDuringCharge(ServerWorld world, ServerPlayerEntity caster, FlameChargeData data) {

        Box hitbox = caster.getBoundingBox().expand(0.6);

        List<LivingEntity> targets = world.getEntitiesByClass(
                LivingEntity.class,
                hitbox,
                e -> e != caster && !caster.isTeammate(e)
        );

        for (LivingEntity target : targets) {

            // Prevent double-damage in same dash
            if (data.hit.contains(target.getUuid())) continue;

            data.hit.add(target.getUuid());

            target.damage(world.getDamageSources().playerAttack(caster), 20.0f);
            target.setOnFireFor(5);
        }
    }

    /* ============================================================
       PARTICLE EFFECTS
       ============================================================ */

    private static void spawnDashParticles(ServerWorld world, ServerPlayerEntity caster, Vec3d dir) {

        Vec3d pos = caster.getPos().add(0, 1, 0);

        // Particles behind the player (opposite dash direction)
        Vec3d back = pos.subtract(dir.normalize().multiply(0.5));

        world.spawnParticles(
                ParticleTypes.FLAME,
                back.x, back.y, back.z,
                6,
                0.1, 0.1, 0.1,
                0.05
        );

        world.spawnParticles(
                ParticleTypes.SMOKE,
                back.x, back.y, back.z,
                4,
                0.1, 0.1, 0.1,
                0.02
        );
    }
    

    private static class FlameChargeData {
        int remainingTicks;
        final Vec3d velocity;
        final Set<UUID> hit;

        FlameChargeData(int ticks, Vec3d vel, Set<UUID> hitList) {
            this.remainingTicks = ticks;
            this.velocity = vel;
            this.hit = hitList;
        }
    }
}
