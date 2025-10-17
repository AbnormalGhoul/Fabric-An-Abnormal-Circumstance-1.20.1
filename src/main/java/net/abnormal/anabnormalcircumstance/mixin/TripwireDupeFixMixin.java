package net.abnormal.anabnormalcircumstance.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TripwireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Prevent tripwire-based string duplication
@Mixin(TripwireBlock.class)
public class TripwireDupeFixMixin {
    @Inject(method = "onBreak", at = @At("HEAD"), cancellable = true)
    private void disableTripwireDuping(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        // If the tripwire is attached, don't drop string when broken manually
        if (state.get(TripwireBlock.ATTACHED)) {
            ci.cancel(); // prevents vanilla drop logic
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
    }
}
