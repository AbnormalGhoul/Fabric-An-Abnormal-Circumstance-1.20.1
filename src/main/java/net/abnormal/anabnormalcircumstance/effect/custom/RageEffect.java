package net.abnormal.anabnormalcircumstance.effect.custom;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

/**
 * Rage
 *
 * +10% damage dealt per level
 * +10% damage taken per level
 */
public class RageEffect extends StatusEffect {

    public RageEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }
}
