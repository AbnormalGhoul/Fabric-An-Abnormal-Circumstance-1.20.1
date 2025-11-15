package net.abnormal.anabnormalcircumstance.entity.custom.mob;

import net.abnormal.anabnormalcircumstance.entity.custom.projectile.JavelinProjectileEntity;
import net.abnormal.anabnormalcircumstance.entity.goal.OrcProjectileAttackGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class OrcJavelinThrowerEntity extends HostileEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("atk1");

    public OrcJavelinThrowerEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new OrcProjectileAttackGoal(this));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.75));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8));
        this.goalSelector.add(4, new LookAroundGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        return super.canTarget(target) && !(target instanceof OrcJavelinThrowerEntity);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0)
                .add(EntityAttributes.GENERIC_ARMOR, 16.0D);
    }

    // server-side only: spawn the projectile
    public void fireJavelin() {
        if (this.getWorld().isClient()) return;

        LivingEntity target = getTarget();
        if (target == null) return;

        Vec3d facing = this.getRotationVec(1.0f).normalize();
        double spawnX = this.getX() + facing.x * 1.2;
        double spawnY = this.getEyeY() - 0.1;
        double spawnZ = this.getZ() + facing.z * 1.2;

        JavelinProjectileEntity javelin = new JavelinProjectileEntity(this.getWorld(), this);
        javelin.setPosition(spawnX, spawnY, spawnZ);

        double dx = target.getX() - spawnX;
        double dy = target.getEyeY() - spawnY;
        double dz = target.getZ() - spawnZ;

        javelin.setVelocity(dx, dy, dz, 1.8f, 2.0f);
        this.getWorld().spawnEntity(javelin);

        this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0f, 1.0f);
    }

    // animations
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "move", 5, event -> {
            if (event.isMoving()) {
                event.setAndContinue(WALK_ANIM);
            } else {
                event.setAndContinue(IDLE_ANIM);
            }
            return PlayState.CONTINUE;
        }));

        controllers.add(new AnimationController<>(this, "attack", 0, event -> PlayState.CONTINUE)
                .triggerableAnim("atk1", ATTACK_ANIM));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean isBaby() {
        return false;
    }
}

