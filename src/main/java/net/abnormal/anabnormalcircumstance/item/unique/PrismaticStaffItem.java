package net.abnormal.anabnormalcircumstance.item.unique;

import net.abnormal.anabnormalcircumstance.item.util.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.util.UniqueItemCooldownManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PrismaticStaffItem extends Item implements UniqueAbilityItem {

    private static final double RADIUS = 8.0;
    private static final int AURA_DURATION = 20 * 20;   // 20s
    private static final int REGEN_DURATION = 4 * 20;   // 4s
    private static final long COOLDOWN_MS = 60 * 1000;

    // Aura Tracking
    private static final CopyOnWriteArrayList<AuraInstance> ACTIVE_AURAS = new CopyOnWriteArrayList<>();

    static {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            List<AuraInstance> toRemove = new ArrayList<>();

            for (AuraInstance aura : ACTIVE_AURAS) {
                aura.ticks++;

                PlayerEntity owner = server.getPlayerManager().getPlayer(aura.ownerId);
                if (owner == null || owner.isRemoved()) {
                    toRemove.add(aura);
                    continue;
                }

                if (aura.ticks % 10 == 0) {
                    aura.applyRegen(owner);
                }
                aura.spawnParticles();

                if (aura.ticks >= AURA_DURATION) {
                    toRemove.add(aura);
                }
            }

            ACTIVE_AURAS.removeAll(toRemove);
        });
    }

    public PrismaticStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public void useUniqueAbility(PlayerEntity player) {
        World world = player.getWorld();
        if (world.isClient()) return;

        if (UniqueItemCooldownManager.isOnCooldown(player)) {
            long remaining = UniqueItemCooldownManager.getRemaining(player);
            player.sendMessage(
                    Text.literal("Ability Cooldown (" + remaining / 1000 + "s)")
                            .formatted(Formatting.WHITE),
                    true
            );
            return;
        }

        ServerWorld serverWorld = (ServerWorld) world;

        player.sendMessage(
                Text.literal("Prismatic Aura!")
                        .formatted(Formatting.GOLD),
                true
        );

        serverWorld.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE,
                SoundCategory.PLAYERS,
                2.0f,
                1.2f
        );

        // Prevent duplicate aura per player
        ACTIVE_AURAS.removeIf(a -> a.ownerId.equals(player.getUuid()));

        ACTIVE_AURAS.add(new AuraInstance(serverWorld, player.getUuid(), player.getPos()));

        player.sendMessage(
                Text.literal("Prismatic energies bloom around you.")
                        .formatted(Formatting.AQUA),
                true
        );

        UniqueItemCooldownManager.setCooldown(player, COOLDOWN_MS);
    }

    private static class AuraInstance {
        final ServerWorld world;
        final UUID ownerId;
        final Vec3d center;
        int ticks = 0;

        AuraInstance(ServerWorld world, UUID ownerId, Vec3d center) {
            this.world = world;
            this.ownerId = ownerId;
            this.center = center;
        }

        void applyRegen(PlayerEntity owner) {
            Box area = new Box(
                    center.x - RADIUS, center.y - RADIUS, center.z - RADIUS,
                    center.x + RADIUS, center.y + RADIUS, center.z + RADIUS
            );

            List<LivingEntity> allies = world.getEntitiesByClass(
                    LivingEntity.class,
                    area,
                    e -> e.isAlive() && (e == owner || owner.isTeammate(e))
            );

            for (LivingEntity ally : allies) {
                StatusEffectInstance current =
                        ally.getStatusEffect(StatusEffects.REGENERATION);

                if (current == null || current.getDuration() <= 10) {
                    ally.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.REGENERATION,
                            REGEN_DURATION, // 4 seconds
                            2,              // Regen III
                            true,
                            false,
                            true
                    ));
                }
            }
        }

        void spawnParticles() {
            spawnGroundRing();
            spawnParticleDome();
        }

        private void spawnGroundRing() {
            int points = 80;

            for (int i = 0; i < points; i++) {
                double angle = (2 * Math.PI / points) * i;
                double x = center.x + Math.cos(angle) * RADIUS;
                double z = center.z + Math.sin(angle) * RADIUS;

                world.spawnParticles(
                        ParticleTypes.END_ROD,
                        x, center.y + 0.05, z,
                        1, 0, 0, 0, 0
                );

                world.spawnParticles(
                        new BlockStateParticleEffect(
                                ParticleTypes.FALLING_DUST,
                                Blocks.AMETHYST_BLOCK.getDefaultState()
                        ),
                        x, center.y + 0.05, z,
                        1, 0, 0, 0, 0
                );
            }
        }

        private void spawnParticleDome() {
            int rings = 8;
            int pointsPerRing = 32;

            for (int r = 0; r <= rings; r++) {
                double height = (r / (double) rings) * RADIUS;
                double ringRadius = Math.sqrt(RADIUS * RADIUS - height * height);

                for (int i = 0; i < pointsPerRing; i++) {
                    double angle = (2 * Math.PI / pointsPerRing) * i;
                    double x = center.x + Math.cos(angle) * ringRadius;
                    double z = center.z + Math.sin(angle) * ringRadius;

                    world.spawnParticles(
                            ParticleTypes.END_ROD,
                            x, center.y + height, z,
                            1, 0, 0, 0, 0
                    );
                }
            }
        }
    }

    // Tooltip
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Active: Create a prismatic healing aura for 1 minute").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 1 minute").formatted(Formatting.GRAY));
    }
}
