package net.abnormal.anabnormalcircumstance.magic.spells.geomancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;

import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

import net.minecraft.util.Identifier;

public class QuickClawsSpell extends Spell {

    public QuickClawsSpell(Identifier id, Identifier icon) {
        super(
                id,
                SpellElement.GEOMANCY,
                SpellTier.TIER_1,
                25,             // mana
                45,             // cooldown seconds
                icon,
                "Quick Claws",
                "Boosts your mining and action speed for a short period."
        );
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Apply Haste II (amplifier 1) for 25 sec
        caster.addStatusEffect(new StatusEffectInstance(
                StatusEffects.HASTE,
                25 * 20,     // 25 seconds
                1,           // amplifier = Haste II
                false,
                false,
                true
        ));

        // Sound
        world.playSound(
                null,
                caster.getBlockPos(),
                SoundEvents.BLOCK_GRAVEL_BREAK,
                SoundCategory.PLAYERS,
                5f,
                0.8f
        );

        // Particle burst (earthy claw-like spray)
        spawnActivationParticles(world, caster);

        return true;
    }

    private void spawnActivationParticles(ServerWorld world, ServerPlayerEntity caster) {
        double x = caster.getX();
        double y = caster.getY() + 1.0;
        double z = caster.getZ();

        // Stone chips burst
        world.spawnParticles(
                new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.STONE.getDefaultState()),
                x, y, z,
                40,
                0.6, 0.4, 0.6,
                0.12
        );

        // Claw-like streaks (crit particles look nice for “slashing energy”)
        world.spawnParticles(
                ParticleTypes.CRIT,
                x, y, z,
                20,
                0.3, 0.4, 0.3,
                0.15
        );
    }
}
