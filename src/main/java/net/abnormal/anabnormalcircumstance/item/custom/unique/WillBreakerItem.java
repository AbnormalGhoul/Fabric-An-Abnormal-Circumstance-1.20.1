package net.abnormal.anabnormalcircumstance.item.custom.unique;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.abnormal.anabnormalcircumstance.item.interfaces.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.util.UniqueItemCooldownManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
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

public class WillBreakerItem extends SwordItem implements UniqueAbilityItem {

    public WillBreakerItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public void useUniqueAbility(PlayerEntity player) {
        if (player.getWorld().isClient()) return;
        if (UniqueItemCooldownManager.isOnCooldown(player)) {
            long remaining = UniqueItemCooldownManager.getRemaining(player);
            player.sendMessage(Text.literal("Ability Cooldown (" + (remaining / 1000) + "s)"), true);
            return;
        }

        World world = player.getWorld();
        var lookVec = player.getRotationVec(1.0F);
        var aoeCenter = player.getPos().add(lookVec.multiply(2.0)); // 2 blocks in front

        List<LivingEntity> targets = world.getEntitiesByClass(
                LivingEntity.class,
                player.getBoundingBox().expand(3.0, 2.0, 3.0),
                e -> e != player && e.isAlive() && e.distanceTo(player) <= 4.0
        );

        for (LivingEntity target : targets) {
            var toTarget = target.getPos().subtract(player.getPos()).normalize();
            double dot = lookVec.dotProduct(toTarget);
            if (dot > 0.5) { // in front cone
                target.damage(world.getDamageSources().playerAttack(player), 35.0F);
                target.setOnFireFor(5);
                target.addStatusEffect(new StatusEffectInstance(ModEffects.BLEEDING, 60, 2));
            }
        }

        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 5.0F, 1.0F);
        player.sendMessage(Text.literal("§6Inferno Strike unleashed!").formatted(Formatting.GOLD), true);
        UniqueItemCooldownManager.setCooldown(player, 30 * 1000);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient()) {
            target.setOnFireFor(10);
            target.addStatusEffect(new StatusEffectInstance(ModEffects.BLEEDING, 60, 2));
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Causes Bleeding & Fire damage").formatted(net.minecraft.util.Formatting.AQUA));
        tooltip.add(Text.literal("Active: Inferno Strike, Inflicts heavy damage (R)").formatted(net.minecraft.util.Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 30s").formatted(net.minecraft.util.Formatting.GRAY));
    }
}
