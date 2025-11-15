package net.abnormal.anabnormalcircumstance.magic.spells.pyromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;

import net.minecraft.entity.Entity;
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

import java.util.List;

public class BloodPactSpell extends Spell {

    public BloodPactSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.PYROMANCY, SpellTier.TIER_2, 65,60, icon, "Blood Pact", "Sacrifice 6 hearts to gain Strength III for 20 seconds. ");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Must have > 4 hearts to sacrifice
        if (caster.getHealth() <= 4.0f) {
            caster.sendMessage(net.minecraft.text.Text.literal(
                    "§cYou are too weak to perform a Blood Pact."), true);
            return false;
        }

        // Sacrifice 4 hearts (8 HP)
        caster.damage(world.getDamageSources().outOfWorld(), 8.0f);

        // Sounds
        world.playSound(
                null, caster.getBlockPos(),
                SoundEvents.ENTITY_BLAZE_SHOOT,
                SoundCategory.PLAYERS,
                1.8f, 0.8f
        );
        world.playSound(
                null, caster.getBlockPos(),
                SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,
                SoundCategory.PLAYERS,
                1.2f, 1.4f
        );

        // Blood/fire swirl particles
        spawnBloodPactParticles(world, caster);

        // Apply Strength III (amplifier = 2)
        StatusEffectInstance strength =
                new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 20, 2, false, true, true);

        // Apply to self
        caster.addStatusEffect(strength);

        // Apply to allies within 3 blocks
        double radius = 3.0;
        Vec3d pos = caster.getPos();

        Box area = new Box(
                pos.x - radius, pos.y - radius, pos.z - radius,
                pos.x + radius, pos.y + radius, pos.z + radius
        );

        List<LivingEntity> nearby = world.getEntitiesByClass(
                LivingEntity.class, area,
                entity -> entity != caster && caster.isTeammate(entity)
        );

        for (LivingEntity ally : nearby) {
            ally.addStatusEffect(new StatusEffectInstance(strength));
        }

        return true;
    }

    private void spawnBloodPactParticles(ServerWorld world, ServerPlayerEntity caster) {
        Vec3d pos = caster.getPos().add(0, 1.0, 0);

        // Flame burst
        world.spawnParticles(
                ParticleTypes.FLAME,
                pos.x, pos.y, pos.z,
                60,
                0.5, 0.8, 0.5,
                0.04
        );

        // Crimson-colored soul fire sparks (visual contrast)
        world.spawnParticles(
                ParticleTypes.SOUL_FIRE_FLAME,
                pos.x, pos.y - 0.2, pos.z,
                30,
                0.4, 0.3, 0.4,
                0.01
        );

        // Blood-like drips (lava particles)
        world.spawnParticles(
                ParticleTypes.DRIPPING_LAVA,
                pos.x, pos.y + 0.2, pos.z,
                20,
                0.25, 0.5, 0.25,
                0.02
        );
    }
}
