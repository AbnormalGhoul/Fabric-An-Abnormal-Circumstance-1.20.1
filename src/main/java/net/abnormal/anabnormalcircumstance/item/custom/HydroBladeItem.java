package net.abnormal.anabnormalcircumstance.item.custom;

import net.abnormal.anabnormalcircumstance.item.ModToolMaterials;
import net.abnormal.anabnormalcircumstance.network.ModPackets;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class HydroBladeItem extends UniqueBladeItem {
    public HydroBladeItem(Settings settings) {
        super(ModToolMaterials.UNIQUE, 8, -2.4F, settings, 60_000);
    }

    @Override
    protected void onHitEffect(PlayerEntity player, LivingEntity target, ItemStack stack) {
        target.setFrozenTicks(40); // 2 seconds
    }

    @Override
    protected void activateAbility(PlayerEntity player, ItemStack stack) {
        if (!player.getWorld().isClient) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.ABSORPTION, 12000, 3, true, true, true));
        }
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ModPackets.sendBladeSound(serverPlayer, SoundEvents.AMBIENT_UNDERWATER_EXIT.getId().toString());
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Freezes enemies on hit").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Active: Grants 8 Absorption Bonus hearts (R)").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 60s").formatted(Formatting.GRAY));
    }
}