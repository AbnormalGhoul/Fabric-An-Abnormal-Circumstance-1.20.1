package net.abnormal.anabnormalcircumstance.mixin;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityOutgoingDamageMixin {

    @ModifyVariable(
            method = "damage",
            at = @At("HEAD"),
            argsOnly = true
    )
    private float applyOutgoingDamageModifiers(float amount, DamageSource source) {

        Entity attacker = source.getAttacker();
        if (!(attacker instanceof LivingEntity living)) {
            return amount;
        }

        if (source.isIn(DamageTypeTags.BYPASSES_EFFECTS)) {
            return amount;
        }

        float bonusMultiplier = 1.0F;

        // Rage: +10% per level
        StatusEffectInstance rage = living.getStatusEffect(ModEffects.RAGE);
        if (rage != null) {
            int level = rage.getAmplifier() + 1;
            bonusMultiplier += 0.10F * level;
        }

        // Adamantite set: flat +20%
//        if (living.hasStatusEffect(ModEffects.ADAMANTITE_POWER)) {
//            bonusMultiplier += 0.20F;
//        }

        return amount * bonusMultiplier;
    }
}
