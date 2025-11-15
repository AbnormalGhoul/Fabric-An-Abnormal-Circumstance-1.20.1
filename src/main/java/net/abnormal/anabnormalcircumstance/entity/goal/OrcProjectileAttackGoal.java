package net.abnormal.anabnormalcircumstance.entity.goal;

import net.abnormal.anabnormalcircumstance.entity.custom.mob.OrcJavelinThrowerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import java.util.EnumSet;

public class OrcProjectileAttackGoal extends Goal {

    private final OrcJavelinThrowerEntity mob;
    private int cooldown = 0;
    private int throwDelay = -1; // counts down the tick delay

    public OrcProjectileAttackGoal(OrcJavelinThrowerEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void start() {
        cooldown = 10; // short initial delay before first attack
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
        throwDelay = -1;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        double distanceSq = mob.squaredDistanceTo(target);
        double maxAttackDistance = 16 * 16;

        mob.getLookControl().lookAt(target, 30.0f, 30.0f);

        // move toward target if too far
        if (distanceSq > maxAttackDistance * 0.75) {
            mob.getNavigation().startMovingTo(target, 1.0);
        } else {
            mob.getNavigation().stop();
        }

        // countdown to throw if waiting
        if (throwDelay > 0) {
            throwDelay--;
            if (throwDelay == 0) {
                mob.fireJavelin(); // ticks after animation start
            }
        }

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        // attack only if in range and visible
        if (distanceSq <= maxAttackDistance && mob.getVisibilityCache().canSee(target)) {
            mob.triggerAnim("attack", "atk1"); // play throw animation
            throwDelay = 10;                   // wait ticks until throw
            cooldown = 25;                     // attack cooldown
        }
    }
}
