package net.abnormal.anabnormalcircumstance.item.custom.unique;

import net.abnormal.anabnormalcircumstance.item.interfaces.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.util.UniqueItemCooldownManager;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ForgefatherJudgmentItem extends AxeItem implements UniqueAbilityItem {

    private static final Set<UUID> primedPlayers = new HashSet<>();

    public ForgefatherJudgmentItem(ToolMaterial material, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public void useUniqueAbility(PlayerEntity player) {
        if (player.getWorld().isClient()) return;
        if (UniqueItemCooldownManager.isOnCooldown(player)) {
            long remaining = UniqueItemCooldownManager.getRemaining(player);
            player.sendMessage(net.minecraft.text.Text.literal("Ability Cooldown (" + (remaining / 1000) + "s)"), true);
            return;
        }
        player.getWorld().playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ITEM_TRIDENT_HIT_GROUND,
                SoundCategory.PLAYERS,
                5.0f,
                1.0f
        );
        primedPlayers.add(player.getUuid());
        player.sendMessage(Text.literal("Next strike will call down lightning!").formatted(Formatting.GOLD), true);
        UniqueItemCooldownManager.setCooldown(player, 30 * 1000);
    }

//    When the weapon hits an entity, check if the ability was primed
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.getWorld().isClient() && attacker instanceof PlayerEntity player) {
            if (primedPlayers.remove(player.getUuid())) {
                strikeLightning(target, (ServerWorld) attacker.getWorld());
            }
        }
        return super.postHit(stack, target, attacker);
    }

//    Spawns multiple lightning bolts around the struck target
    private void strikeLightning(LivingEntity target, ServerWorld world) {
        double x = target.getX();
        double y = target.getY();
        double z = target.getZ();

        for (int i = 0; i < 3; i++) {
            LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
            if (bolt == null) continue;

            double offsetX = (i - 1) * 0.5;          // -0.5, 0, 0.5
            double offsetZ = (i % 2 == 0 ? 0.25 : -0.25);

            bolt.refreshPositionAndAngles(
                    target.getX() + offsetX,
                    target.getY(),
                    target.getZ() + offsetZ,
                    0f, 0f
            );
            bolt.setChanneler(null);
            bolt.setCosmetic(false);
            world.spawnEntity(bolt);
        }
    }

    // Passive: Grants haste 2 while held
    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (world.isClient()) return;
        if (entity instanceof PlayerEntity player) {
            boolean holding = selected || player.getOffHandStack() == stack;
            if (holding) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 45, 1, true, false, true));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Grants Haste II").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Active: Causes the next attack to strike multiple lightnings").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 30s").formatted(Formatting.GRAY));
    }

}
