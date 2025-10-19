package net.abnormal.anabnormalcircumstance.item.custom.unique;

import net.abnormal.anabnormalcircumstance.item.interfaces.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.util.UniqueItemCooldownManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;
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
        player.getMainHandStack().getOrCreateNbt().putInt(IMMUNITY_KEY, 7);
        player.sendMessage(Text.literal("Next 7 Hits will be Neglected!").formatted(Formatting.GOLD), true);
        UniqueItemCooldownManager.setCooldown(player, 60 * 1000); // 60 seconds cooldown

    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (world.isClient()) return;
        if (entity instanceof PlayerEntity player) {
            boolean holding = selected || player.getOffHandStack() == stack;
            if (holding) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 45, 0, true, false, true));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Grants Regen I while held").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Active: Grants immunity to next 7 hits (R)").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 60s").formatted(Formatting.GRAY));
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
