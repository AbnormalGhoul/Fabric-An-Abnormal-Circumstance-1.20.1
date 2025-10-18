package net.abnormal.anabnormalcircumstance.item.custom;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.abnormal.anabnormalcircumstance.util.UniqueBladeCooldownManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FirstLeafBowItem extends BowItem {
    private static final long ABILITY_COOLDOWN_MILLIS = 30_000;
    private static final int STUN_DURATION_TICKS = 7 * 20;
    private static final String PRIMED_ARROW_KEY = "FirstLeafPrimedArrow";

    public FirstLeafBowItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity player && selected && stack == player.getMainHandStack()) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 40, 1, true, false));
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    public void tryActivateAbility(PlayerEntity player, ItemStack stack) {
        if (!UniqueBladeCooldownManager.isOnCooldown(player)) {
            stack.getOrCreateNbt().putBoolean(PRIMED_ARROW_KEY, true);
            UniqueBladeCooldownManager.setCooldown(player, ABILITY_COOLDOWN_MILLIS);

            // Play sound on the server so nearby players hear it
            if (!player.getWorld().isClient) {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    serverPlayer.getServerWorld().playSound(
                            null,
                            player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ITEM_TRIDENT_RETURN,
                            SoundCategory.PLAYERS,
                            5.0F, 1.0F
                    );
                } else {
                    player.getWorld().playSound(
                            null,
                            player.getBlockPos(),
                            SoundEvents.ITEM_TRIDENT_RETURN,
                            SoundCategory.PLAYERS,
                            5.0F, 1.0F
                    );
                }
            }
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        boolean primed = stack.getOrCreateNbt().getBoolean(PRIMED_ARROW_KEY);

        if (primed && user instanceof PlayerEntity player) {
            stack.getOrCreateNbt().putBoolean(PRIMED_ARROW_KEY, false);
            player.getItemCooldownManager().set(this, 1);
        }

        super.onStoppedUsing(stack, world, user, remainingUseTicks);

        // Only run on server and if primed
        if (!world.isClient && primed && user instanceof PlayerEntity player) {
            // Find the most recent arrow shot by this player in a reasonable radius
            PersistentProjectileEntity arrow = world.getEntitiesByClass(
                    PersistentProjectileEntity.class,
                    player.getBoundingBox().expand(64),
                    e -> e.getOwner() == player && !e.isRemoved()
            ).stream().max((a, b) -> Long.compare(b.age, a.age)).orElse(null);

            if (arrow != null) {
                // mark the arrow with a custom name instead of using entity NBT APIs
                arrow.setCustomName(Text.literal(PRIMED_ARROW_KEY));
            }
        }
    }

    public static void applyStun(LivingEntity target) {
        target.addStatusEffect(new StatusEffectInstance(ModEffects.STUN, STUN_DURATION_TICKS, 0));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Grants Regeneration I while held").formatted(Formatting.GREEN));
        tooltip.add(Text.literal("Active: Next arrow stuns for 7s (R)").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 30s").formatted(Formatting.GRAY));
    }
}