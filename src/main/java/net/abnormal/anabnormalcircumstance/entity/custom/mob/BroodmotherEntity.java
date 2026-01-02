package net.abnormal.anabnormalcircumstance.entity.custom.mob;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.abnormal.anabnormalcircumstance.entity.ModEntities;
import net.abnormal.anabnormalcircumstance.entity.goal.SpiderTargetGoal;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;

public class BroodmotherEntity extends HostileEntity implements GeoEntity {

    // GeckoLib
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation RUSH = RawAnimation.begin().thenLoop("rush");
    private static final RawAnimation ATK = RawAnimation.begin().thenPlay("atk");

    // Boss Bar
    private final ServerBossBar bossBar =
            new ServerBossBar(
                    Text.literal("Broodmother"),
                    ServerBossBar.Color.PURPLE,
                    ServerBossBar.Style.PROGRESS
            );

    // State
    private boolean phaseTwo = false;
    private int specialAtkCooldown = 0;
    private long lastDamageTime = 0;
    private int stunDelayTicks = -1;
    private boolean stunTriggered = false;

    // Spider spawn tracking
    private float lastHealthCheckpoint = 0;

    // Constructor
    public BroodmotherEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
    }

    // Attributes
    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 3000.0D) // doubled health
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 20.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 20.0D);
    }

    // AI
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new PounceAtTargetGoal(this, 0.5F));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.7D));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 12.0F));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new SpiderTargetGoal<>(this, IronGolemEntity.class));
    }

    // Boss Bar Sync
    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        bossBar.removePlayer(player);
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        bossBar.clearPlayers();
    }

    // Tick Logic
    @Override
    public void tick() {
        super.tick();

        bossBar.setPercent(this.getHealth() / this.getMaxHealth());

        // Spider spawn threshold check
        if (!this.getWorld().isClient) {
            if (lastHealthCheckpoint == 0) lastHealthCheckpoint = this.getMaxHealth();
            float lost = lastHealthCheckpoint - this.getHealth();
            if (lost >= 100) {
                spawnSpiderWave(5);
                lastHealthCheckpoint = this.getHealth();
            }
        }

        // Stun Attack Execution
        if (stunDelayTicks >= 0) {
            stunDelayTicks--;
            if (stunDelayTicks == 0 && !stunTriggered) {
                stunTriggered = true;
                executeGroundStun();
            }
        }

        // Phase 2 Trigger
        if (!phaseTwo && this.getHealth() <= this.getMaxHealth() / 2) {
            enterPhaseTwo();
        }

        // Phase 2 Aura
        if (phaseTwo && this.getWorld() instanceof ServerWorld serverWorld) {
            double r = 0.7;
            for (int i = 0; i < 8; i++) {
                double a = i * (Math.PI * 2 / 8);
                serverWorld.spawnParticles(
                        ParticleTypes.PORTAL,
                        getX() + Math.cos(a) * r,
                        getY() + 1.0,
                        getZ() + Math.sin(a) * r,
                        1, 0, 0, 0, 0
                );
            }
        }

        // Special Attack Cooldown
        if (specialAtkCooldown > 0) specialAtkCooldown--;
        if (specialAtkCooldown <= 0 && this.getTarget() != null) {
            performGroundStun();
            specialAtkCooldown = 300; // 15s
        }

        // Regen if not hit
        if (this.age - lastDamageTime > 300) {
            this.heal(this.getMaxHealth() * 0.05F);
            lastDamageTime = this.age;
        }
    }

    private void spawnSpiderWave(int count) {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return;
        for (int i = 0; i < count; i++) {
            BroodWarriorEntity warrior = ModEntities.BROOD_WARRIOR.create(serverWorld);
            if (warrior != null) {
                double angle = i * (2 * Math.PI / count);
                double radius = 2.0;
                double x = getX() + Math.cos(angle) * radius;
                double z = getZ() + Math.sin(angle) * radius;

                warrior.refreshPositionAndAngles(x, getY(), z, random.nextFloat() * 360F, 0);
                warrior.setSummonedByBoss(true);
                serverWorld.spawnEntity(warrior);
            }
        }

        // Particle effect to emphasize spawning
        serverWorld.playSound(null, getBlockPos(), SoundEvents.ENTITY_SPIDER_AMBIENT, SoundCategory.HOSTILE, 1.2F, 1.2F);
        serverWorld.spawnParticles(
                ParticleTypes.SMOKE,
                getX(), getY() + 1, getZ(),
                40, 1.2, 0.4, 1.2, 0.05
        );
    }

    // Knockback Immunity
    @Override
    public void takeKnockback(double strength, double x, double z) {}

    // Phase Two
    private void enterPhaseTwo() {
        phaseTwo = true;
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    ParticleTypes.PORTAL,
                    getX(), getY() + 1, getZ(),
                    80, 1.2, 0.4, 1.2, 0.1
            );
        }

        this.playSound(SoundEvents.ENTITY_ENDERMAN_SCREAM, 1.2F, 0.6F);

        Objects.requireNonNull(getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                .setBaseValue(getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * 1.5);
        Objects.requireNonNull(getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED))
                .setBaseValue(getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 1.5);
    }

    // AoE Stun Attack
    private void performGroundStun() {
        if (this.getWorld().isClient()) return;
        triggerAnim("attack", "atk");
        this.stunDelayTicks = 30;
        this.stunTriggered = false;
    }

    private void executeGroundStun() {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return;

        this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.2F, 0.8F);

        Box box = this.getBoundingBox().expand(5);
        this.getWorld().getEntitiesByClass(PlayerEntity.class, box, p -> true)
                .forEach(p -> p.addStatusEffect(new StatusEffectInstance(ModEffects.STUN, 50, 0)));

        BlockStateParticleEffect stone =
                new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.STONE.getDefaultState());
        serverWorld.spawnParticles(stone, getX(), getY(), getZ(), 60, 2.0, 0.2, 2.0, 0.15);
    }

    // Combat
    @Override
    public boolean tryAttack(Entity target) {
        boolean hit = super.tryAttack(target);

        if (hit && target instanceof LivingEntity living) {
            this.swingHand(Hand.MAIN_HAND);
            living.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60, 1));

            // Phase 2: 15% chance to inflict Confusion for 3 seconds
            if (phaseTwo && this.random.nextFloat() < 0.15F) {
                living.addStatusEffect(new StatusEffectInstance(ModEffects.CONFUSION, 60, 0));
            }
        }
        return hit;
    }

    // Damage handler (removed old spider spawn logic)
    @Override
    public boolean damage(DamageSource source, float amount) {
        lastDamageTime = this.age;
        return super.damage(source, amount);
    }

    @Override
    public void checkDespawn() {}
    @Override
    public boolean cannotDespawn() { return true; }

    // Status Handling
    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        return effect.getEffectType() != ModEffects.BLEEDING && super.canHaveStatusEffect(effect);
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.ARTHROPOD;
    }

    // GeckoLib Animations
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "move", 5, event ->
                event.isMoving() ? event.setAndContinue(RUSH) : event.setAndContinue(IDLE)));

        controllers.add(new AnimationController<>(this, "attack", 0, event -> PlayState.CONTINUE)
                .triggerableAnim("atk", ATK));
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
