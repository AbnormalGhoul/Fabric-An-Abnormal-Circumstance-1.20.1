package net.abnormal.anabnormalcircumstance.entity.custom.projectile;

import net.abnormal.anabnormalcircumstance.entity.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
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

    private static final int DESPAWN_TICKS = 20 * 60;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public HatchetProjectileEntity(EntityType<? extends HatchetProjectileEntity> type, World world) {
        super(type, world);
    }

    public HatchetProjectileEntity(World world, LivingEntity owner) {
        this(ModEntities.HATCHET_PROJECTILE, world);
        this.setOwner(owner);
    }

    @Override
    protected ItemStack asItemStack() { return ItemStack.EMPTY; }

    @Override
    protected void onEntityHit(EntityHitResult hit) {
        super.onEntityHit(hit);

        float damage = 30.0f;
        DamageSource src = this.getDamageSources().thrown(this, this.getOwner());
        hit.getEntity().damage(src, damage);

        Vec3d knockback = this.getVelocity().normalize().multiply(1.5);
        hit.getEntity().addVelocity(knockback.x, 0.25, knockback.z);
        hit.getEntity().velocityModified = true;

        this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 0.5f);

        this.discard();
    }

    @Override
    protected void onBlockHit(BlockHitResult hit) {
        super.onBlockHit(hit);
        this.playSound(SoundEvents.ITEM_TRIDENT_HIT_GROUND, 1f, 0.62f);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.age >= DESPAWN_TICKS) {
            this.discard();
            return;
        }

        if (!this.inGround) {
            Vec3d vel = this.getVelocity();
            float horizontalMag = (float)Math.sqrt(vel.x * vel.x + vel.z * vel.z);
            if (horizontalMag > 0.01F) {
                this.setYaw((float)Math.toDegrees(Math.atan2(vel.x, vel.z)));
                this.setPitch((float)Math.toDegrees(Math.atan2(vel.y, horizontalMag)));
            }
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
