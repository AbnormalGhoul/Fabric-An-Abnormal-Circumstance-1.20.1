package net.abnormal.anabnormalcircumstance.item.unique;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.abnormal.anabnormalcircumstance.item.util.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.util.UniqueItemCooldownManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BerserkersVialItem extends Item implements UniqueAbilityItem {

    public BerserkersVialItem(Settings settings) {
        super(settings);
    }

    @Override
    public void useUniqueAbility(PlayerEntity player) {
        World world = player.getWorld();

        if (world.isClient()) return;

        // Cooldown check
        if (UniqueItemCooldownManager.isOnCooldown(player)) {
            long remaining = UniqueItemCooldownManager.getRemaining(player);
            player.sendMessage(
                    Text.literal("Ability Cooldown (" + (remaining / 1000) + "s)")
                            .formatted(Formatting.WHITE),
                    true
            );
            return;
        }

        // Apply Rage II for 20s (ambient + particles)
        player.addStatusEffect(new StatusEffectInstance(
                ModEffects.RAGE,
                20 * 20,
                1,      // Level 2 (0 = I)
                true,   // ambient
                true,   // show particles
                true    // show icon
        ));

        // Purple particle explosion
        for (int i = 0; i < 60; i++) {
            Vec3d velocity = new Vec3d(
                    (player.getRandom().nextDouble() - 0.5) * 1.2,
                    player.getRandom().nextDouble() * 0.8,
                    (player.getRandom().nextDouble() - 0.5) * 1.2
            );

            ((ServerWorld)world).spawnParticles(
                    ParticleTypes.WITCH,
                    player.getX(),
                    player.getBodyY(0.5),
                    player.getZ(),
                    1,
                    velocity.x,
                    velocity.y,
                    velocity.z,
                    0.15
            );
        }

        // Unique sound (aggressive, vial-like)
        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ITEM_TOTEM_USE,
                SoundCategory.PLAYERS,
                1.6f,  // louder
                0.7f   // deeper pitch
        );

        player.sendMessage(
                Text.literal("You feel uncontrollable rage surge through you!")
                        .formatted(Formatting.DARK_PURPLE),
                true
        );

        // 1 minute cooldown
        UniqueItemCooldownManager.setCooldown(player, 60 * 1000);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Active: Enter a berserk rage - Grants Rage II for 20 seconds").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 1min").formatted(Formatting.GRAY));
    }
}
