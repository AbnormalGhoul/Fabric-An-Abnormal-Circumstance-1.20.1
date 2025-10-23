package net.abnormal.anabnormalcircumstance.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Interface for player's mana component.
 * Must extend CCA's Component interface for registration.
 */
public interface ManaComponent extends Component {
    int MAX_MANA = 100;

    int getMana();
    double getManaExact();
    void setMana(int value);
    void addMana(int delta);
    void tick(ServerPlayerEntity player);

    // CCA serialization (note: both are void)
    void readFromNbt(NbtCompound tag);
    void writeToNbt(NbtCompound tag);
}
