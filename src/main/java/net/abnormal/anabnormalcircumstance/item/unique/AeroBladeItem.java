package net.abnormal.anabnormalcircumstance.item.unique;

import net.abnormal.anabnormalcircumstance.item.util.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.util.UniqueItemCooldownManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AeroBladeItem extends SwordItem implements UniqueAbilityItem {

    public AeroBladeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    // Passive: Prevent fall damage while held
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

    // Called from KeyBindingHandler
    public void useUniqueAbility(PlayerEntity player) {
        if (player.getWorld().isClient()) return;
        if (UniqueItemCooldownManager.isOnCooldown(player)) {
            long remaining = UniqueItemCooldownManager.getRemaining(player);
            player.sendMessage(net.minecraft.text.Text.literal("Ability Cooldown (" + (remaining / 1000) + "s)"), true);
            return;
        }
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
                    3.0F,
                    1.0F
            );
            player.sendMessage(Text.literal("Dash!").formatted(Formatting.GOLD), true);
            UniqueItemCooldownManager.setCooldown(player, 30 * 1000); // 30 seconds cooldown
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: No fall damage while held").formatted(net.minecraft.util.Formatting.AQUA));
        tooltip.add(Text.literal("Active: Dash forward").formatted(net.minecraft.util.Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 30s").formatted(net.minecraft.util.Formatting.GRAY));
    }
}