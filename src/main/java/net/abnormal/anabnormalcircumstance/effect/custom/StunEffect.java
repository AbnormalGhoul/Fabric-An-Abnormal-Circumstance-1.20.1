package net.abnormal.anabnormalcircumstance.effect.custom;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;

public class StunEffect extends StatusEffect {
    public StunEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        // Gravity baseline (~0.08)
        double gravity = entity.hasNoGravity() ? 0.0 : 0.08;

        // Gets current velocity
        double vy = entity.getVelocity().y;

        // Stops all horizontal motion
        double vx = 0.0;
        double vz = 0.0;

        // Vertical logic
        // Prevent jumping / upward motion
        if (vy > 0) vy = 0;

        // Apply gravity if applicable
        if (!entity.hasNoGravity()) vy -= gravity;

        // Apply final velocity
        entity.setVelocity(vx, vy, vz);
        entity.velocityDirty = true;
        entity.setJumping(false);

        // Disable AI movement / targeting
        if (!entity.getWorld().isClient && entity instanceof MobEntity mob) {
            mob.getNavigation().stop();
            mob.getMoveControl().moveTo(mob.getX(), mob.getY(), mob.getZ(), 0);
            mob.getLookControl().lookAt(mob.getX(), mob.getEyeY(), mob.getZ());
        }
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);

        if (!entity.getWorld().isClient) {
            entity.addStatusEffect(
                    new StatusEffectInstance(
                            ModEffects.STUN_IMMUNITY,
                            20 * 3,
                            0,
                            true,
                            true
                    )
            );
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true; // apply every tick
    }
}
