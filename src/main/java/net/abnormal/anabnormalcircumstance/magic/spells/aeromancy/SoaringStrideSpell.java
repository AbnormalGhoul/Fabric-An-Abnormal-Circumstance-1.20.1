package net.abnormal.anabnormalcircumstance.magic.spells.aeromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;

/**
 * Soaring Stride Spell:
 * Launches the caster upward with a burst of wind,
 * grants temporary immunity to fall damage (10s),
 * and plays wind-like visuals and sound.
 */
public class SoaringStrideSpell extends Spell {

    private static final int MANA_COST = 40;
    private static final int COOLDOWN_TICKS = 100; // 100 seconds
    private static final int FALL_IMMUNITY_TICKS = 20 * 10; // 10 seconds

    // Track which players are currently immune to fall damage
    private static final Map<ServerPlayerEntity, Integer> FALL_IMMUNITY = new HashMap<>();

    public SoaringStrideSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.AEROMANCY, SpellTier.TIER_2, MANA_COST, COOLDOWN_TICKS, icon, "Soaring Stride", "Launches you high into the air using a strong burst of wind.");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        // --- Launch player upwards ---
        Vec3d velocity = caster.getVelocity();
        caster.setVelocity(velocity.x, 2.5, velocity.z);
        caster.velocityModified = true;

        // --- Apply fall immunity ---
        FALL_IMMUNITY.put(caster, FALL_IMMUNITY_TICKS);

        // --- Play effects ---
        playCastEffects(world, caster);

        return true;
    }

    private void playCastEffects(ServerWorld world, ServerPlayerEntity caster) {
        BlockPos pos = caster.getBlockPos();

        // Sound: Wind gust
        world.playSound(null, pos, SoundEvents.ENTITY_PHANTOM_FLAP, SoundCategory.PLAYERS, 1.5f, 1.0f);

        // Particles: burst of clouds around feet
        for (int i = 0; i < 50; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 2.0;
            double offsetZ = (world.random.nextDouble() - 0.5) * 2.0;
            double velY = world.random.nextDouble() * 0.3 + 0.1;
            world.spawnParticles(
                    ParticleTypes.CLOUD,
                    caster.getX() + offsetX,
                    caster.getY(),
                    caster.getZ() + offsetZ,
                    1, 0, velY, 0, 0.05
            );
        }

        // Subtle gust particles near body
        world.spawnParticles(ParticleTypes.CLOUD,
                caster.getX(),
                caster.getY() + 0.5,
                caster.getZ(),
                15, 0.4, 0.4, 0.4, 0.1);
    }

    /**
     * Called every server tick to handle fall immunity countdown.
     */
    public static void tick(ServerPlayerEntity player) {
        if (FALL_IMMUNITY.containsKey(player)) {
            int ticksLeft = FALL_IMMUNITY.get(player);

            // Reset fall distance while immunity active
            if (!player.isOnGround() && player.getVelocity().y < 0 && player.fallDistance > 2.5F) {
                player.fallDistance = 0.0F;
            }

            // Decrease timer
            if (ticksLeft <= 0) {
                FALL_IMMUNITY.remove(player);
            } else {
                FALL_IMMUNITY.put(player, ticksLeft - 1);
            }
        }
    }

    /**
     * Utility called by global event handler to check and nullify fall damage.
     */
    public static boolean shouldCancelFallDamage(ServerPlayerEntity player) {
        return FALL_IMMUNITY.containsKey(player);
    }
}
