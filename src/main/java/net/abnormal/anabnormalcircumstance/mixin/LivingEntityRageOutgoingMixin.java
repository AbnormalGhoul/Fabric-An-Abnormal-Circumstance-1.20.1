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
public abstract class LivingEntityRageOutgoingMixin {

    @ModifyVariable(
            method = "damage",
            at = @At("HEAD"),
            argsOnly = true
    )
    private float applyRageOutgoing(float amount, DamageSource source) {

        Entity attacker = source.getAttacker();
        if (!(attacker instanceof LivingEntity livingAttacker)) {
            return amount;
        }

        if (source.isIn(DamageTypeTags.BYPASSES_EFFECTS)) {
            return amount;
        }

        StatusEffectInstance rage =
                livingAttacker.getStatusEffect(ModEffects.RAGE);

        if (rage == null) {
            return amount;
        }

        int level = rage.getAmplifier() + 1;

        // +10% damage dealt per level
        return amount * (1.0F + 0.10F * level);
    }
}


