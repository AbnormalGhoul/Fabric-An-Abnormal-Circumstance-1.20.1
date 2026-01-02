package net.abnormal.anabnormalcircumstance.entity.custom.projectile;

import net.abnormal.anabnormalcircumstance.entity.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HatchetProjectileEntity extends PersistentProjectileEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private boolean inGround = false;
    private int despawnCounter = 20 * 60;

    public HatchetProjectileEntity(EntityType<? extends HatchetProjectileEntity> type, World world) {
        super(type, world);
    }

    public HatchetProjectileEntity(World world, LivingEntity owner) {
        this(ModEntities.HATCHET_PROJECTILE, world);
        this.setOwner(owner);
    }

    public int getDespawnCounter() { return despawnCounter; }
    public void setDespawnCounter(int ticks) { this.despawnCounter = ticks; }

    @Override
    protected ItemStack asItemStack() { return ItemStack.EMPTY; }

    @Override
    protected void onEntityHit(EntityHitResult hit) {
        super.onEntityHit(hit);

        float damage = 30.0f;
        DamageSource src = this.getDamageSources().thrown(this, this.getOwner());
        hit.getEntity().damage(src, damage);

        // Knockback
        Vec3d direction = this.getVelocity().normalize().multiply(1.5);
        hit.getEntity().addVelocity(direction.x, 0.25, direction.z);
        hit.getEntity().velocityModified = true;

        this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 0.5f);
        this.setNoGravity(true);
        this.setVelocity(Vec3d.ZERO);
        this.inGround = true;
        this.age = 0;
        this.setDespawnCounter(6000);
    }

    @Override
    protected void onBlockHit(net.minecraft.util.hit.BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        Vec3d vel = this.getVelocity();
        float horizontalMag = (float)Math.sqrt(vel.x * vel.x + vel.z * vel.z);
        this.setYaw((float)(Math.toDegrees(Math.atan2(vel.x, vel.z))));
        this.setPitch((float)(Math.toDegrees(Math.atan2(vel.y, horizontalMag))));
        this.setPosition(blockHitResult.getPos().add(vel.normalize().multiply(0.05)));
        this.setVelocity(Vec3d.ZERO);
        this.setNoGravity(true);
        this.inGround = true;
        this.age = 0;
        this.setDespawnCounter(6000);
        this.playSound(SoundEvents.ITEM_TRIDENT_HIT_GROUND, 1f, 0.62f);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.inGround) {
            if (this.age++ > this.getDespawnCounter()) {
                this.discard();
            }
            return;
        }

        Vec3d vel = this.getVelocity();
        float horizontalMag = (float)Math.sqrt(vel.x * vel.x + vel.z * vel.z);
        if (horizontalMag > 0.01F) {
            this.setYaw((float)(Math.toDegrees(Math.atan2(vel.x, vel.z))));
            this.setPitch((float)(Math.toDegrees(Math.atan2(vel.y, horizontalMag))));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "hatchet_idle", 5, event -> {
            if (!this.inGround) return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
            return PlayState.STOP;
        }));
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
