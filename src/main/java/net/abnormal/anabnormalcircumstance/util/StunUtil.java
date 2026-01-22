package net.abnormal.anabnormalcircumstance.util;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;

public final class StunUtil {

    private StunUtil() {}

    public static boolean tryApplyStun(LivingEntity target, int durationTicks, int amplifier) {
        if (target.hasStatusEffect(ModEffects.STUN)
                || target.hasStatusEffect(ModEffects.STUN_IMMUNITY)) {
            return false;
        }

        target.addStatusEffect(
                new StatusEffectInstance(
                        ModEffects.STUN,
                        durationTicks,
                        amplifier,
                        true,   // ambient
                        true    // show particles
                )
        );
        return true;
    }
}

