package net.abnormal.anabnormalcircumstance.entity.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;

public class NearestPlayerTargetGoal extends Goal {
    private final HostileEntity mob;
    private PlayerEntity target;
    private int cooldown = 0;

    public NearestPlayerTargetGoal(HostileEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    @Override
    public boolean canStart() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        cooldown = 20; // check once per second

        World world = mob.getWorld();
        double range = 16.0;
        if (mob.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE) != null) {
            range = mob.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).getValue();
        }

        List<? extends PlayerEntity> players = world.getPlayers();
        PlayerEntity nearest = null;
        double nearestSq = Double.MAX_VALUE;
        double rangeSq = range * range;

        for (PlayerEntity p : players) {
            if (!p.isAlive() || p.isSpectator()) continue;
            double dx = mob.getX() - p.getX();
            double dy = mob.getY() - p.getY();
            double dz = mob.getZ() - p.getZ();
            double sq = dx * dx + dy * dy + dz * dz;
            if (sq <= rangeSq && sq < nearestSq) {
                nearest = p;
                nearestSq = sq;
            }
        }

        if (nearest == null) return false;
        this.target = nearest;
        return true;
    }

    @Override
    public void start() {
        mob.setTarget(this.target);
    }

    @Override
    public boolean shouldContinue() {
        // End the goal quickly; target remains set on the mob.
        return false;
    }

    @Override
    public void stop() {
        this.target = null;
    }
}
