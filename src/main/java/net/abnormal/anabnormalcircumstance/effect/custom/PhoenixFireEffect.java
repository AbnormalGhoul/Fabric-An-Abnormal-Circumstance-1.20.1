package net.abnormal.anabnormalcircumstance.effect.custom;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class PhoenixFireEffect extends StatusEffect {
    public PhoenixFireEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xFF4500); // fiery orange color
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false; // no passive tick effect
    }
}
