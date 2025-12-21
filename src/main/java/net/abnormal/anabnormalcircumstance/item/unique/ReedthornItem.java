package net.abnormal.anabnormalcircumstance.item.unique;

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
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.util.math.Vec3d;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;

import java.util.List;
import java.util.UUID;

public class ReedthornItem extends SwordItem implements UniqueAbilityItem {
    private static final UUID REACH_MODIFIER_ID = UUID.fromString("c8d9f7a0-92b8-4a17-9e9a-0d6aafcf9d03");
    private static final UUID ATTACK_RANGE_MODIFIER_ID = UUID.fromString("c0b8e4a0-9123-4a17-8e2b-0a4b2dcf8d01");

    private static final EntityAttributeModifier REACH_MODIFIER =
            new EntityAttributeModifier(REACH_MODIFIER_ID, "Reedthorn reach bonus", 2.0, EntityAttributeModifier.Operation.ADDITION);
    private static final EntityAttributeModifier ATTACK_RANGE_MODIFIER =
            new EntityAttributeModifier(ATTACK_RANGE_MODIFIER_ID, "Reedthorn attack range bonus", 2.0, EntityAttributeModifier.Operation.ADDITION);

    public ReedthornItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public void useUniqueAbility(PlayerEntity player) {
        World world = player.getWorld();
        if (world.isClient()) return;

        boolean hasMark = TrinketsApi.getTrinketComponent(player)
                .map(comp -> comp.isEquipped(ModItems.CHAMPIONS_CREST))
                .orElse(false);

        if (!hasMark) {
            player.sendMessage(Text.literal("You must equip the Champion's Crest to use this weapon").formatted(Formatting.DARK_RED), true);
            return;
        }

        if (UniqueItemCooldownManager.isOnCooldown(player)) {
            long remaining = UniqueItemCooldownManager.getRemaining(player);
            player.sendMessage(Text.literal("Ability Cooldown (" + (remaining / 1000) + "s)").formatted(Formatting.WHITE), true);
            return;
        }

        // Play dash start sound
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_SCREAM, SoundCategory.PLAYERS, 1.2f, 1.5f);

        // Calculate dash direction (look vector)
        Vec3d look = player.getRotationVec(1.0f).normalize();
        Vec3d dashTarget = player.getPos().add(look.multiply(8));

        // Damage any entities in dash path (exclude teammates)
        Box dashBox = new Box(player.getPos(), dashTarget).expand(1.5);
        List<LivingEntity> hitEntities = world.getEntitiesByClass(LivingEntity.class, dashBox, e -> e != player && e.isAlive() && !player.isTeammate(e));

        for (LivingEntity target : hitEntities) {
            target.damage(world.getDamageSources().playerAttack(player), 10.0F);
            target.addStatusEffect(new StatusEffectInstance(ModEffects.CONFUSION, 100, 0, false, true, true));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 0, false, true, true));
            world.playSound(null, target.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }

        // Perform dash movement
        player.addVelocity(look.x * 2.5, 0.2, look.z * 2.5);
        player.velocityModified = true;

        // Sound and cooldown
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.2f, 1.2f);
        UniqueItemCooldownManager.setCooldown(player, 60 * 1000);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (!(entity instanceof PlayerEntity player)) return;

        boolean holding = selected || player.getOffHandStack() == stack;

        EntityAttributeInstance reachAttr = player.getAttributeInstance(ReachEntityAttributes.REACH);
        EntityAttributeInstance attackAttr = player.getAttributeInstance(ReachEntityAttributes.ATTACK_RANGE);

        if (reachAttr == null || attackAttr == null) return;

        if (holding) {
            if (!reachAttr.hasModifier(REACH_MODIFIER)) {
                reachAttr.addPersistentModifier(REACH_MODIFIER);
            }
            if (!attackAttr.hasModifier(ATTACK_RANGE_MODIFIER)) {
                attackAttr.addPersistentModifier(ATTACK_RANGE_MODIFIER);
            }
        } else {
            if (reachAttr.hasModifier(REACH_MODIFIER)) {
                reachAttr.removeModifier(REACH_MODIFIER_ID);
            }
            if (attackAttr.hasModifier(ATTACK_RANGE_MODIFIER)) {
                attackAttr.removeModifier(ATTACK_RANGE_MODIFIER_ID);
            }
        }
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!target.getWorld().isClient()) {
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60, 1));
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Passive: Extended Attack Reach, And Inflicts Poison II").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Active: Confusion Charge - Confuses all entities in the way of the charge").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Cooldown: 1 min").formatted(Formatting.GRAY));
    }
}
