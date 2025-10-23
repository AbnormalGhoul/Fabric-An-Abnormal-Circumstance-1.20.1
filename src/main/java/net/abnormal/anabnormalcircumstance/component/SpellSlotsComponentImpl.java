package net.abnormal.anabnormalcircumstance.component;

import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.Map;

/**
 * Stores one spell Identifier and cooldown per tier.
 */
public class SpellSlotsComponentImpl implements SpellSlotsComponent {
    private final Map<SpellTier, Identifier> equipped = new EnumMap<>(SpellTier.class);
    private final Map<SpellTier, Integer> cooldowns = new EnumMap<>(SpellTier.class);

    public SpellSlotsComponentImpl() {
        for (SpellTier t : SpellTier.values()) {
            equipped.put(t, null);
            cooldowns.put(t, 0);
        }
    }

    @Override
    public Identifier getSpellForTier(SpellTier tier) {
        return equipped.get(tier);
    }

    @Override
    public void setSpellForTier(SpellTier tier, Identifier id) {
        equipped.put(tier, id);
    }

    @Override
    public void clearAllSpells() {
        for (SpellTier t : SpellTier.values()) equipped.put(t, null);
    }

    @Override
    public int getCooldownTicks(SpellTier tier) {
        return cooldowns.getOrDefault(tier, 0);
    }

    @Override
    public void setCooldownTicks(SpellTier tier, int ticks) {
        cooldowns.put(tier, Math.max(0, ticks));
    }

    @Override
    public void tick(ServerPlayerEntity player) {
        for (SpellTier t : SpellTier.values()) {
            int cd = getCooldownTicks(t);
            if (cd > 0) {
                cooldowns.put(t, cd - 1);
            }
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        for (SpellTier t : SpellTier.values()) {
            String keySpell = "spell_t" + t.getLevel();
            if (tag.contains(keySpell)) {
                equipped.put(t, Identifier.tryParse(tag.getString(keySpell)));
            } else {
                equipped.put(t, null);
            }
            String keyCd = "cd_t" + t.getLevel();
            cooldowns.put(t, tag.getInt(keyCd));
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        for (SpellTier t : SpellTier.values()) {
            Identifier id = equipped.get(t);
            if (id != null) tag.putString("spell_t" + t.getLevel(), id.toString());
            tag.putInt("cd_t" + t.getLevel(), cooldowns.getOrDefault(t, 0));
        }
    }
}
