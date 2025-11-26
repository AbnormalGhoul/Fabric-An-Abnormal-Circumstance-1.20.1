package net.abnormal.anabnormalcircumstance.entity.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;

/**
 * A reusable version of the spider-style melee attack goal.
 * Prevents attacking while carrying passengers and adjusts
 * attack distance similarly to vanilla spiders.
 */
public class SpiderAttackGoal extends MeleeAttackGoal {

    /**
     * @param mob        The mob using this goal.
     * @param speed      Movement speed multiplier during attack.
     * @param pauseWhenIdle  Whether to pause if target not in range.
     */
    public SpiderAttackGoal(MobEntity mob, double speed, boolean pauseWhenIdle) {
        super((PathAwareEntity) mob, speed, pauseWhenIdle);
    }

    // Spiders (and similar mobs) do not attack when ridden or carrying entities.
    @Override
    public boolean canStart() {
        return super.canStart() && !this.mob.hasPassengers();
    }


    // Vanilla spider logic: slightly increased attack reach, using target width in the calculation.
    @Override
    protected double getSquaredMaxAttackDistance(LivingEntity target) {
        return 4.0F + target.getWidth();
    }
}

