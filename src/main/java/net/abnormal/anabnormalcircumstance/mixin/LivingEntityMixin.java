package net.abnormal.anabnormalcircumstance.mixin;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.abnormal.anabnormalcircumstance.item.custom.unique.GeoBladeItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    // Geo Blade Damage Block
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity)(Object)this;
        if (entity instanceof PlayerEntity player) {
            if (GeoBladeItem.tryBlockDamage(player)) {
                cir.setReturnValue(false);
            }
        }
    }

    // Bitten Effect
    @Inject(method = "heal", at = @At("HEAD"), cancellable = true)
    private void anabnormalcircumstance$blockNaturalRegen(float amount, CallbackInfo ci) {
        LivingEntity self = (LivingEntity)(Object)this;

        // Only block NATURAL regeneration (small heal amounts)
        // Potions, spells, golden apples all heal > 1.0f
        boolean isNaturalRegen = amount <= 1.0f;

        if (isNaturalRegen && self.hasStatusEffect(ModEffects.BITTEN)) {
            ci.cancel();   // block the heal
        }
    }
}
