package net.abnormal.anabnormalcircumstance.mixin;

import net.minecraft.enchantment.PowerEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Post Nether
@Mixin(PowerEnchantment.class)
public class PowerEnchantmentMixin {

    @Inject(method = "getMaxLevel", at = @At("HEAD"), cancellable = true)
    private void increaseMaxLevel(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(7);  // New Max Level
    }
}