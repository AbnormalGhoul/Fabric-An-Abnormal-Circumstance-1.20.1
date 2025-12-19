package net.abnormal.anabnormalcircumstance.entity.custom.projectile;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BroodWebProjectileEntity extends PersistentProjectileEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BroodWebProjectileEntity(EntityType<? extends PersistentProjectileEntity> type, World world) {
        super(type, world);
        this.setNoGravity(false);
        this.setDamage(0);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    public BroodWebProjectileEntity(EntityType<? extends PersistentProjectileEntity> type, World world, LivingEntity owner) {
        super(type, owner, world);
        this.setNoGravity(false);
        this.setDamage(0);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (!(this.getWorld() instanceof ServerWorld)) return;

        if (entityHitResult.getEntity() instanceof LivingEntity target) {
            target.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    ModEffects.STUN, 40, 0, false, true, true
            ));
            target.damage(this.getDamageSources().thrown(this, this.getOwner()), 0.0F);
        }

        // custom sound
        this.playSound(SoundEvents.ENTITY_TURTLE_EGG_BREAK, 1.0f, 1.0f);

        this.discard();
    }

    @Override
    protected void onBlockHit(BlockHitResult hitResult) {
        this.playSound(SoundEvents.ENTITY_TURTLE_EGG_BREAK, 1.0f, 1.0f);
        this.discard();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
            for (int i = 0; i < 2; i++) {
                // Use a visible but thematic particle
                this.getWorld().addParticle(ParticleTypes.FALLING_SPORE_BLOSSOM,
                        this.getX(), this.getY(), this.getZ(),
                        0, 0, 0);
            }
        }
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }

    // GeckoLib integration
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Add spin animation later if you wish
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
