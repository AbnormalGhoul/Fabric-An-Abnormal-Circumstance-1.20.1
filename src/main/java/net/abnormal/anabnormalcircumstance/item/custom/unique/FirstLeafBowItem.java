package net.abnormal.anabnormalcircumstance.item.custom.unique;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.abnormal.anabnormalcircumstance.item.interfaces.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.util.UniqueItemCooldownManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FirstLeafBowItem extends BowItem implements UniqueAbilityItem {
    private static final String PRIMED_ARROW_KEY = "FirstLeafPrimedArrow";

    public FirstLeafBowItem(Settings settings) {
        super(settings);
    }

    public static void applyStun(LivingEntity target) {
        target.addStatusEffect(new StatusEffectInstance(ModEffects.STUN, 140, 0));
    }

    @Override
    public void useUniqueAbility(PlayerEntity player) {
        if (player.getWorld().isClient()) return;
        if (UniqueItemCooldownManager.isOnCooldown(player)) {
            long remaining = UniqueItemCooldownManager.getRemaining(player);
            player.sendMessage(net.minecraft.text.Text.literal("Ability Cooldown (" + (remaining / 1000) + "s)"), true);
            return;
        }
        player.getMainHandStack().getOrCreateNbt().putBoolean(PRIMED_ARROW_KEY, true);
        player.getWorld().playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ITEM_TRIDENT_RETURN,
                SoundCategory.PLAYERS,
                1.0f,
                1.0f
        );

        player.sendMessage(Text.literal("Next arrow will stun your target!").formatted(Formatting.GOLD), true);

        UniqueItemCooldownManager.setCooldown(player, 30 * 1000); // 30 seconds cooldown

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

    // Passive: Grants regeneration 2 while held
    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (world.isClient()) return;
        if (entity instanceof PlayerEntity player) {
            boolean holding = selected || player.getOffHandStack() == stack;
            if (holding) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 45, 1, true, false, true));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Grants Regeneration II").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Active: Next arrow stuns for 7s (R)").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 30s").formatted(Formatting.GRAY));
    }
}