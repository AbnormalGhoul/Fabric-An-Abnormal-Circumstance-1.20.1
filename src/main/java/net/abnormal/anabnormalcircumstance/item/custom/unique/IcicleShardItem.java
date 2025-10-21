package net.abnormal.anabnormalcircumstance.item.custom.unique;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.abnormal.anabnormalcircumstance.item.interfaces.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.util.UniqueItemCooldownManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IcicleShardItem extends ShieldItem implements UniqueAbilityItem {
    public IcicleShardItem(Settings settings) {
        super(settings);
    }

    @Override
    public void useUniqueAbility(PlayerEntity player) {
        if (player.getWorld().isClient()) return;
        if (UniqueItemCooldownManager.isOnCooldown(player)) {
            long remaining = UniqueItemCooldownManager.getRemaining(player);
            player.sendMessage(net.minecraft.text.Text.literal("Ability Cooldown (" + (remaining / 1000) + "s)"), true);
            return;
        }
        World world = player.getWorld();

        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ENTITY_WARDEN_DEATH,
                SoundCategory.PLAYERS,
                5.0f,
                1.2f
        );
        player.sendMessage(Text.literal("Freeze!").formatted(Formatting.GOLD), true);
        double radius = 5.0;
        Box area = new Box(
                player.getX() - radius, player.getY() - 2, player.getZ() - radius,
                player.getX() + radius, player.getY() + 2, player.getZ() + radius
        );
        // Affect nearby entities
        List<LivingEntity> nearbyEntities = world.getEntitiesByClass(LivingEntity.class, area,
                entity -> entity != player && entity.isAlive());

        for (LivingEntity target : nearbyEntities) {
            target.addStatusEffect(new StatusEffectInstance(ModEffects.STUN, 60, 0, false, true, true));
            world.playSound(
                    null,
                    target.getBlockPos(),
                    SoundEvents.BLOCK_GLASS_BREAK,
                    SoundCategory.PLAYERS,
                    1.0f,
                    1.0f
            );
        }
        UniqueItemCooldownManager.setCooldown(player, 60 * 1000);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (world.isClient()) return;
        if (entity instanceof PlayerEntity player) {
            boolean holding = selected || player.getOffHandStack() == stack;
            if (holding) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 45, 1, true, false, true));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Grants Speed II").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Active: Stuns all nearby Entities (R)").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 1min").formatted(Formatting.GRAY));
    }
}
