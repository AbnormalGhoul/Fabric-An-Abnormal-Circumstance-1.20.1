package net.abnormal.anabnormalcircumstance.mixin;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.input.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void invertControls(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        if (!player.hasStatusEffect(ModEffects.CONFUSION)) return;

        // player.input is the Input instance used by the client for movement state
        Input input = player.input;
        if (input == null) return;

        InputAccessor acc = (InputAccessor) (Object) input;

        // read current key booleans
        boolean f = acc.getPressingForward();
        boolean b = acc.getPressingBack();
        boolean l = acc.getPressingLeft();
        boolean r = acc.getPressingRight();

        // swap them
        acc.setPressingForward(b);
        acc.setPressingBack(f);
        acc.setPressingLeft(r);
        acc.setPressingRight(l);

        // recompute movement values (same logic KeyboardInput uses)
        acc.setMovementForward(getMovementMultiplier(acc.getPressingForward(), acc.getPressingBack()));
        acc.setMovementSideways(getMovementMultiplier(acc.getPressingLeft(), acc.getPressingRight()));
    }

    // Private copy of logic used in KeyboardInput to compute movement
    @Unique
    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) return 0.0F;
        return positive ? 1.0F : -1.0F;
    }
}
