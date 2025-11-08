package net.abnormal.anabnormalcircumstance.magic.spells.aeromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

/**
 * Light as a Feather (Aeromancy, Tier 1)
 * Cost: 5 mana | Cooldown: 20 seconds
 * Grants Slow Falling for 15 seconds.
 * Plays airy sound and spawns feather-like particles.
 */
public class LightAsAFeatherSpell extends Spell {

    public LightAsAFeatherSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.AEROMANCY, SpellTier.TIER_1, 5, 20, icon, "Light as a Feather", "Reduces your falling speed, letting you glide safely downward.");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Apply Slow Falling for 15 seconds (15 * 20 ticks)
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 15 * 20, 0, false, false, true));

        // Play a light airy sound
        world.playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_PHANTOM_FLAP, SoundCategory.PLAYERS, 1.5f, 1.4f);

        // Spawn feather-like particles around player
        spawnFeatherParticles(world, caster);

        return true;
    }

    private void spawnFeatherParticles(ServerWorld world, ServerPlayerEntity caster) {
        Vec3d pos = caster.getPos().add(0, 1.2, 0);
        var random = world.getRandom();

        // Light pastel blue swirl (air aura)
        DustParticleEffect airEffect = new DustParticleEffect(new Vector3f(0.8f, 0.9f, 1.0f), 1.0f);

        for (int i = 0; i < 40; i++) {
            double ox = (random.nextDouble() - 0.5) * 1.2;
            double oy = random.nextDouble() * 1.5;
            double oz = (random.nextDouble() - 0.5) * 1.2;

            world.spawnParticles(airEffect, pos.x + ox, pos.y + oy, pos.z + oz, 1, 0, 0, 0, 0);
        }

        // Add a few cloud particles for softness
        world.spawnParticles(ParticleTypes.CLOUD, pos.x, pos.y, pos.z, 20, 0.5, 0.5, 0.5, 0.01);
    }
}
