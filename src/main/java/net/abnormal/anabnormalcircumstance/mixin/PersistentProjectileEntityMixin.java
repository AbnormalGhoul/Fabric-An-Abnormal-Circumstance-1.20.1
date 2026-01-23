package net.abnormal.anabnormalcircumstance.mixin;

import net.abnormal.anabnormalcircumstance.item.unique.FirstLeafBowItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin {
    @Unique
    private static final String FIRST_LEAF_FLAG_NAME = "FirstLeafPrimedArrow";

    // Called when hitting an entity
    @Inject(method = "onEntityHit", at = @At("HEAD"))
    private void anabnormalcircumstance_onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        PersistentProjectileEntity arrow = (PersistentProjectileEntity) (Object) this;
        if (!arrow.hasCustomName()) return;
        if (!FIRST_LEAF_FLAG_NAME.equals(Objects.requireNonNull(arrow.getCustomName()).getString())) return;

        if (arrow.getWorld() instanceof ServerWorld serverWorld) {
            Vec3d hitPos = entityHitResult.getPos();
            FirstLeafBowItem.triggerMist(serverWorld, hitPos, (PlayerEntity) arrow.getOwner());
        }
    }

    // Also trigger if arrow hits a block (not just entities)
    @Inject(method = "onBlockHit", at = @At("HEAD"))
    private void anabnormalcircumstance_onBlockHit(BlockHitResult blockHitResult, CallbackInfo ci) {
        PersistentProjectileEntity arrow = (PersistentProjectileEntity) (Object) this;
        if (!arrow.hasCustomName()) return;
        if (!FIRST_LEAF_FLAG_NAME.equals(Objects.requireNonNull(arrow.getCustomName()).getString())) return;

        if (arrow.getWorld() instanceof ServerWorld serverWorld) {
            Vec3d hitPos = Vec3d.ofCenter(blockHitResult.getBlockPos());
            FirstLeafBowItem.triggerMist(serverWorld, hitPos, (PlayerEntity) arrow.getOwner());
        }
    }
}
