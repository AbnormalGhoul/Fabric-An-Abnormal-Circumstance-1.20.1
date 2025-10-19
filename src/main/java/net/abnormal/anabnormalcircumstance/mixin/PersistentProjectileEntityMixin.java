package net.abnormal.anabnormalcircumstance.mixin;

import net.abnormal.anabnormalcircumstance.item.custom.unique.FirstLeafBowItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEntityMixin {
    @Unique
    private static final String FIRST_LEAF_FLAG_NAME = "FirstLeafPrimedArrow";

    @Inject(method = "onEntityHit", at = @At("HEAD"))
    private void anabnormalcircumstance_onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        PersistentProjectileEntity arrow = (PersistentProjectileEntity) (Object) this;
        if (!arrow.hasCustomName()) return;
        if (!FIRST_LEAF_FLAG_NAME.equals(Objects.requireNonNull(arrow.getCustomName()).getString())) return;

        if (entityHitResult.getEntity() instanceof LivingEntity living) {
            FirstLeafBowItem.applyStun(living);
        }
    }
}
