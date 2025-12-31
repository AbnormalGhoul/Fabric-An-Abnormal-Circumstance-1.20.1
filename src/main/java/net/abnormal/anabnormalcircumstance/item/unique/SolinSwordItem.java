package net.abnormal.anabnormalcircumstance.item.unique;

import dev.emi.trinkets.api.TrinketsApi;
import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.abnormal.anabnormalcircumstance.item.util.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.util.UniqueAbilityHelper;
import net.abnormal.anabnormalcircumstance.util.UniqueItemCooldownManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SolinSwordItem extends AxeItem implements UniqueAbilityItem {
    private static final UUID DAMAGE_BOOST_ID = UUID.fromString("13fdd9f0-23c5-4c19-b4e2-8a7b50e1f00a");
    private static final Set<UUID> ARMED_PLAYERS = new HashSet<>();
    private static final EntityAttributeModifier DAMAGE_BOOST =
            new EntityAttributeModifier(DAMAGE_BOOST_ID, "Solin Dual Wield Boost", 3, EntityAttributeModifier.Operation.ADDITION);

    public SolinSwordItem(ToolMaterial material, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public void useUniqueAbility(PlayerEntity player) {
        if (player.getWorld().isClient()) return;

        boolean hasMark = TrinketsApi.getTrinketComponent(player)
                .map(comp -> comp.isEquipped(ModItems.CHAMPIONS_CREST))
                .orElse(false);
        if (!hasMark) {
            player.sendMessage(
                    Text.literal("You must equip the Champion's Crest to use this weapon")
                            .formatted(Formatting.DARK_RED),
                    true
            );
            return;
        }
        if (UniqueItemCooldownManager.isOnCooldown(player)) {
            long remaining = UniqueItemCooldownManager.getRemaining(player);
            player.sendMessage(
                    Text.literal("Ability Cooldown (" + (remaining / 1000) + "s)"),
                    true
            );
            return;
        }
        if (!UniqueAbilityHelper.hasBothSolinWeapons(player)) {
            player.sendMessage(
                    Text.literal("You must wield both Solin weapons")
                            .formatted(Formatting.RED),
                    true
            );
            return;
        }
        // Arm next hit
        ARMED_PLAYERS.add(player.getUuid());
        player.getWorld().playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,
                SoundCategory.PLAYERS,
                1.6f,
                1.2f
        );
        player.sendMessage(
                Text.literal("Your next strike will expose the enemy!")
                        .formatted(Formatting.GOLD),
                true
        );
        // 1 minute cooldown
        UniqueItemCooldownManager.setCooldown(player, 60 * 1000);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient()
                && attacker instanceof PlayerEntity player) {
            UUID uuid = player.getUuid();
            if (ARMED_PLAYERS.contains(uuid)) {
                // Apply Vulnerability I for 20 seconds
                target.addStatusEffect(
                        new StatusEffectInstance(
                                ModEffects.VULNERABILITY,
                                20 * 20,
                                0,
                                false,
                                true,
                                true
                        )
                );
                target.addStatusEffect(
                        new StatusEffectInstance(
                                StatusEffects.GLOWING,
                                20 * 20,
                                0,
                                false,
                                true,
                                true
                        )
                );
                attacker.getWorld().playSound(
                        null,
                        target.getBlockPos(),
                        SoundEvents.ITEM_TRIDENT_HIT,
                        SoundCategory.PLAYERS,
                        1.2f,
                        0.9f
                );
                ARMED_PLAYERS.remove(uuid);
            }
        }

        return super.postHit(stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (world.isClient()) return;
        if (entity instanceof PlayerEntity player) {
            boolean bothEquipped = UniqueAbilityHelper.hasBothSolinWeapons(player);

            // Add damage if both weapons are equipped
            var attr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            if (attr != null) {
                boolean hasBoost = attr.getModifier(DAMAGE_BOOST_ID) != null;
                if (bothEquipped && !hasBoost) {
                    attr.addTemporaryModifier(DAMAGE_BOOST);
                } else if (!bothEquipped && hasBoost) {
                    attr.removeModifier(DAMAGE_BOOST_ID);
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Increased damage while both Blades are held together").formatted(net.minecraft.util.Formatting.AQUA));
        tooltip.add(Text.literal("Active: Inflict Vulnerability onto next hit").formatted(net.minecraft.util.Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 1min").formatted(net.minecraft.util.Formatting.GRAY));
    }
}
