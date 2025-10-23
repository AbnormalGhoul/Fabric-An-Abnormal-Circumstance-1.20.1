package net.abnormal.anabnormalcircumstance.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Stores equipped spell id and cooldown per tier.
 */
public interface SpellSlotsComponent extends Component {
    Identifier getSpellForTier(SpellTier tier);
    void setSpellForTier(SpellTier tier, Identifier id);
    void clearAllSpells();

    int getCooldownTicks(SpellTier tier);
    void setCooldownTicks(SpellTier tier, int ticks);

    void tick(ServerPlayerEntity player);

    // CCA serialization (void, not NbtCompound return)
    void readFromNbt(NbtCompound tag);
    void writeToNbt(NbtCompound tag);
}
