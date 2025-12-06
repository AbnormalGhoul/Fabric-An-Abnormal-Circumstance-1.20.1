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
import software.bernie.geckolib.util.GeckoLibUtil;

public class JavelinProjectileEntity extends PersistentProjectileEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public JavelinProjectileEntity(EntityType<? extends JavelinProjectileEntity> type, World world) {
        super(type, world);
    }

    public JavelinProjectileEntity(World world, LivingEntity owner) {
        this(ModEntities.JAVELIN_PROJECTILE, world);
        this.setOwner(owner);
    }

    private boolean inGround = false;
    private int despawnCounter = 6000; // 5 minutes (20 ticks * 60 * 5)

    public int getDespawnCounter() {
        return despawnCounter;
    }

    public void setDespawnCounter(int ticks) {
        this.despawnCounter = ticks;
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY; // no pickup item
    }

    @Override
    protected void onEntityHit(EntityHitResult hit) {
        super.onEntityHit(hit);

        // Damage the entity
        float damage = 15.0f;
        DamageSource src = this.getDamageSources().thrown(this, this.getOwner());
        hit.getEntity().damage(src, damage);

        // Knockback
        double kb_strength = 1.75;
        Vec3d direction = this.getVelocity().normalize().multiply(kb_strength);
        hit.getEntity().addVelocity(direction.x, 0.25, direction.z);
        hit.getEntity().velocityModified = true;

        this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1f, 1.2f);

        // Mark as stuck in the entity
        this.setNoGravity(true);
        this.setVelocity(Vec3d.ZERO);
        this.inGround = true;

        // schedule despawn in 5 minutes (6000 ticks)
        this.age = 0;
        this.setDespawnCounter(6000);
    }

    @Override
    protected void onBlockHit(net.minecraft.util.hit.BlockHitResult blockHitResult) {
        // Let PersistentProjectileEntity handle inGround, sound, block position, etc.
        super.onBlockHit(blockHitResult);

        // Align the javelin’s rotation to match its velocity at impact
        Vec3d vel = this.getVelocity();
        float horizontalMag = (float)Math.sqrt(vel.x * vel.x + vel.z * vel.z);
        this.setYaw((float)(Math.toDegrees(Math.atan2(vel.x, vel.z))));
        this.setPitch((float)(Math.toDegrees(Math.atan2(vel.y, horizontalMag))));

        // Ensure it doesn't hover above block
        this.setPosition(blockHitResult.getPos());

        // Sink into the block slightly
        Vec3d hitPos = blockHitResult.getPos();
        Vec3d offset = vel.normalize().multiply(0.05); // 0.05 blocks “inside”
        this.setPosition(hitPos.add(offset));


        // Stop movement and gravity
        this.setVelocity(Vec3d.ZERO);
        this.setNoGravity(true);
        this.inGround = true;

        this.playSound(SoundEvents.BLOCK_WOOD_HIT, 1f, 1.2f);

        this.age = 0;
        this.setDespawnCounter(6000);
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

        // Only update rotation while flying
        Vec3d vel = this.getVelocity();
        float horizontalMag = (float)Math.sqrt(vel.x * vel.x + vel.z * vel.z);
        if (horizontalMag > 0.01F) {
            this.setYaw((float)(Math.toDegrees(Math.atan2(vel.x, vel.z))));
            this.setPitch((float)(Math.toDegrees(Math.atan2(vel.y, horizontalMag))));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
