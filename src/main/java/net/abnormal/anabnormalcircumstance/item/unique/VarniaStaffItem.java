package net.abnormal.anabnormalcircumstance.item.unique;

import net.abnormal.anabnormalcircumstance.item.util.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.util.UniqueItemCooldownManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VarniaStaffItem extends Item implements UniqueAbilityItem {

    private static final long BIND_COOLDOWN_MS = 60 * 1000;
    private static final long ABILITY_COOLDOWN_MS = 180 * 1000;
    private static final double TELEPORT_RADIUS = 4.0;

    public VarniaStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(net.minecraft.item.ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();

        if (player == null || world.isClient()) {
            return ActionResult.SUCCESS;
        }

        NbtCompound nbt = stack.getOrCreateNbt();
        long now = world.getTime() * 50L;

        long lastBind = nbt.getLong("varnia_last_bind");
        if (now - lastBind < BIND_COOLDOWN_MS) {
            long remaining = (BIND_COOLDOWN_MS - (now - lastBind)) / 1000;
            player.sendMessage(
                    Text.literal("Staff recalibrating (" + remaining + "s)")
                            .formatted(Formatting.GRAY),
                    true
            );
            return ActionResult.FAIL;
        }

        BlockPos pos = context.getBlockPos();

        nbt.putInt("varnia_bound_x", pos.getX());
        nbt.putInt("varnia_bound_y", pos.getY());
        nbt.putInt("varnia_bound_z", pos.getZ());
        nbt.putLong("varnia_last_bind", now);

        world.playSound(
                null,
                pos,
                SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                SoundCategory.PLAYERS,
                1.2f,
                1.1f
        );

        player.sendMessage(
                Text.literal("Varnia Staff bound to location.")
                        .formatted(Formatting.AQUA),
                true
        );

        return ActionResult.SUCCESS;
    }

    @Override
    public void useUniqueAbility(PlayerEntity player) {
        World world = player.getWorld();
        ItemStack stack = player.getMainHandStack();

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

        if (!stack.hasNbt()) {
            player.sendMessage(
                    Text.literal("The staff is unbound.")
                            .formatted(Formatting.RED),
                    true
            );
            return;
        }

        NbtCompound nbt = stack.getNbt();
        if (!nbt.contains("varnia_bound_x")) {
            player.sendMessage(
                    Text.literal("The staff is unbound.")
                            .formatted(Formatting.RED),
                    true
            );
            return;
        }

        BlockPos target = new BlockPos(
                nbt.getInt("varnia_bound_x"),
                nbt.getInt("varnia_bound_y"),
                nbt.getInt("varnia_bound_z")
        );

        BlockPos center = player.getBlockPos();
        Box box = new Box(center).expand(TELEPORT_RADIUS);

        List<Entity> entities = world.getEntitiesByClass(
                Entity.class,
                box,
                e -> e.isAlive() && !e.hasVehicle()
        );

        for (Entity entity : entities) {
            entity.teleport(
                    target.getX() + 0.5,
                    target.getY() + 1,
                    target.getZ() + 0.5
            );
        }

        world.playSound(
                null,
                target,
                SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.PLAYERS,
                1.5f,
                0.8f
        );

        for (int i = 0; i < 60; i++) {
            Vec3d velocity = new Vec3d(
                    (player.getRandom().nextDouble() - 0.5) * 1.2,
                    player.getRandom().nextDouble() * 0.8,
                    (player.getRandom().nextDouble() - 0.5) * 1.2
            );

            ((ServerWorld)world).spawnParticles(
                    ParticleTypes.WITCH,
                    player.getX(),
                    player.getBodyY(0.5),
                    player.getZ(),
                    1,
                    velocity.x,
                    velocity.y,
                    velocity.z,
                    0.15
            );
        }

        player.sendMessage(
                Text.literal("Reality folds around you.")
                        .formatted(Formatting.DARK_AQUA),
                true
        );

        UniqueItemCooldownManager.setCooldown(player, ABILITY_COOLDOWN_MS);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Right Click Block: Bind location").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Active: Teleport nearby entities").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Bind Cooldown: 1min").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Ability Cooldown: 3min").formatted(Formatting.GRAY));
    }
}
