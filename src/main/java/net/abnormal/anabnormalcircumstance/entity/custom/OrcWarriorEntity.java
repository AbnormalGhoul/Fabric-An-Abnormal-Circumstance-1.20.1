package net.abnormal.anabnormalcircumstance.entity.custom;

import net.abnormal.anabnormalcircumstance.entity.goal.OrcMeleeAttackGoal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.Hand;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * OrcWarrior hostile mob entity.
 * Handles AI, attack logic, and animations (idle, walk, atk3).
 */
public class OrcWarriorEntity extends HostileEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("atk3");

    // Timings for attack logic
    private static final int ATTACK_ANIM_LENGTH = 35;  // ticks
    private static final int ATTACK_DAMAGE_TICK = 15;  // when to deal damage
    private static final int ATTACK_COOLDOWN = 5;      // delay after attack ends

    private static final TrackedData<Boolean> ATTACKING =
            DataTracker.registerData(OrcWarriorEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int attackTick = 0;  // counts through attack animation
    private boolean hasDealtDamage = false;

    public OrcWarriorEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    // Entity Attributes
    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 16.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 16.0D);
    }

    // Entity Goals
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new OrcMeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.75D));
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (isAttacking()) {
            attackTick++;

            if (attackTick == ATTACK_DAMAGE_TICK && getTarget() != null && getTarget().isAlive() && !hasDealtDamage) {
                double distanceSq = this.squaredDistanceTo(getTarget());
                double reach = this.getWidth() * 2.5D + getTarget().getWidth();
                if (distanceSq <= reach * reach) {
                    super.tryAttack(getTarget());
                    this.getWorld().playSound(null, this.getBlockPos(),
                            SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.HOSTILE, 1.0F,
                            0.9F + this.random.nextFloat() * 0.2F);
                }
                hasDealtDamage = true;
            }

            if (attackTick >= ATTACK_ANIM_LENGTH + ATTACK_COOLDOWN) {
                attackTick = 0;
                setAttacking(false);
                hasDealtDamage = false;
            }
        }
    }

    public boolean isAttacking() {
        return this.dataTracker.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.dataTracker.set(ATTACKING, attacking);
    }

    // Start the attack animation sequence without applying damage immediately
    // Called by the custom MeleeAttackGoal so damage is applied at the correct animation tick
    public void startAttack(LivingEntity target) {
        if (!isAttacking()) {
            setAttacking(true);
            attackTick = 0;
            hasDealtDamage = false;
            this.swingHand(Hand.MAIN_HAND);
        }
    }

    // Override to perform the actual hit/damage using vanilla logic when called (tick damage moment)
    @Override
    public boolean tryAttack(Entity target) {
        return super.tryAttack(target);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACKING, false);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 0.15F, 1.0F);
    }

    // Animation Controller
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private <E extends GeoEntity> PlayState predicate(AnimationState<E> state) {
        AnimationController<?> controller = state.getController();

        if (isAttacking()) {
            controller.setAnimation(ATTACK_ANIM);
            return PlayState.CONTINUE;
        }

        if (state.isMoving()) {
            controller.setAnimation(WALK_ANIM);
            return PlayState.CONTINUE;
        }

        controller.setAnimation(IDLE_ANIM);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return super.damage(source, amount);
    }
}