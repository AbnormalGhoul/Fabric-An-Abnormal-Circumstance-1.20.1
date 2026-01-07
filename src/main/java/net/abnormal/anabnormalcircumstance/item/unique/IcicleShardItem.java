package net.abnormal.anabnormalcircumstance.item.unique;

import com.github.crimsondawn45.fabricshieldlib.lib.object.FabricShieldItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.abnormal.anabnormalcircumstance.item.util.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.util.UniqueItemCooldownManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IcicleShardItem extends FabricShieldItem implements UniqueAbilityItem {

    public IcicleShardItem(Settings settings, int coolDownTicks, int enchantability, Item... repairItems) {
        super(settings, coolDownTicks, enchantability, repairItems);
    }

    @Override
    public void useUniqueAbility(PlayerEntity player) {
        if (player.getWorld().isClient()) return;

        boolean hasMark = TrinketsApi.getTrinketComponent(player)
                .map(comp -> comp.isEquipped(ModItems.CHAMPIONS_CREST))
                .orElse(false);

        if (!hasMark) {
            player.sendMessage(
                    Text.literal("You must equip the Champion's Crest to use this shield")
                            .formatted(Formatting.DARK_RED),
                    true
            );
            return;
        }

        if (UniqueItemCooldownManager.isOnCooldown(player)) {
            long remaining = UniqueItemCooldownManager.getRemaining(player);
            player.sendMessage(
                    Text.literal("Ability Cooldown (" + (remaining / 1000) + "s)")
                            .formatted(Formatting.WHITE),
                    true
            );
            return;
        }

        if (!(player.getWorld() instanceof ServerWorld world)) return;

        Vec3d origin = player.getPos().add(0, 1.0, 0);
        Vec3d look = player.getRotationVec(1.0F).normalize();

        double forwardRange = 3.0;
        double halfWidth = 1.5;
        double height = 2.0;

        // Large box for candidate collection
        Box searchBox = player.getBoundingBox().expand(4.0, 2.0, 4.0);

        List<LivingEntity> targets = world.getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                e -> e != player && e.isAlive() && !player.isTeammate(e)
        );

        player.sendMessage(
                Text.literal("Frost Bash!")
                        .formatted(Formatting.GOLD),
                true
        );

        for (LivingEntity target : targets) {
            Vec3d toTarget = target.getPos().subtract(origin);
            double forwardDot = toTarget.normalize().dotProduct(look);

            // Must be in front
            if (forwardDot <= 0.5) continue;

            // Project distance forward
            double forwardDist = toTarget.dotProduct(look);
            if (forwardDist < 0 || forwardDist > forwardRange) continue;

            // Side distance
            Vec3d sideways = toTarget.subtract(look.multiply(forwardDist));
            if (sideways.lengthSquared() > halfWidth * halfWidth) continue;

            // Apply damage
            target.damage(
                    world.getDamageSources().playerAttack(player),
                    25.0F
            );

            // Freeze (20 seconds)
            target.setFrozenTicks(20 * 20);

            // Launch away
            Vec3d knockback = target.getPos()
                    .subtract(player.getPos())
                    .normalize()
                    .multiply(1.8)
                    .add(0, 0.6, 0);

            target.addVelocity(knockback.x, knockback.y, knockback.z);
            target.velocityModified = true;
        }

        // Sound
        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ITEM_TRIDENT_THUNDER,
                SoundCategory.PLAYERS,
                2.0F,
                0.8F
        );

        // Sweep particles
        for (int i = 0; i < 24; i++) {
            double progress = i / 24.0;
            Vec3d point = origin.add(look.multiply(progress * forwardRange));

            world.spawnParticles(
                    ParticleTypes.SNOWFLAKE,
                    point.x,
                    point.y,
                    point.z,
                    3,
                    0.15,
                    0.2,
                    0.15,
                    0.02
            );

            world.spawnParticles(
                    ParticleTypes.CLOUD,
                    point.x,
                    point.y,
                    point.z,
                    1,
                    0.1,
                    0.1,
                    0.1,
                    0.01
            );
        }

        UniqueItemCooldownManager.setCooldown(player, 30 * 1000);
    }



    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (world.isClient()) {
//            if (entity instanceof PlayerEntity player) {
//                boolean holding = player.getOffHandStack() == stack;
//                if (holding && world.getTime() % 5 == 0) {
//                    double offsetX = (world.random.nextDouble() - 0.5) * 1.2;
//                    double offsetY = world.random.nextDouble() * 1.8;
//                    double offsetZ = (world.random.nextDouble() - 0.5) * 1.2;
//
//                    world.addParticle(
//                            net.minecraft.particle.ParticleTypes.SNOWFLAKE,
//                            player.getX() + offsetX,
//                            player.getY() + offsetY,
//                            player.getZ() + offsetZ,
//                            0.0, 0.02, 0.0
//                    );
//                }
//            }
            return; }

        if (entity instanceof PlayerEntity player) {
            boolean holdingOffhand = player.getOffHandStack() == stack;
            if (holdingOffhand) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 45, 0, true, false, true));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Grants Fire Resistance & +3 Damage while in Offhand").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Active: Frost Bash - launches & freezes enemies").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Cooldown: 30s").formatted(Formatting.GRAY));
    }
}
