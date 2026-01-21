package net.abnormal.anabnormalcircumstance.magic.spells.aeromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class GaleWindsSpell extends Spell {

    private static final int DASH_TICKS = 8;
    private static final double KNOCKBACK_STRENGTH = 6;
    private static final double KNOCKBACK_UP = 0.6;
    private static final int SLOW_FALLING_TICKS = 300; // 15 seconds

    private static final Map<UUID, GaleDashData> ACTIVE = new HashMap<>();

    private static final double DASH_STRENGTH = 2.8; // Forward push multiplier
    private static final double VERTICAL_BOOST = 0.3; // Slight lift to feel airy
    private static final int PARTICLE_COUNT = 50;

    public GaleWindsSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.AEROMANCY, SpellTier.TIER_3, 20, 30, icon, "Gale Winds", "Dash forward with the power of gale winds, leaving a trail of swirling air.");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        if (ACTIVE.containsKey(caster.getUuid())) return false;

        Vec3d look = caster.getRotationVector().normalize();

        Vec3d dashVelocity = new Vec3d(
                look.x * DASH_STRENGTH,
                look.y * 0.4 + VERTICAL_BOOST,
                look.z * DASH_STRENGTH
        );

        ACTIVE.put(caster.getUuid(), new GaleDashData(DASH_TICKS, dashVelocity));

        caster.getServerWorld().playSound(
                null,
                caster.getBlockPos(),
                SoundEvents.ENTITY_ENDER_DRAGON_FLAP,
                SoundCategory.PLAYERS,
                1.5f,
                1.0f
        );

        return true;
    }
    static {
        ServerTickEvents.END_SERVER_TICK.register(server -> {

            Iterator<Map.Entry<UUID, GaleDashData>> it = ACTIVE.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<UUID, GaleDashData> entry = it.next();
                UUID id = entry.getKey();
                GaleDashData data = entry.getValue();

                ServerPlayerEntity player = server.getPlayerManager().getPlayer(id);
                if (player == null || !player.isAlive()) {
                    it.remove();
                    continue;
                }

                ServerWorld world = player.getServerWorld();

                // Apply dash velocity
                player.setVelocity(data.velocity);
                player.velocityModified = true;

                // Spawn trail
                spawnWindTrail(world, player);

                // Launch entities in path
                launchEntities(world, player, data);

                data.ticks--;
                if (data.ticks <= 0) {
                    it.remove();
                }
            }
        });
    }

    private static void launchEntities(ServerWorld world, ServerPlayerEntity caster, GaleDashData data) {

        Box box = caster.getBoundingBox().expand(1.2, 0.8, 1.2);

        List<LivingEntity> entities = world.getEntitiesByClass(
                LivingEntity.class,
                box,
                e -> e != caster && !caster.isTeammate(e)
        );

        for (LivingEntity target : entities) {

            if (data.hit.contains(target.getUuid())) continue;
            data.hit.add(target.getUuid());

            Vec3d pushDir = target.getPos()
                    .subtract(caster.getPos())
                    .normalize();

            Vec3d knockback = new Vec3d(
                    pushDir.x * KNOCKBACK_STRENGTH,
                    KNOCKBACK_UP,
                    pushDir.z * KNOCKBACK_STRENGTH
            );

            target.addVelocity(knockback.x, knockback.y, knockback.z);
            target.velocityModified = true;

            target.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SLOW_FALLING,
                    SLOW_FALLING_TICKS,
                    0,
                    false,
                    true
            ));
        }
    }

    private static void spawnWindTrail(ServerWorld world, ServerPlayerEntity caster) {
        Vec3d pos = caster.getPos();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double angle = Math.random() * Math.PI * 2;
            double radius = Math.random() * 1.5;
            double height = Math.random() * 1.5;
            double x = pos.x + Math.cos(angle) * radius;
            double z = pos.z + Math.sin(angle) * radius;
            double y = pos.y + 0.5 + height * 0.5;

            world.spawnParticles(
                    ParticleTypes.CLOUD,
                    x,
                    y,
                    z,
                    1,
                    0.0,
                    0.0,
                    0.0,
                    0.0
            );
        }
    }

    private static class GaleDashData {
        int ticks;
        Vec3d velocity;
        Set<UUID> hit = new HashSet<>();

        GaleDashData(int ticks, Vec3d velocity) {
            this.ticks = ticks;
            this.velocity = velocity;
        }
    }

}
