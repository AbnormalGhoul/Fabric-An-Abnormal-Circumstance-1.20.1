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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
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
            player.sendMessage(Text.literal("You must equip the Champion's Crest to use this weapon").formatted(Formatting.DARK_RED), true);
            return;
        }

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
        // Affect nearby entities (exclude teammates)
        List<LivingEntity> nearbyEntities = world.getEntitiesByClass(LivingEntity.class, area,
                entity -> entity != player && entity.isAlive() && !player.isTeammate(entity));

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
        if (world.isClient()) {
            if (entity instanceof PlayerEntity player) {
                boolean holding = selected || player.getOffHandStack() == stack;
                if (holding) {
                    // Only spawn particles occasionally
                    if (world.getTime() % 5 == 0) { // every 0.5s
                        double offsetX = (world.random.nextDouble() - 0.5) * 1.2;
                        double offsetY = world.random.nextDouble() * 1.8;
                        double offsetZ = (world.random.nextDouble() - 0.5) * 1.2;

                        world.addParticle(
                                net.minecraft.particle.ParticleTypes.SNOWFLAKE,
                                player.getX() + offsetX,
                                player.getY() + offsetY,
                                player.getZ() + offsetZ,
                                0.0, 0.02, 0.0
                        );
                    }
                }
            }
            return; // stops client-side execution
        }

        // Effect
        if (entity instanceof PlayerEntity player) {
            boolean holding = selected || player.getOffHandStack() == stack;
            if (holding) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 45, 1, true, false, true));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Grants Speed II while held").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Active: Stuns all nearby Entities").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 1min").formatted(Formatting.GRAY));
    }
}