package net.abnormal.anabnormalcircumstance.magic.spells.pyromancy;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class PhoenixRebirthSpell extends Spell {

    public PhoenixRebirthSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.PYROMANCY, SpellTier.TIER_4,
                100,        // Mana cost
                10 * 60, // Cooldown (5 min)
                icon,
                "Phoenix Rebirth",
                "Embrace the flames of immortality, upon death you will rise again from the ashes. Also grants strength III.");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Apply Phoenix’s Fire (5 minutes = 6000 ticks, amplifier = 2 → 3 total lives)
        caster.addStatusEffect(new StatusEffectInstance(
                ModEffects.PHOENIX_FIRE,
                5 * 60 * 20,
                0,
                false, false, true
        ));

        caster.addStatusEffect(new StatusEffectInstance(
                StatusEffects.FIRE_RESISTANCE,
                5 * 60 * 20,
                0,
                false, false, true
        ));

        caster.addStatusEffect(new StatusEffectInstance(
                StatusEffects.STRENGTH,
                45 * 20,
                2,
                false, false, true
        ));

        // Casting effects
        world.playSound(
                null, caster.getBlockPos(),
                SoundEvents.ENTITY_WARDEN_DEATH,  // deep fiery sound
                SoundCategory.PLAYERS,
                1.5f, 0.8f
        );

        spawnActivationParticles(world, caster);
        return true;
    }

    private void spawnActivationParticles(ServerWorld world, ServerPlayerEntity caster) {
        // Fiery swirl burst
        world.spawnParticles(ParticleTypes.FLAME,
                caster.getX(), caster.getY() + 1, caster.getZ(),
                120, 0.6, 1.0, 0.6, 0.04);
        world.spawnParticles(ParticleTypes.LAVA,
                caster.getX(), caster.getY() + 0.5, caster.getZ(),
                40, 0.4, 0.4, 0.4, 0.08);
    }
}
