package net.abnormal.anabnormalcircumstance.magic;

import net.minecraft.util.Identifier;

import java.util.*;

/**
 * Tiny registry for Spell instances. Register all spells at mod init.
 * Uses Identifier -> Spell mapping and provides tier checks.
 */
public final class SpellRegistry {
    private static final Map<Identifier, Spell> BY_ID = new LinkedHashMap<>();
    private static final Map<SpellTier, List<Spell>> BY_TIER = new EnumMap<>(SpellTier.class);

    private SpellRegistry() {}

    public static void register(Spell spell) {
        BY_ID.put(spell.getId(), spell);
        BY_TIER.computeIfAbsent(spell.getTier(), t -> new ArrayList<>()).add(spell);
    }

    public static Spell get(Identifier id) {
        return BY_ID.get(id);
    }

    public static Collection<Spell> all() { return Collections.unmodifiableCollection(BY_ID.values()); }

    public static Optional<Spell> getIfTierMatches(Identifier id, SpellTier tier) {
        Spell s = BY_ID.get(id);
        if (s == null) return Optional.empty();
        return s.getTier() == tier ? Optional.of(s) : Optional.empty();
    }
}
