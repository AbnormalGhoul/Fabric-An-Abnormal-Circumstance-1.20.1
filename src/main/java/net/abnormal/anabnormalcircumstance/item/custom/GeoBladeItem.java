package net.abnormal.anabnormalcircumstance.item.custom;

import net.abnormal.anabnormalcircumstance.item.ModToolMaterials;
import net.abnormal.anabnormalcircumstance.network.ModPackets;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GeoBladeItem extends UniqueBladeItem {
    private static final String IMMUNITY_KEY = "geomancy_immunity";

    public GeoBladeItem(Settings settings) {
        super(ModToolMaterials.UNIQUE, 8, -2.4F, settings, 60_000);
    }

    @Override
    protected void activateAbility(PlayerEntity player, ItemStack stack) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ModPackets.sendBladeSound(serverPlayer, SoundEvents.BLOCK_ANVIL_PLACE.getId().toString());
        }
        stack.getOrCreateNbt().putInt(IMMUNITY_KEY, 7);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (entity instanceof PlayerEntity player && selected && stack == player.getMainHandStack()) {
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.HASTE, 40, 0, true, false, true));
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Grants Haste I while held").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Active: Grants immunity to next 7 hits (R)").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 60s").formatted(Formatting.GRAY));
    }

    public static boolean tryBlockDamage(PlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        if (stack.getItem() instanceof GeoBladeItem) {
            int immunity = stack.getOrCreateNbt().getInt(IMMUNITY_KEY);
            if (immunity > 0) {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    ModPackets.sendBladeSound(serverPlayer, SoundEvents.BLOCK_ANVIL_LAND.getId().toString());
                }
                stack.getOrCreateNbt().putInt(IMMUNITY_KEY, immunity - 1);
                return true; // Block damage
            }
        }
        return false;
    }
}
