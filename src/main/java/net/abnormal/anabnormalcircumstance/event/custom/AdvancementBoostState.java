package net.abnormal.anabnormalcircumstance.event.custom;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.world.PersistentState;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AdvancementBoostState extends PersistentState {
    private final Set<UUID> orcBoosted = new HashSet<>();
    private final Set<UUID> broodBoosted = new HashSet<>();

    public AdvancementBoostState() {}

    // Called by PersistentStateManager when saving
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList orcList = new NbtList();
        for (UUID u : orcBoosted) orcList.add(NbtHelper.fromUuid(u));
        nbt.put("orcBoosts", orcList);

        NbtList broodList = new NbtList();
        for (UUID u : broodBoosted) broodList.add(NbtHelper.fromUuid(u));
        nbt.put("broodBoosts", broodList);

        return nbt;
    }

    // Factory used by PersistentStateManager.getOrCreate
    public static AdvancementBoostState fromNbt(NbtCompound nbt) {
        AdvancementBoostState state = new AdvancementBoostState();

        if (nbt.contains("orcBoosts")) {
            NbtList orcList = nbt.getList("orcBoosts", 10); // compound
            for (int i = 0; i < orcList.size(); i++) {
                state.orcBoosted.add(NbtHelper.toUuid(orcList.getCompound(i)));
            }
        }

        if (nbt.contains("broodBoosts")) {
            NbtList broodList = nbt.getList("broodBoosts", 10);
            for (int i = 0; i < broodList.size(); i++) {
                state.broodBoosted.add(NbtHelper.toUuid(broodList.getCompound(i)));
            }
        }

        return state;
    }

    // Convenience accessors
    public boolean hasOrc(UUID player) {
        return orcBoosted.contains(player);
    }

    public boolean hasBrood(UUID player) {
        return broodBoosted.contains(player);
    }

    public void addOrc(UUID player) {
        if (orcBoosted.add(player)) markDirty();
    }

    public void addBrood(UUID player) {
        if (broodBoosted.add(player)) markDirty();
    }
}
