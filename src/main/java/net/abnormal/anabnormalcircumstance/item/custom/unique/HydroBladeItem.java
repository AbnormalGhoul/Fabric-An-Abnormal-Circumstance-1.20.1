package net.abnormal.anabnormalcircumstance.item.custom.unique;

import net.abnormal.anabnormalcircumstance.item.ModToolMaterials;
import net.abnormal.anabnormalcircumstance.item.interfaces.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.util.UniqueItemCooldownManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
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

public class HydroBladeItem extends SwordItem implements UniqueAbilityItem {

    public HydroBladeItem(ToolMaterial material, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient()) {
            target.setFrozenTicks(60);
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void useUniqueAbility(PlayerEntity player) {
        if (player.getWorld().isClient()) return;
        if (UniqueItemCooldownManager.isOnCooldown(player)) {
            long remaining = UniqueItemCooldownManager.getRemaining(player);
            player.sendMessage(net.minecraft.text.Text.literal("Ability Cooldown (" + (remaining / 1000) + "s)"), true);
            return;
        }
        if (!player.getWorld().isClient) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.ABSORPTION, 30 * 20, 3, false, true, true));
        }
        player.getWorld().playSound(
                null,
                player.getBlockPos(),
                SoundEvents.AMBIENT_UNDERWATER_EXIT,
                SoundCategory.PLAYERS,
                5.0f,
                1.0f
        );
        player.sendMessage(Text.literal("Absorption Hearts Granted!").formatted(Formatting.GOLD), true);
        UniqueItemCooldownManager.setCooldown(player, 60 * 1000); // 60 seconds cooldown

    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Freezes enemies on hit").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Active: Grants 8 Absorption Bonus hearts").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 1min").formatted(Formatting.GRAY));
    }
}