package net.abnormal.anabnormalcircumstance.component;

import net.abnormal.anabnormalcircumstance.enchantment.util.ManaEnchantments;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Server-authoritative Mana implementation.
 */
public class ManaComponentImpl implements ManaComponent {
    private double mana = MAX_MANA;
    private double fractional = 0.0;
    private static final double MANA_PER_SECOND = 1.0;

    @Override
    public int getMana() {
        return Math.max(0, Math.min(MAX_MANA, (int) Math.floor(mana)));
    }

    @Override
    public double getManaExact() {
        return mana;
    }

    @Override
    public void setMana(int value) {
        this.mana = Math.max(0, Math.min(MAX_MANA, value));
    }

    @Override
    public void addMana(int delta) {
        setMana(getMana() + delta);
    }

    @Override
    public void tick(ServerPlayerEntity player) {
        double multiplier = ManaEnchantments.getManaRegenMultiplier(player);

        fractional += (MANA_PER_SECOND * multiplier) / 20.0;

        if (fractional >= 1.0) {
            int gain = (int) Math.floor(fractional);
            fractional -= gain;
            setMana(getMana() + gain);
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.mana = tag.contains("mana") ? tag.getDouble("mana") : MAX_MANA;
        this.fractional = tag.contains("mana_frac") ? tag.getDouble("mana_frac") : 0.0;
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putDouble("mana", this.mana);
        tag.putDouble("mana_frac", this.fractional);
    }
}
