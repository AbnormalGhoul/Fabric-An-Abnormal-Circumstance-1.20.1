package net.abnormal.anabnormalcircumstance.event;


import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.damage.DamageSource;
import net.abnormal.anabnormalcircumstance.magic.spells.aeromancy.SoaringStrideSpell;
import net.abnormal.anabnormalcircumstance.magic.spells.hydromancy.BileWaterSpell;
import net.abnormal.anabnormalcircumstance.magic.spells.hydromancy.ControlWeatherSpell;
import net.abnormal.anabnormalcircumstance.magic.spells.pyromancy.FireAspectSpell;
import net.abnormal.anabnormalcircumstance.magic.spells.pyromancy.MoltenFurySpell;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;


public class ModEvents {
    public static void registerEvents() {
        ModAttackEvent.register();
        WidowsGemEvents.register();
        MarkOfAChampionEvents.register();

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient && entity instanceof LivingEntity target) {

                if (MoltenFurySpell.hasFireHit((ServerPlayerEntity) player)) {
                    target.setOnFireFor(4);
                }

                FireAspectSpell.onEntityAttacked((ServerPlayerEntity) player, target);
                BileWaterSpell.onEntityAttacked(player, target, player.getDamageSources().playerAttack(player));
                ControlWeatherSpell.onEntityAttacked(player, target, player.getDamageSources().playerAttack(player));
            }

            return ActionResult.PASS;
        });

        // Cancel fall damage for players under Soaring Stride protection
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof ServerPlayerEntity player) {
                // Check if the damage source is fall damage
                DamageSource fall = player.getDamageSources().fall();
                if (source.getTypeRegistryEntry() == fall.getTypeRegistryEntry()) {
                    if (SoaringStrideSpell.shouldCancelFallDamage(player)) {
                        return false; // cancel the damage event entirely
                    }
                }
            }
            return true; // allow all other damage
        });

        // Tick every world and spawn aura particles for players with active buff
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            // world is a ServerWorld
            for (ServerPlayerEntity player : world.getPlayers()) {
                SoaringStrideSpell.tick(player);
                MoltenFurySpell.tick(player);
                FireAspectSpell.tick(player);
                ControlWeatherSpell.tick(player);
            }
        });
    }

    private static float getAttackDamage(ServerPlayerEntity player, LivingEntity target) {
        float damage = (float) player.getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.GENERIC_ATTACK_DAMAGE);

        // Critical hits (volume multiplied)
        boolean isCrit =
                player.fallDistance > 0.0F &&
                        !player.isOnGround() &&
                        !player.isClimbing() &&
                        !player.isTouchingWater() &&
                        !player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.BLINDNESS) &&
                        !player.hasVehicle();

        if (isCrit)
            damage *= 1.5f;

        // Strength/Weakness
        if (player.hasStatusEffect(StatusEffects.STRENGTH)) {
            int amp = player.getStatusEffect(StatusEffects.STRENGTH).getAmplifier() + 1;
            damage += amp * 3.0f;
        }
        if (player.hasStatusEffect(StatusEffects.WEAKNESS)) {
            int amp = player.getStatusEffect(StatusEffects.WEAKNESS).getAmplifier() + 1;
            damage -= amp * 4.0f;
        }

        // Enchantments
        damage += EnchantmentHelper.getAttackDamage(player.getMainHandStack(), target.getGroup());

        return damage;
    }
}
