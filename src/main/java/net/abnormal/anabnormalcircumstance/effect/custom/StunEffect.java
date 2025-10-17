package net.abnormal.anabnormalcircumstance.effect.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class StunEffect extends StatusEffect {
    public StunEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.setVelocity(0, entity.getVelocity().y, 0);
        entity.velocityDirty = true;
        entity.setJumping(false);

        // for mobs: stop navigation
        if (!entity.getWorld().isClient && entity instanceof net.minecraft.entity.mob.MobEntity mob) {
            mob.getNavigation().stop();
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

}