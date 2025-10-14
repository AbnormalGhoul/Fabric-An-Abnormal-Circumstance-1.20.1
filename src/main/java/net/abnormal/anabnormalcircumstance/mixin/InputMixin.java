package net.abnormal.anabnormalcircumstance.mixin;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// This mixin targets the Input class (the superclass that holds WASD states)
// and inverts movement keys when the Confusion effect is active.

@Mixin(Input.class)
public abstract class InputMixin {
    // Shadows for input states (declared in Input)
    @Shadow public boolean pressingForward;
    @Shadow public boolean pressingBack;
    @Shadow public boolean pressingLeft;
    @Shadow public boolean pressingRight;

    @Inject(method = "tick", at = @At("HEAD"))
    private void anabnormalcircumstance_invertControls(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        if (player.hasStatusEffect(ModEffects.CONFUSION)) {
            // Swaps key states to invert movement
            System.out.println("[ConfuseEffect] Invert check ticked!");
            boolean f = pressingForward;
            boolean b = pressingBack;
            boolean l = pressingLeft;
            boolean r = pressingRight;

            pressingForward = b;
            pressingBack = f;
            pressingLeft = r;
            pressingRight = l;
        }
    }
}
