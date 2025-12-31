package net.abnormal.anabnormalcircumstance.item.unique;

import dev.emi.trinkets.api.TrinketsApi;
import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.abnormal.anabnormalcircumstance.item.util.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.util.UniqueItemCooldownManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ForgefatherJudgmentItem extends AxeItem implements UniqueAbilityItem {

    public ForgefatherJudgmentItem(ToolMaterial material, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public void useUniqueAbility(PlayerEntity player) {
        if (player.getWorld().isClient()) return;

        boolean hasMark = TrinketsApi.getTrinketComponent(player)
                .map(comp -> comp.isEquipped(ModItems.CHAMPIONS_CREST))
                .orElse(false);

        if (!hasMark) {
            player.sendMessage(Text.literal("You must equip the Champion's Crest to use this weapon").formatted(Formatting.DARK_RED), true);
            return;
        }

        if (UniqueItemCooldownManager.isOnCooldown(player)) {
            long remaining = UniqueItemCooldownManager.getRemaining(player);
            player.sendMessage(Text.literal("Ability Cooldown (" + (remaining / 1000) + "s)").formatted(Formatting.GRAY), true);
            return;
        }

        ServerWorld world = (ServerWorld) player.getWorld();

        // Summon lightning on player
        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
        if (lightning != null) {
            lightning.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), 0f, 0f);
            lightning.setCosmetic(false);
            world.spawnEntity(lightning);
        }

        // Play activation sound
        world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_TRIDENT_THUNDER, SoundCategory.PLAYERS, 5.0f, 1.0f);

        // Apply effects
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 3, false, true, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 2, false, true, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 200, 1, false, true, true));

        player.sendMessage(Text.literal("You are overcharged with divine power!").formatted(Formatting.GOLD, Formatting.BOLD), true);

        // 1 minute cooldown
        UniqueItemCooldownManager.setCooldown(player, 60 * 1000);
    }

    // Passive: Grants haste 2 while held
    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (world.isClient()) return;
        if (entity instanceof PlayerEntity player) {
            if (player.getMainHandStack() == stack) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 45, 1, true, false, true));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 45, 0, true, false, true));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Grants Haste II & Resistance I while held").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Active: Overcharges the player with Lightning, grants Resistance IV & Regen III").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 1min").formatted(Formatting.GRAY));
    }

}
