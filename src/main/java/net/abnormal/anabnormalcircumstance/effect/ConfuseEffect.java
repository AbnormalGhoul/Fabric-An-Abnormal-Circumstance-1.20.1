package net.abnormal.anabnormalcircumstance.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class ConfuseEffect extends StatusEffect {
    public ConfuseEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    public static boolean isConfused(net.minecraft.entity.LivingEntity entity) {
        return entity.hasStatusEffect(ModEffects.CONFUSION);
    }

}
