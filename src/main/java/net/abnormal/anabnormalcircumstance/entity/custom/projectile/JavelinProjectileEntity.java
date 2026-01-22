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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class JavelinProjectileEntity extends PersistentProjectileEntity implements GeoEntity {

    private static final int DESPAWN_TICKS = 20 * 60;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public JavelinProjectileEntity(EntityType<? extends JavelinProjectileEntity> type, World world) {
        super(type, world);
    }

    public JavelinProjectileEntity(World world, LivingEntity owner) {
        this(ModEntities.JAVELIN_PROJECTILE, world);
        this.setOwner(owner);
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY; // no pickup item
    }

    @Override
    protected void onEntityHit(EntityHitResult hit) {
        super.onEntityHit(hit);

        float damage = 15.0f;
        DamageSource src = this.getDamageSources().thrown(this, this.getOwner());
        hit.getEntity().damage(src, damage);

        Vec3d knockback = this.getVelocity().normalize().multiply(1.75);
        hit.getEntity().addVelocity(knockback.x, 0.25, knockback.z);
        hit.getEntity().velocityModified = true;

        this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1f, 1.2f);

        this.discard();
    }

    @Override
    protected void onBlockHit(BlockHitResult hit) {
        super.onBlockHit(hit);

        this.playSound(SoundEvents.BLOCK_WOOD_HIT, 1f, 1.2f);
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
