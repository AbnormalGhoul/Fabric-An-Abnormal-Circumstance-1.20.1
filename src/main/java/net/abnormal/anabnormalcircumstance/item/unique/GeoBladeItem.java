package net.abnormal.anabnormalcircumstance.item.unique;

import net.abnormal.anabnormalcircumstance.item.util.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.util.UniqueItemCooldownManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GeoBladeItem extends SwordItem implements UniqueAbilityItem {
    private static final String IMMUNITY_KEY = "geomancy_immunity";

    public GeoBladeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public void useUniqueAbility(PlayerEntity player) {
        if (player.getWorld().isClient()) return;
        if (UniqueItemCooldownManager.isOnCooldown(player)) {
            long remaining = UniqueItemCooldownManager.getRemaining(player);
            player.sendMessage(net.minecraft.text.Text.literal("Ability Cooldown (" + (remaining / 1000) + "s)"), true);
            return;
        }
        player.getWorld().playSound(
                null,
                player.getBlockPos(),
                SoundEvents.BLOCK_ANVIL_PLACE,
                SoundCategory.PLAYERS,
                5.0f,
                1.0f
        );
        player.getMainHandStack().getOrCreateNbt().putInt(IMMUNITY_KEY, 5);
        player.sendMessage(Text.literal("Next 5 Hits will be Neglected!").formatted(Formatting.GOLD), true);
        UniqueItemCooldownManager.setCooldown(player, 60 * 1000); // 60 seconds cooldown

    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (world.isClient()) return;
        if (entity instanceof PlayerEntity player) {
            if (player.getMainHandStack() == stack) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 45, 0, true, false, true));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Grants Resistance I while held").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Active: Grants immunity to next 5 hits (R)").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 1min").formatted(Formatting.GRAY));
    }

    public static boolean tryBlockDamage(PlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        if (stack.getItem() instanceof GeoBladeItem) {
            int immunity = stack.getOrCreateNbt().getInt(IMMUNITY_KEY);
            if (immunity > 0) {
                player.getWorld().playSound(
                        null,
                        player.getBlockPos(),
                        SoundEvents.BLOCK_POINTED_DRIPSTONE_LAND,
                        SoundCategory.PLAYERS,
                        1.0f,
                        1.0f
                );
                stack.getOrCreateNbt().putInt(IMMUNITY_KEY, immunity - 1);
                return true; // Block damage
            }
        }
        return false;
    }
}
