package net.abnormal.anabnormalcircumstance.entity.goal;


import net.abnormal.anabnormalcircumstance.entity.custom.OrcWarriorEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;

// Custom melee attack goal for OrcWarrior to handle animation-driven attacks.
public class OrcMeleeAttackGoal extends MeleeAttackGoal {

    private final OrcWarriorEntity orc;

    public OrcMeleeAttackGoal(OrcWarriorEntity orc, double speed, boolean pauseWhenIdle) {
        super(orc, speed, pauseWhenIdle);
        this.orc = orc;
    }

    @Override
    protected void attack(LivingEntity target, double squaredDistance) {
        double reach = this.getSquaredMaxAttackDistance(target);
        if (squaredDistance <= reach && this.isCooledDown()) {
            this.resetCooldown();
            orc.startAttack(target);
        }
    }

}

