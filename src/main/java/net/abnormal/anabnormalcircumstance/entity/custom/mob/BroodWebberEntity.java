package net.abnormal.anabnormalcircumstance.entity.custom.mob;

import net.abnormal.anabnormalcircumstance.entity.ModEntities;
import net.abnormal.anabnormalcircumstance.entity.custom.projectile.BroodWebProjectileEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BroodWebberEntity extends HostileEntity implements GeoEntity {
    private static final TrackedData<Byte> SPIDER_FLAGS =
            DataTracker.registerData(BroodWebberEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Boolean> TRAPPING =
            DataTracker.registerData(BroodWebberEntity.class, TrackedDataHandlerRegistry.BOOLEAN);


    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation TRAP = RawAnimation.begin().thenPlay("trap");

    private boolean isTrapping = false;
    private int trapCooldown = 0;
    private int trapTicks = 0;


    public BroodWebberEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
    }

    // Attributes
    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(3, new WanderAroundGoal(this, 0.8));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (trapCooldown > 0) trapCooldown--;

        if (isTrapping) {
            // freeze movement
            this.getNavigation().stop();
            this.setVelocity(Vec3d.ZERO);
            if (--trapTicks <= 0) {
                shootTrapProjectile();
                isTrapping = false;
                this.dataTracker.set(TRAPPING, false);
                trapCooldown = 200; // 10s
            }
            return;
        }

        if (!this.getWorld().isClient && trapCooldown <= 0) {
            PlayerEntity target = this.getWorld().getClosestPlayer(this, 16.0);
            if (target != null && !target.isCreative() && !target.isSpectator()
                    && this.canSee(target) && trapCooldown <= 0) {
                performTrapAttack(target);
            }
        }
    }

    private void shootTrapProjectile() {
        LivingEntity target = this.getTarget();
        if (target == null) return;

        BroodWebProjectileEntity projectile =
                new BroodWebProjectileEntity(ModEntities.BROOD_WEB_PROJECTILE, this.getWorld(), this);

        Vec3d direction = target.getEyePos().subtract(this.getEyePos()).normalize();

        // adjust projectile spawn position (slightly in front)
        projectile.setPosition(this.getX(), this.getEyeY() - 0.1, this.getZ());

        // increase power for longer range
        double power = 1.5;
        projectile.setVelocity(direction.x * power, direction.y * power, direction.z * power);

        this.getWorld().spawnEntity(projectile);
    }

    public EntityGroup getGroup() {
        return EntityGroup.ARTHROPOD;
    }

    private void performTrapAttack(LivingEntity target) {
        if (isTrapping) return;

        isTrapping = true;
        this.dataTracker.set(TRAPPING, true);
        trapTicks = 40;                     // charge duration
        this.swingHand(Hand.MAIN_HAND);
        this.lookAtEntity(target, 180.0F, 180.0F);
        this.setTarget(target);
    }

    // Animation
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 0, event -> {
            boolean trapping = this.dataTracker.get(TRAPPING);

            AnimationController<?> controller = event.getController();

            // play trap once, without resetting every tick
            if (trapping) {
                if (controller.getCurrentRawAnimation() != TRAP) {
                    controller.setAnimation(TRAP);
                }
                return PlayState.CONTINUE;
            }

            if (event.isMoving()) {
                event.setAndContinue(WALK);
            } else {
                event.setAndContinue(IDLE);
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(SPIDER_FLAGS, (byte) 0);
        this.dataTracker.startTracking(TRAPPING, false);
    }


    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SPIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SPIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SPIDER_DEATH;
    }

    @Override
    public boolean isBaby() {
        return false;
    }
}
