package net.abnormal.anabnormalcircumstance.mixin;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityRageIncomingMixin {

    @Inject(
            method = "modifyAppliedDamage",
            at = @At("RETURN"),
            cancellable = true
    )
    private void applyRageIncoming(
            DamageSource source,
            float amount,
            CallbackInfoReturnable<Float> cir
    ) {
        LivingEntity self = (LivingEntity)(Object)this;

        // Match vanilla behavior
        if (source.isIn(DamageTypeTags.BYPASSES_EFFECTS)) return;
        if (source.isIn(DamageTypeTags.BYPASSES_RESISTANCE)) return;

        StatusEffectInstance rage = self.getStatusEffect(ModEffects.RAGE);
        if (rage == null) return;

        int level = rage.getAmplifier() + 1;

        // +10% damage taken per level
        float multiplier = 1.0F + (0.10F * level);

        cir.setReturnValue(cir.getReturnValue() * multiplier);
    }
}
