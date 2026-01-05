package net.abnormal.anabnormalcircumstance.item.unique;

import net.abnormal.anabnormalcircumstance.item.util.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.util.UniqueItemCooldownManager;
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

import java.util.List;

public class PrismaticStaffItem extends Item implements UniqueAbilityItem {

    private static final double RADIUS = 8.0;
    private static final int DURATION_TICKS = 20 * 20; // 20s
    private static final long COOLDOWN_MS = 60 * 1000;

    public PrismaticStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public void useUniqueAbility(PlayerEntity player) {
        World world = player.getWorld();

        if (world.isClient()) return;

        // Cooldown check
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
        Vec3d center = player.getPos();

        // Sound (crystalline / prismatic feel)
        serverWorld.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE,
                SoundCategory.PLAYERS,
                2.0f,
                1.2f
        );

        // Apply Regen III to allies in radius
        Box area = new Box(
                center.x - RADIUS, center.y - RADIUS, center.z - RADIUS,
                center.x + RADIUS, center.y + RADIUS, center.z + RADIUS
        );

        List<LivingEntity> targets = serverWorld.getEntitiesByClass(
                LivingEntity.class,
                area,
                entity -> entity.isAlive() && (player.isTeammate(entity) || entity == player)
        );

        for (LivingEntity target : targets) {
            target.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.REGENERATION,
                    DURATION_TICKS,
                    2,      // Regen III
                    true,
                    true,
                    true
            ));
        }

        // Particles
        spawnGroundRing(serverWorld, center, RADIUS);
        spawnParticleDome(serverWorld, center, RADIUS);

        player.sendMessage(
                Text.literal("Prismatic energies bloom around you.")
                        .formatted(Formatting.AQUA),
                true
        );

        UniqueItemCooldownManager.setCooldown(player, COOLDOWN_MS);
    }

    private void spawnGroundRing(ServerWorld world, Vec3d center, double radius) {
        int points = 100;

        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI / points) * i;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;

            world.spawnParticles(
                    ParticleTypes.END_ROD, // shulker-like glow
                    x,
                    center.y + 0.05,
                    z,
                    1,
                    0.0,
                    0.0,
                    0.0,
                    0.0
            );

            world.spawnParticles(
                    new BlockStateParticleEffect(
                            ParticleTypes.FALLING_DUST,
                            Blocks.AMETHYST_BLOCK.getDefaultState()
                    ),
                    x,
                    center.y + 0.05,
                    z,
                    1,
                    0.0,
                    0.0,
                    0.0,
                    0.0
            );

        }
    }

    private void spawnParticleDome(ServerWorld world, Vec3d center, double radius) {
        int rings = 12;
        int pointsPerRing = 48;

        for (int r = 0; r <= rings; r++) {
            double height = (r / (double) rings) * radius;
            double ringRadius = Math.sqrt(radius * radius - height * height);

            for (int i = 0; i < pointsPerRing; i++) {
                double angle = (2 * Math.PI / pointsPerRing) * i;
                double x = center.x + Math.cos(angle) * ringRadius;
                double z = center.z + Math.sin(angle) * ringRadius;
                double y = center.y + height;

                world.spawnParticles(
                        ParticleTypes.END_ROD,
                        x,
                        y,
                        z,
                        1,
                        0.0,
                        0.0,
                        0.0,
                        0.0
                );
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Active: Create a prismatic healing aura").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Grants Regeneration III to allies for 20s").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Radius: 8 blocks").formatted(Formatting.BLUE));
        tooltip.add(Text.literal("Cooldown: 1min").formatted(Formatting.GRAY));
    }
}
