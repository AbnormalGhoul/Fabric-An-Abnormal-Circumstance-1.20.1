package net.abnormal.anabnormalcircumstance.entity.custom.mob;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.abnormal.anabnormalcircumstance.entity.custom.projectile.HatchetProjectileEntity;
import net.abnormal.anabnormalcircumstance.entity.goal.NearestPlayerTargetGoal;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ShieldItem;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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

import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

// OrcChampionEntity – custom boss with 2 phases, projectile attacks, AoE stun, and boss bar.
public class OrcChampionEntity extends HostileEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossBar bossBar =
            new ServerBossBar(Text.literal("Orc Champion"), ServerBossBar.Color.RED, ServerBossBar.Style.PROGRESS);

    private static final double BOSS_BAR_RANGE = 48.0;
    private boolean phaseTwo = false;
    private long lastDamageTime = 0;
    private int atk4Cooldown = 0;

    // Used for the second hatchet throw delay
    private int atk4SecondProjectileDelay = 0;
    private boolean atk4FiredFirst = false;

    // Animations
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATK3_ANIM = RawAnimation.begin().thenPlay("atk3");
    private static final RawAnimation ATK4_ANIM = RawAnimation.begin().thenPlay("atk4");
    private static final RawAnimation ATK2_ANIM = RawAnimation.begin().thenPlay("atk2");

    public OrcChampionEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
    }

    // Attributes
    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1000.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 30.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.30D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0D)
                .add(EntityAttributes.GENERIC_ARMOR, 20.0D);
    }

    // AI Goals
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2D, false)); // atk3
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.8D));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));

        this.targetSelector.add(1, new NearestPlayerTargetGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
    }

    // Knockback Immunity
    @Override
    public void takeKnockback(double strength, double x, double z) {}

    @Override
    public void checkDespawn() {
        // Prevents natural despawning
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    // Damage / Healing
    @Override
    public boolean damage(DamageSource source, float amount) {
        // Immune to bleeding damage source
        if (source.getName().equals("magic") && this.hasStatusEffect(ModEffects.BLEEDING)) return false;

        lastDamageTime = this.age;
        return super.damage(source, amount);
    }

    // Boss Bar Management
    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        if (isPlayerInBossBarRange(player)) {
            bossBar.addPlayer(player);
        }
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

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        bossBar.clearPlayers();
    }

    // Tick Logic
    @Override
    public void tick() {
        super.tick();

        // Update boss bar percentage
        if (!this.getWorld().isClient() && this.isAlive()) {
            bossBar.setPercent(this.getHealth() / this.getMaxHealth());

            for (ServerPlayerEntity player : ((ServerWorld) this.getWorld()).getPlayers()) {
                boolean inRange = isPlayerInBossBarRange(player);
                boolean hasBar = bossBar.getPlayers().contains(player);

                if (inRange && !hasBar) {
                    bossBar.addPlayer(player);
                } else if (!inRange && hasBar) {
                    bossBar.removePlayer(player);
                }
            }
        }

        // Phase 2 trigger
        if (!phaseTwo && this.getHealth() <= this.getMaxHealth() / 2) {
            enterPhaseTwo();
        }

        // Subtle fiery aura in Phase 2
        if (phaseTwo && this.getWorld() instanceof ServerWorld serverWorld) {
            double radius = 0.6; // small aura
            int particles = 8;
            for (int i = 0; i < particles; i++) {
                double angle = 2 * Math.PI * i / particles;
                double x = this.getX() + radius * Math.cos(angle);
                double z = this.getZ() + radius * Math.sin(angle);
                double y = this.getY() + 1.0 + this.random.nextDouble() * 0.3; // slightly above boss
                serverWorld.spawnParticles(
                        ParticleTypes.FLAME,
                        x, y, z,
                        1, 0.0, 0.0, 0.0, 0.0
                );
            }
        }

        // Heal if idle for 15s
        if (this.age - lastDamageTime > 300) {
            this.heal(this.getMaxHealth() * 0.05F);
            lastDamageTime = this.age;
        }

        // Handle second hatchet after 1s
        if (atk4SecondProjectileDelay > 0) {
            atk4SecondProjectileDelay--;
            if (atk4SecondProjectileDelay == 0 && atk4FiredFirst) {
                spawnSecondHatchet();
                atk4FiredFirst = false;
            }
        }

        // Handle atk4 cooldown and attack selection
        if (atk4Cooldown > 0) atk4Cooldown--;
        if (atk4Cooldown <= 0 && this.getTarget() != null && this.isAlive()) {
            if (phaseTwo && new Random().nextBoolean()) {
                performAtk2();
            } else {
                performAtk4();
            }
            atk4Cooldown = 400; // every 20s
        }
    }

    // Helper Method for Boss bar
    private boolean isPlayerInBossBarRange(ServerPlayerEntity player) {
        double dx = Math.abs(player.getX() - this.getX());
        double dy = Math.abs(player.getY() - this.getY());
        double dz = Math.abs(player.getZ() - this.getZ());

        return dx <= BOSS_BAR_RANGE && dy <= BOSS_BAR_RANGE && dz <= BOSS_BAR_RANGE;
    }

    // Phase Two Behavior
    private void enterPhaseTwo() {
        phaseTwo = true;

        // Flame particles + knockback nearby entities
        this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(6), e -> e != this)
                .forEach(e -> {
                    Vec3d vec = e.getPos().subtract(this.getPos()).normalize().multiply(1.0);
                    e.addVelocity(vec.x, 0.5, vec.z);
                    e.velocityModified = true;

                    if (this.getWorld() instanceof ServerWorld serverWorld) {
                        serverWorld.spawnParticles(ParticleTypes.FLAME, e.getX(), e.getY() + 1, e.getZ(), 8, 0.2, 0.2, 0.2, 0.01);
                    }
                });

        // Boost stats
        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                .setBaseValue(45.0D);

        this.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.2f, 0.8f);
    }

    // ATK4 – Hatchet Throw
    private void performAtk4() {
        if (this.getWorld().isClient() || this.getTarget() == null) return;

        this.getNavigation().stop();
        triggerAnim("attack", "atk4");

        Vec3d facing = this.getRotationVec(1.0f).normalize();
        double spawnX = this.getX() + facing.x * 1.2;
        double spawnY = this.getEyeY() - 0.1;
        double spawnZ = this.getZ() + facing.z * 1.2;

        HatchetProjectileEntity hatchet1 = new HatchetProjectileEntity(this.getWorld(), this);
        hatchet1.setPosition(spawnX, spawnY, spawnZ);
        spawnProjectileToTarget(hatchet1, this.getTarget());
        this.getWorld().spawnEntity(hatchet1);

        atk4SecondProjectileDelay = 20; // 1s later
        atk4FiredFirst = true;
    }

    private void spawnSecondHatchet() {
        if (this.getWorld().isClient() || this.getTarget() == null) return;

        Vec3d facing = this.getRotationVec(1.0f).normalize();
        double spawnX = this.getX() + facing.x * 1.2;
        double spawnY = this.getEyeY() - 0.1;
        double spawnZ = this.getZ() + facing.z * 1.2;

        HatchetProjectileEntity hatchet2 = new HatchetProjectileEntity(this.getWorld(), this);
        hatchet2.setPosition(spawnX, spawnY, spawnZ);
        spawnProjectileToTarget(hatchet2, this.getTarget());
        this.getWorld().spawnEntity(hatchet2);
    }

    // ATK2 – Pyroclasm Wave (replaces old stun ability)
    private void performAtk2() {
        if (this.getWorld().isClient()) return;
        ServerWorld world = (ServerWorld) this.getWorld();

        triggerAnim("attack", "atk2");

        // Main explosion sound + small burst
        world.playSound(null, this.getBlockPos(), SoundEvents.ENTITY_GENERIC_EXPLODE,
                this.getSoundCategory(), 1.4f, 1.2f);

        world.spawnParticles(
                ParticleTypes.LAVA,
                this.getX(), this.getY() + 0.1, this.getZ(),
                30,
                0.4, 0.1, 0.4,
                0.05
        );

        // Schedule 5 waves (5 ticks apart)
        for (int wave = 0; wave < 5; wave++) {
            int delay = wave * 10;
            int waveNumber = wave;
            scheduleTask(world, delay, () -> spawnPyroWave(world, waveNumber));
        }
    }

    // Handles each expanding wave
    private void spawnPyroWave(ServerWorld world, int waveIndex) {
        double maxRadius = 10.0;
        double waves = 5.0;
        double radius = (maxRadius / waves) * (waveIndex + 1);

        Vec3d center = new Vec3d(this.getX(), this.getY() + 0.1, this.getZ());

        // Create ring particles
        int points = 40;
        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI * i) / points;
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);

            world.spawnParticles(ParticleTypes.FLAME, x, center.y, z,
                    3, 0.04, 0.01, 0.04, 0.02);
            world.spawnParticles(ParticleTypes.SMOKE, x, center.y, z,
                    2, 0.06, 0.02, 0.06, 0.01);
        }

        // Damage and ignite nearby entities
        Box area = new Box(
                center.x - radius, center.y - 1, center.z - radius,
                center.x + radius, center.y + 2, center.z + radius
        );

        world.getEntitiesByClass(LivingEntity.class, area, e -> e != this)
                .forEach(target -> {
                    target.damage(world.getDamageSources().mobAttack(this), 15.0f);
                    target.setOnFireFor(5);
                });

        // Subtle whoosh sound
        world.playSound(
                null, this.getBlockPos(),
                SoundEvents.BLOCK_LAVA_EXTINGUISH,
                this.getSoundCategory(),
                0.8f, 1.4f
        );
    }

    // Simple delayed task scheduler for waves
    private static final java.util.List<DelayedTask> DELAYED_TASKS = new java.util.ArrayList<>();

    private static void scheduleTask(ServerWorld world, int delayTicks, Runnable run) {
        DELAYED_TASKS.add(new DelayedTask(world.getTime() + delayTicks, world, run));
    }

    private record DelayedTask(long executeTime, ServerWorld world, Runnable run) {}

    static {
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_WORLD_TICK.register(world -> {
            Iterator<DelayedTask> it = DELAYED_TASKS.iterator();
            while (it.hasNext()) {
                DelayedTask task = it.next();
                if (task.world.getTime() >= task.executeTime) {
                    task.run.run();
                    it.remove();
                }
            }
        });
    }

    // Utility: Projectile Spawn
    private void spawnProjectileToTarget(HatchetProjectileEntity projectile, LivingEntity target) {
        double dx = target.getX() - projectile.getX();
        double dy = target.getEyeY() - projectile.getY();
        double dz = target.getZ() - projectile.getZ();
        projectile.setVelocity(dx, dy, dz, 1.8f, 2.0f);
        this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0f, 1.0f);
    }

    // Attack Logic with Shield Disabling
    @Override
    public boolean tryAttack(Entity target) {
        triggerAnim("attack", "atk3");
        this.swingHand(Hand.MAIN_HAND);
        this.playSound(SoundEvents.ENTITY_WITCH_THROW, 0.8F, 0.6F);

        if (target instanceof PlayerEntity player) {
            // If the player is using an item and that active item is a shield,
            // disable the shield in the hand they're actively using.
            if (player.isUsingItem() && player.getActiveItem().getItem() instanceof ShieldItem) {
                if (player.getActiveHand() == Hand.OFF_HAND) {
                    disablePlayerOffhandShield(player);
                } else {
                    disablePlayerShield(player);
                }
                return true;
            }
        }

        // Perform normal attack
        boolean hit = super.tryAttack(target);

        // Apply Fire Aspect during Phase 2
        if (hit && phaseTwo && target instanceof LivingEntity livingTarget) {
            livingTarget.setOnFireFor(5);

            // Fiery impact sound + small flame burst
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(
                        ParticleTypes.FLAME,
                        target.getX(),
                        target.getY() + target.getHeight() / 2,
                        target.getZ(),
                        10, 0.3, 0.3, 0.3, 0.02
                );
                this.getWorld().playSound(
                        null,
                        target.getBlockPos(),
                        SoundEvents.ITEM_FIRECHARGE_USE,
                        this.getSoundCategory(),
                        1.0F,
                        1.2F
                );
            }
        }

        return hit;
    }

    // Utility to disable main-hand shield
    private void disablePlayerShield(PlayerEntity player) {
        player.clearActiveItem();
        player.getItemCooldownManager().set(player.getMainHandStack().getItem(), 100);
        player.getWorld().playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.ITEM_SHIELD_BREAK,
                player.getSoundCategory(),
                1.0F,
                0.8F + player.getRandom().nextFloat() * 0.4F
        );
    }

    // Utility to disable offhand shield
    private void disablePlayerOffhandShield(PlayerEntity player) {
        player.clearActiveItem();
        player.getItemCooldownManager().set(player.getOffHandStack().getItem(), 100);
        player.getWorld().playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.ITEM_SHIELD_BREAK,
                player.getSoundCategory(),
                1.0F,
                0.8F + player.getRandom().nextFloat() * 0.4F
        );
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        this.tickHandSwing();
    }

    // Geckolib Animation Controllers
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Movement
        controllers.add(new AnimationController<>(this, "movement", 5, event -> {
            if (event.isMoving()) return event.setAndContinue(WALK_ANIM);
            return event.setAndContinue(IDLE_ANIM);
        }));

        // Attack
        controllers.add(new AnimationController<>(this, "attack", 0, event -> PlayState.CONTINUE)
                .triggerableAnim("atk3", ATK3_ANIM)
                .triggerableAnim("atk4", ATK4_ANIM)
                .triggerableAnim("atk2", ATK2_ANIM));
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
