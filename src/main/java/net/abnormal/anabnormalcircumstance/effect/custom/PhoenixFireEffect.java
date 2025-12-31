package net.abnormal.anabnormalcircumstance.effect.custom;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class PhoenixFireEffect extends StatusEffect {
    public PhoenixFireEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false; // no passive tick effect
    }
}
