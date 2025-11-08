package net.abnormal.anabnormalcircumstance.magic.spells.geomancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.Blocks;

import java.util.*;

/**
 * Immovable Object (Geomancy, Tier 4)
 * Cost: 75 mana | Cooldown: 5 minutes (300s)
 * Grants Resistance X, Slowness II, Mining Fatigue II for 25 seconds.
 * Displays a subtle rocky aura for the duration.
 */
public class ImmovableObjectSpell extends Spell {

    // Duration (in ticks)
    private static final int DURATION = 25 * 20;

    // Active aura tracker
    private static final Map<UUID, Long> ACTIVE_AURAS = new HashMap<>();

    public ImmovableObjectSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.GEOMANCY, SpellTier.TIER_4, 75, 5 * 60, icon, "Immovable Object", "Locks your body into a fortified state, making you immune to damage but slowing all movement.");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // Apply effects
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, DURATION, 9, true, false, false)); // Resistance X
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, DURATION, 1, true, false, false));   // Slowness II
        caster.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, DURATION, 1, true, false, false)); // Mining Fatigue II

        // Play cast sound and spawn initial particles
        world.playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_STONE_PLACE, SoundCategory.PLAYERS, 6.0f, 0.8f);

        Vec3d pos = caster.getPos().add(0, 1.0, 0);
        world.spawnParticles(
                new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.STONE.getDefaultState()),
                pos.x, pos.y, pos.z,
                60, 1.0, 1.0, 1.0, 0.05
        );

        // Activate aura tracking
        ACTIVE_AURAS.put(caster.getUuid(), world.getTime() + DURATION);

        return true;
    }

    // Called each tick to render the rock aura while active.
    public static void tick(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        UUID id = player.getUuid();
        Long expire = ACTIVE_AURAS.get(id);

        if (expire == null) return;

        if (world.getTime() < expire) {
            spawnAuraParticles(world, player);
        } else {
            ACTIVE_AURAS.remove(id);
        }
    }

    private static void spawnAuraParticles(ServerWorld world, ServerPlayerEntity player) {
        Vec3d pos = player.getPos().add(0, 1.0, 0);
        var random = world.getRandom();

        // Use subtle rock particle effect
        BlockStateParticleEffect rock = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.STONE.getDefaultState());

        for (int i = 0; i < 4; i++) {
            double ox = (random.nextDouble() - 0.5) * 1.2;
            double oy = random.nextDouble() * 1.2;
            double oz = (random.nextDouble() - 0.5) * 1.2;
            world.spawnParticles(rock, pos.x + ox, pos.y + oy, pos.z + oz, 1, 0, 0, 0, 0);
        }

        // Occasional low rumble or grind sound (once every ~3s)
        if (world.getTime() % 60 == 0) {
            world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_STONE_HIT, SoundCategory.PLAYERS, 0.6f, 0.8f);
        }
    }

    // Clears all active aura data (e.g., on death or disconnect).
    public static void clearAura(ServerPlayerEntity player) {
        ACTIVE_AURAS.remove(player.getUuid());
    }

    // Static tick registration
    static {
        // Ensures aura updates every tick for all players
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                ImmovableObjectSpell.tick(player);
            }
        });
    }
}
