package net.abnormal.anabnormalcircumstance.magic.spells.aeromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class StormlordsWillSpell extends Spell {

    private static final int DURATION_TICKS = 35 * 20;       // Spell duration: 35s
    private static final int FALL_IMMUNITY_TICKS = 45 * 20;  // Fall immunity: 45s

    // Tracks players currently affected by the spell
    private static final Map<UUID, Integer> activePlayers = new HashMap<>();
    private static final Map<UUID, Boolean> jumpPending = new HashMap<>(); // triggered on jump key press

    private static final Map<UUID, Integer> postFallImmunity = new HashMap<>();
    private static final int POST_IMMUNITY_TICKS = 10 * 20; // 10 seconds

    private static boolean tickRegistered = false;

    public StormlordsWillSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.AEROMANCY, SpellTier.TIER_4, 65, 600, icon, "Stormlord's Will", "Allows you to perform unlimited midair jumps and negates fall damage for the duration.");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();

        activePlayers.put(caster.getUuid(), 0);
        jumpPending.put(caster.getUuid(), false);

        // Play spell activation sound
        world.playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 2.0f, 1.0f);

        // Spawn initial particles
        spawnAirParticles(world, caster);

        // Register server tick handler
        registerTickHandler();

        return true;
    }

    // Spawn subtle swirling cloud particles around the player
    private void spawnAirParticles(ServerWorld world, ServerPlayerEntity caster) {
        Vec3d pos = caster.getPos().add(0, 1.0, 0);
        var random = world.getRandom();

        for (int i = 0; i < 10; i++) { // reduced for subtlety
            double angle = random.nextDouble() * 2 * Math.PI;
            double radius = 0.3 + random.nextDouble() * 0.3;
            double ox = Math.cos(angle) * radius;
            double oz = Math.sin(angle) * radius;
            double oy = random.nextDouble() * 1.0;

            world.spawnParticles(ParticleTypes.CLOUD, pos.x + ox, pos.y + oy, pos.z + oz, 1, 0, 0.01, 0, 0.005);
        }
    }

    // Register the server tick handler for spell effects
    private void registerTickHandler() {
        if (tickRegistered) return;
        tickRegistered = true;

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // Handle active spell players
            Iterator<Map.Entry<UUID, Integer>> iterator = activePlayers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID, Integer> entry = iterator.next();
                UUID uuid = entry.getKey();
                int ticks = entry.getValue();

                ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                if (player == null || player.isRemoved() || !player.isAlive()) {
                    iterator.remove();
                    jumpPending.remove(uuid);
                    postFallImmunity.remove(uuid);
                    continue;
                }

                // Fall damage immunity while spell is active
                if (ticks < FALL_IMMUNITY_TICKS) player.fallDistance = 0;

                // Subtle ongoing particle effect
                if (ticks < DURATION_TICKS) spawnAirParticles(player.getServerWorld(), player);

                // Handle air jumps
                handleAirJump(player);

                // Increment tick count
                entry.setValue(ticks + 1);

                // Spell ends
                if (ticks >= DURATION_TICKS) {
                    iterator.remove();
                    jumpPending.remove(uuid);
                    // Start post-spell fall immunity
                    postFallImmunity.put(uuid, 0);
                }
            }

            // Handle post-spell fall immunity (grace period)
            Iterator<Map.Entry<UUID, Integer>> postIt = postFallImmunity.entrySet().iterator();
            while (postIt.hasNext()) {
                Map.Entry<UUID, Integer> entry = postIt.next();
                UUID uuid = entry.getKey();
                int ticks = entry.getValue();

                ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                if (player == null || player.isRemoved() || !player.isAlive()) {
                    postIt.remove();
                    continue;
                }

                // Keep fall damage reset during grace period
                player.fallDistance = 0;

                // Increment grace period counter
                entry.setValue(ticks + 1);
                if (ticks + 1 >= POST_IMMUNITY_TICKS) {
                    postIt.remove();
                }
            }
        });
    }


    /**
     * Handles server-side air-jump logic
     * Adds stronger dash in the direction player is looking
     */
    private void handleAirJump(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();

        if (player.isOnGround()) {
            jumpPending.put(uuid, false);
            return;
        }

        if (jumpPending.getOrDefault(uuid, false)) {
            Vec3d look = player.getRotationVec(1.0f); // direction player is looking

            double horizontalSpeed = 0.6; // stronger horizontal dash
            double verticalBoost = 0.6;   // stronger vertical boost

            player.setVelocity(
                    look.x * horizontalSpeed,
                    verticalBoost,
                    look.z * horizontalSpeed
            );
            player.velocityModified = true;

            // Subtle particle effect
            player.getServerWorld().spawnParticles(
                    ParticleTypes.CLOUD,
                    player.getX(), player.getY() + 0.1, player.getZ(),
                    1, 0.05, 0.05, 0.05, 0.005
            );

            jumpPending.put(uuid, false);
        }
    }


    // Called from client packet when jump key is pressed
    public static void requestAirJump(ServerPlayerEntity player) {
        UUID uuid = player.getUuid();
        if (activePlayers.containsKey(uuid)) jumpPending.put(uuid, true);
    }
}
