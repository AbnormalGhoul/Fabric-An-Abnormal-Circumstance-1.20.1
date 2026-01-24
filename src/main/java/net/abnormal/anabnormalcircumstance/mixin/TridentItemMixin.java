package net.abnormal.anabnormalcircumstance.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin {

    @Unique
    private static final int COOLDOWN_TICKS = 150;

    @Inject(
            method = "onStoppedUsing(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)V",
            at = @At("TAIL")
    )
    private void afterThrow(
            ItemStack stack,
            World world,
            LivingEntity user,
            int remainingUseTicks,
            CallbackInfo ci
    ) {
        if (world.isClient) return;
        if (!(user instanceof PlayerEntity player)) return;

        // Safety check: ensure this was actually a throw, not a cancel
        int usedTicks = stack.getMaxUseTime() - remainingUseTicks;
        if (usedTicks < 10) return; // vanilla throw threshold

        player.getItemCooldownManager().set(Items.TRIDENT, COOLDOWN_TICKS);
    }
}
