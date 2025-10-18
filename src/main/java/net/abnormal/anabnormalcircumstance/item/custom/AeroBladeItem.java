package net.abnormal.anabnormalcircumstance.item.custom;


import net.abnormal.anabnormalcircumstance.item.ModToolMaterials;
import net.abnormal.anabnormalcircumstance.network.ModPackets;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AeroBladeItem extends UniqueBladeItem {

    public AeroBladeItem(Settings settings) {
        super(ModToolMaterials.UNIQUE, 8, -2.4F, settings, 30_000); // 30s cooldown
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: No fall damage while held").formatted(net.minecraft.util.Formatting.AQUA));
        tooltip.add(Text.literal("Active: Dash forward (R)").formatted(net.minecraft.util.Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 30s").formatted(net.minecraft.util.Formatting.GRAY));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (entity instanceof PlayerEntity player && selected && stack == player.getMainHandStack()) {
            // Only reset fallDistance if the player is about to take fall damage (i.e., just before landing)
            if (!player.isOnGround() && player.getVelocity().y < 0 && player.fallDistance > 2.5F) {
                player.fallDistance = 0.0F;
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    protected void activateAbility(PlayerEntity player, ItemStack stack) {
        // Only apply dash and play sound on the server
         if (player instanceof ServerPlayerEntity serverPlayer) {
            Vec3d look = player.getRotationVec(1.0F);
            double dashStrength = 2.5;
            player.addVelocity(look.x * dashStrength, look.y * 0.2, look.z * dashStrength);
            player.velocityModified = true;

            // Play sound for all nearby players (including self)
            serverPlayer.getServerWorld().playSound(
                null, // null = all players
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_ENDER_DRAGON_FLAP,
                net.minecraft.sound.SoundCategory.PLAYERS,
                3.0F, // volume (default is 1.0F, so 3.0F is much louder)
                1.0F  // pitch
            );
        }
    }
}