package net.abnormal.anabnormalcircumstance.entity.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;

/**
 * A reusable target-selection goal modeled after the vanilla spider:
 * targets specified entity classes with basic pursuit behavior.
 *
 * @param <T> Target type (e.g., PlayerEntity, IronGolemEntity, etc.)
 */
public class SpiderTargetGoal<T extends LivingEntity> extends ActiveTargetGoal<T> {

    /**
     * @param mob          The mob using this goal.
     * @param targetClass  The class of living entity to target.
     * @param checkVisibility Whether to check line-of-sight before targeting.
     */
    public SpiderTargetGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility) {
        super(mob, targetClass, checkVisibility);
    }

    /**
     * Convenience constructor matching vanilla spider behavior
     * (visibility checks enabled).
     */
    public SpiderTargetGoal(MobEntity mob, Class<T> targetClass) {
        super(mob, targetClass, true);
    }
}
