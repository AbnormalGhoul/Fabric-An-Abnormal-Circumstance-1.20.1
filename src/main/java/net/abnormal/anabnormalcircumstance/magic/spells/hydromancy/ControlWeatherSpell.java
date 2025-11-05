package net.abnormal.anabnormalcircumstance.magic.spells.hydromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.LightningEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ControlWeatherSpell extends Spell {
    // Tracks which players currently have the lightning proc buff active (stores expiry tick)
    private static final Map<UUID, Long> ACTIVE_BUFFS = new HashMap<>();

    // Mana: 75, Cooldown: 30 minutes (1800 seconds)
    public ControlWeatherSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.HYDROMANCY, SpellTier.TIER_4, 75, 30 * 60, icon, "Control Weather");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Set thunderstorm (clearTime=0, rainTime=6000 ticks ~ 5 minutes)
        world.setWeather(0, 6000, true, true);

        // Play thunder sound and spawn stormy particles
        world.playSound(null, caster.getBlockPos(),
                SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                SoundCategory.WEATHER, 5.0f, 1.0f);

        spawnWeatherParticles(world, caster);

        // Activate 30-second lightning-proc buff (store expiry tick)
        ACTIVE_BUFFS.put(caster.getUuid(), world.getTime() + (30 * 20));

        return true;
    }

    private void spawnWeatherParticles(ServerWorld world, ServerPlayerEntity caster) {
        Vec3d pos = caster.getPos().add(0, 1.0, 0);
        var random = world.getRandom();

        DustParticleEffect swirl = new DustParticleEffect(new Vector3f(0.3f, 0.6f, 1.0f), 1.2f);
        for (int i = 0; i < 50; i++) {
            double ox = (random.nextDouble() - 0.5) * 2.0;
            double oy = random.nextDouble() * 2.0;
            double oz = (random.nextDouble() - 0.5) * 2.0;
            // spawnParticles(ServerPlayerEntity targetPlayer, ParticleEffect, longDistance, x,y,z,count,dx,dy,dz,speed)
            // Use the overload that sends to nearby clients; using caster ensures client sees it.
            world.spawnParticles(caster, swirl, true, pos.x + ox, pos.y + oy, pos.z + oz, 1, 0, 0, 0, 0);
        }
    }

//     Called when an entity is attacked.
//     NOTE: attacker is generic LivingEntity here so AttackEntityCallback (which passes PlayerEntity) compiles cleanly.
    public static void onEntityAttacked(LivingEntity attacker, LivingEntity target, DamageSource source) {
        // Only proceed if the attacker is a server-side player with the buff
        if (!(attacker instanceof ServerPlayerEntity player)) return;
        if (target == null || attacker.getWorld().isClient) return;

        ServerWorld world = (ServerWorld) player.getWorld();
        UUID id = player.getUuid();

        Long expire = ACTIVE_BUFFS.get(id);
        if (expire == null || world.getTime() > expire) {
            // buff expired / not present
            ACTIVE_BUFFS.remove(id);
            return;
        }

        // 50% chance to summon lightning on hit
        var random = world.getRandom();
        if (random.nextDouble() < 0.5) {
            // Create lightning via EntityType
            LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
            if (lightning != null) {
                // Position lightning at target and spawn it
                lightning.refreshPositionAfterTeleport(target.getX(), target.getY(), target.getZ());
                // Optionally mark the player as the channeler if your environment supports it:
                try {
                    lightning.setChanneler(player);
                } catch (Throwable ignored) {
                    // some mappings may not include setChanneler; ignore if absent
                }
                world.spawnEntity(lightning);
                world.playSound(null, target.getBlockPos(),
                        SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2.0f, 1.0f);
            }
        }
    }

    // Called each server tick for players — spawns aura particles while buff is active
    public static void tick(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        UUID uuid = player.getUuid();
        Long endTime = ACTIVE_BUFFS.get(uuid);

        if (endTime != null) {
            if (world.getTime() < endTime) {
                spawnAuraParticles(world, player);
            } else {
                ACTIVE_BUFFS.remove(uuid);
            }
        }
    }

    private static void spawnAuraParticles(ServerWorld world, ServerPlayerEntity player) {
        Vec3d pos = player.getPos().add(0, 1.0, 0);
        var random = world.getRandom();

        DustParticleEffect spark = new DustParticleEffect(new Vector3f(0.3f, 0.6f, 1.0f), 1.0f);
        for (int i = 0; i < 4; i++) {
            double ox = (random.nextDouble() - 0.5) * 0.8;
            double oy = random.nextDouble() * 1.2;
            double oz = (random.nextDouble() - 0.5) * 0.8;

            world.spawnParticles(player, spark, true, pos.x + ox, pos.y + oy, pos.z + oz, 1, 0, 0, 0, 0);
            world.spawnParticles(player, ParticleTypes.ELECTRIC_SPARK, true, pos.x + ox, pos.y + oy, pos.z + oz, 1, 0, 0, 0, 0);
        }
    }

    public static void clearBuff(ServerPlayerEntity player) {
        ACTIVE_BUFFS.remove(player.getUuid());
    }
}
