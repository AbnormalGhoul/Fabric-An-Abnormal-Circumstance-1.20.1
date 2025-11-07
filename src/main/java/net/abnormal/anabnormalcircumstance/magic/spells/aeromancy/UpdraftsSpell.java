package net.abnormal.anabnormalcircumstance.magic.spells.aeromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.List;

/**
 * Updrafts (Aeromancy, Tier 1)
 * Cost: 15 mana | Cooldown: 60 seconds
 * Levitation II (5s) for nearby ENEMY entities within 3 blocks.
 * Plays strong gust sound and spawns swirling air particles.
 */
public class UpdraftsSpell extends Spell {

    public UpdraftsSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.AEROMANCY, SpellTier.TIER_1, 15, 60, icon, "Updrafts");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Define 3-block radius box
        Vec3d pos = caster.getPos();
        Box area = new Box(pos.x - 3, pos.y - 3, pos.z - 3, pos.x + 3, pos.y + 3, pos.z + 3);

        // Get all nearby hostile entities (excluding self and allies)
        List<Entity> entities = world.getOtherEntities(caster, area,
                e -> e instanceof LivingEntity && e.isAlive() && isEnemy(caster, e));

        // Apply Levitation II (5 seconds) to enemies only
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 100, 1, false, true, true));
            }
        }

        // Play gust sound effect
        world.playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_PHANTOM_FLAP, SoundCategory.PLAYERS, 2.0f, 0.8f);
        world.playSound(null, caster.getBlockPos(), SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.PLAYERS, 1.0f, 1.8f);

        // Spawn visual updraft particles
        spawnUpdraftParticles(world, caster);

        return true;
    }

    private void spawnUpdraftParticles(ServerWorld world, ServerPlayerEntity caster) {
        Vec3d pos = caster.getPos().add(0, 1.0, 0);
        var random = world.getRandom();

        DustParticleEffect swirlEffect = new DustParticleEffect(new Vector3f(0.7f, 0.9f, 1.0f), 1.2f);

        // Swirling air around the caster
        for (int i = 0; i < 80; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double radius = 1.5 + random.nextDouble();
            double ox = Math.cos(angle) * radius;
            double oz = Math.sin(angle) * radius;
            double oy = random.nextDouble() * 2.5;

            world.spawnParticles(swirlEffect, pos.x + ox, pos.y + oy, pos.z + oz, 1, 0, 0, 0, 0.02);
        }

        // Add upward wind particles
        world.spawnParticles(ParticleTypes.CLOUD, pos.x, pos.y, pos.z, 40, 1.2, 1.0, 1.2, 0.05);
    }

    // Returns true if the target is an enemy (not an ally / teammate)
    private boolean isEnemy(ServerPlayerEntity caster, Entity target) {
        return !caster.isTeammate(target) && target instanceof LivingEntity;
    }
}
