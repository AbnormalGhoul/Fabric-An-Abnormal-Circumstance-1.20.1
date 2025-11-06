package net.abnormal.anabnormalcircumstance.magic.spells.pyromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.particle.ParticleTypes;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MoltenFurySpell extends Spell {

    // Track active players and remaining duration (ticks)
    private static final Map<UUID, Integer> ACTIVE = new HashMap<>();

    // Track fire-on-hit duration (ticks)
    private static final Map<UUID, Integer> FIRE_ON_HIT = new HashMap<>();

    public MoltenFurySpell(Identifier id, Identifier icon) {
        super(id, SpellElement.PYROMANCY, SpellTier.TIER_4,
                100,   // mana
                900,   // cooldown = 15 minutes
                icon,
                "Molten Fury");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // 30-second main buff
        ACTIVE.put(caster.getUuid(), 30 * 20);

        // 30 seconds of fire-on-hit
        FIRE_ON_HIT.put(caster.getUuid(), 30 * 20);

        // 1 minute of fire resistance
        caster.addStatusEffect(new StatusEffectInstance(
                StatusEffects.FIRE_RESISTANCE,
                60 * 20,
                0,
                false, false, true
        ));

        // Sound
        world.playSound(
                null, caster.getBlockPos(),
                SoundEvents.ENTITY_BLAZE_SHOOT,
                SoundCategory.PLAYERS,
                2.0f, 0.6f
        );

        // Initial burst
        spawnActivationParticles(world, caster);

        return true;
    }

    public static void tick(ServerPlayerEntity player) {
        UUID id = player.getUuid();

        // Buff timer
        if (ACTIVE.containsKey(id)) {
            int t = ACTIVE.get(id) - 1;
            if (t <= 0) ACTIVE.remove(id);
            else {
                ACTIVE.put(id, t);
                spawnAuraParticles((ServerWorld) player.getWorld(), player);
            }
        }

        // Fire-on-hit timer
        if (FIRE_ON_HIT.containsKey(id)) {
            int ft = FIRE_ON_HIT.get(id) - 1;
            if (ft <= 0) FIRE_ON_HIT.remove(id);
            else FIRE_ON_HIT.put(id, ft);
        }
    }

    public static boolean hasBuff(ServerPlayerEntity player) {
        return ACTIVE.containsKey(player.getUuid());
    }

    public static boolean hasFireHit(ServerPlayerEntity player) {
        return FIRE_ON_HIT.containsKey(player.getUuid());
    }

    public static float applyDamageBoost(ServerPlayerEntity player, float baseDamage) {
        if (!hasBuff(player)) return baseDamage;
        return baseDamage * 1.5f; // +50%
    }


    // PARTICLES (subtle aura already included)

    private void spawnActivationParticles(ServerWorld world, ServerPlayerEntity caster) {
        world.spawnParticles(ParticleTypes.FLAME,
                caster.getX(), caster.getY() + 1, caster.getZ(),
                80, 0.6, 0.8, 0.6, 0.04);

        world.spawnParticles(ParticleTypes.LAVA,
                caster.getX(), caster.getY() + 1, caster.getZ(),
                20, 0.4, 0.4, 0.4, 0.06);
    }

    private static void spawnAuraParticles(ServerWorld world, ServerPlayerEntity caster) {
        double x = caster.getX();
        double y = caster.getY() + 1.0;
        double z = caster.getZ();

        if (world.random.nextFloat() < 0.60f) {
            world.spawnParticles(
                    ParticleTypes.FLAME,
                    x, y, z,
                    2, 0.12, 0.18, 0.12, 0.004
            );
        }

        if (world.random.nextFloat() < 0.10f) {
            world.spawnParticles(
                    ParticleTypes.SMALL_FLAME,
                    x, y, z,
                    1, 0.10, 0.15, 0.10, 0.006
            );
        }
    }
}
