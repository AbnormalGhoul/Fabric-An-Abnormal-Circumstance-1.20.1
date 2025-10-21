package net.abnormal.anabnormalcircumstance.item.custom.unique;

import net.abnormal.anabnormalcircumstance.item.interfaces.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.util.UniqueItemCooldownManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PyroBladeItem extends SwordItem implements UniqueAbilityItem {

    public PyroBladeItem(ToolMaterial material, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    public void useUniqueAbility(PlayerEntity player) {
        if (player.getWorld().isClient()) return;
        if (UniqueItemCooldownManager.isOnCooldown(player)) {
            long remaining = UniqueItemCooldownManager.getRemaining(player);
            player.sendMessage(net.minecraft.text.Text.literal("Ability Cooldown (" + (remaining / 1000) + "s)"), true);
            return;
        }
        UniqueItemCooldownManager.setCooldown(player, 30 * 1000);
        player.sendMessage(Text.literal("Magma Swing!").formatted(Formatting.GOLD), true);
        World world = player.getWorld();
        Box area = player.getBoundingBox().expand(3.0);
        for (Entity entity : world.getEntitiesByClass(LivingEntity.class, area, e -> e != player)) {
            entity.damage(world.getDamageSources().magic(), 25.0F);
        }
        player.getWorld().playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ENTITY_BLAZE_SHOOT,
                SoundCategory.PLAYERS,
                5.0f,
                1.0f
        );
        // Spawn fire and red particles in a pillar
        if (world.isClient) return;
        for (int y = 0; y < 5; y++) {
            double py = player.getY() + y;
            for (int i = 0; i < 16; i++) {
                double angle = (2 * Math.PI / 16) * i;
                double px = player.getX() + Math.cos(angle) * 1.5;
                double pz = player.getZ() + Math.sin(angle) * 1.5;
                ((ServerWorld)world).spawnParticles(ParticleTypes.FLAME, px, py, pz, 1, 0, 0, 0, 0);
                ((ServerWorld)world).spawnParticles(ParticleTypes.CRIMSON_SPORE, px, py, pz, 1, 0, 0, 0, 1);
            }
        }
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient()) {
            target.setOnFireFor(5);
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Burns enemies on hit").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Active: Magma Swing - 25 damage in 3-block radius (R)").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 30s").formatted(Formatting.GRAY));
    }
}