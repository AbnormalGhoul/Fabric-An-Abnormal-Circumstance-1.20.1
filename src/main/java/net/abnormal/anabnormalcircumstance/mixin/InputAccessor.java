package net.abnormal.anabnormalcircumstance.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.input.Input;

@Mixin(Input.class)
public interface InputAccessor {
    @Accessor("pressingForward") boolean getPressingForward();
    @Accessor("pressingForward") void setPressingForward(boolean value);

    @Accessor("pressingBack") boolean getPressingBack();
    @Accessor("pressingBack") void setPressingBack(boolean value);

    @Accessor("pressingLeft") boolean getPressingLeft();
    @Accessor("pressingLeft") void setPressingLeft(boolean value);

    @Accessor("pressingRight") boolean getPressingRight();
    @Accessor("pressingRight") void setPressingRight(boolean value);

    @Accessor("movementForward") float getMovementForward();
    @Accessor("movementForward") void setMovementForward(float value);

    @Accessor("movementSideways") float getMovementSideways();
    @Accessor("movementSideways") void setMovementSideways(float value);
}
