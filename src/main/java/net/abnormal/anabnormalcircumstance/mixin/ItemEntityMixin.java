package net.abnormal.anabnormalcircumstance.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void anabnormalcircumstance$blockContestedPickup(PlayerEntity player, CallbackInfo ci) {
        ItemEntity item = (ItemEntity) (Object) this;
        World world = player.getWorld();

        // Only run on the server
        if (world.isClient) return;

        // Get all players near this item (within pickup range + contest range)
        double radius = 1.5; // tweakable "contest range"
        List<PlayerEntity> nearbyPlayers = world.getEntitiesByClass(
                PlayerEntity.class,
                item.getBoundingBox().expand(radius),
                p -> !p.isSpectator() && p.isAlive()
        );

        // If more than one player is contesting the item, block pickup
        if (nearbyPlayers.size() > 1) {
            ci.cancel(); // cancel the pickup attempt entirely
        }
    }
}
