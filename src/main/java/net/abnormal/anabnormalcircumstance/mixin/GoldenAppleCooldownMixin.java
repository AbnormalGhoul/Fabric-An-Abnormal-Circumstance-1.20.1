package net.abnormal.anabnormalcircumstance.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class GoldenAppleCooldownMixin {

    // 0.5 seconds = 10 ticks
    @Unique
    private static final int COOLDOWN_TICKS = 10;

    @Inject(
            method = "eatFood",
            at = @At("TAIL")
    )
    private void applyGoldenAppleCooldown(
            World world,
            ItemStack stack,
            CallbackInfoReturnable<ItemStack> cir
    ) {
        PlayerEntity player = (PlayerEntity)(Object)this;

        if (stack.isOf(Items.GOLDEN_APPLE)) {
            player.getItemCooldownManager().set(Items.GOLDEN_APPLE, COOLDOWN_TICKS);
        }
    }
}
