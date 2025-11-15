package net.abnormal.anabnormalcircumstance.entity.custom.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

//OrcWarriorEntity — uses vanilla melee damage and triggers GeckoLib attack animation via a synced trigger.
public class OrcWarriorEntity extends HostileEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("atk3");

    // 1.75s = 35 ticks
    private static final int ATTACK_ANIMATION_TICKS = 35;

    public OrcWarriorEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
    }

    // Attributes
    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 16.0D)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 18.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 16.0D);
    }

    // Goals
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.75D));
        this.goalSelector.add(3, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
    }

    // Immediate damage trigger
    @Override
    public boolean tryAttack(Entity target) {
        boolean hit = super.tryAttack(target);
        if (hit) {
            // Triggers Geckolib ATTACK controller
            this.swingHand(Hand.MAIN_HAND);
            // Play attack sound
            this.playSound(SoundEvents.ENTITY_WITCH_THROW, 0.8F, 0.6F);
        }

        return hit;
    }

    // Geckolib requires swing time update to sync animations
    @Override
    public void tickMovement() {
        super.tickMovement();
        this.tickHandSwing();
    }

//    // Match animation duration to swing duration
//    // Prevents speed-up, cut-off, and spam
//    @Override
//    public int getCurrentSwingDuration() {
//        return ATTACK_ANIMATION_TICKS;
//    }

    // Animation Controllers
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

        // Movement / idle controller
        controllers.add(new AnimationController<>(this, "movement_controller", 0, event -> {

            if (event.isMoving()) {
                event.setAndContinue(WALK_ANIM);
            } else {
                event.setAndContinue(IDLE_ANIM);
            }

            return PlayState.CONTINUE;
        }));

        // Attack controller
        controllers.add(new AnimationController<>(this, "attack_controller", 0, event -> {

            AnimationController<?> controller = event.getController();

            // When swing starts AND controller is not animating: play attack animation
            if (this.handSwinging && controller.getAnimationState() == AnimationController.State.STOPPED) {

                controller.forceAnimationReset();
                controller.setAnimation(ATTACK_ANIM);

                // stops handSwinging so animation doesn't loop
                this.handSwinging = false;
            }

            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // Baby override (required)
    @Override
    public boolean isBaby() {
        return false;
    }
}