package net.abnormal.anabnormalcircumstance.magic.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Lightweight client-side cache of mana + slot state for HUD rendering.
 * Updated by the PacketHandler client sync receiver (client.execute(...)).
 */
public final class ClientComponentAccess {
    private static volatile int clientMana = 100;

    // slot index (1..5) -> SlotData
    private static final Map<Integer, SlotData> slots = new HashMap<>();

    static {
        for (int i = 1; i <= 5; i++) slots.put(i, new SlotData(null, 0));
    }

    // ---- Mana ----
    public static void setClientMana(int mana) { clientMana = mana; }
    public static int getClientMana() { return clientMana; }

    // ---- Slots (thread-safe) ----
    /** Replace all slot data at once (used by full sync packets). */
    public static synchronized void updateAllSlots(Map<Integer, SlotData> newSlots) {
        slots.clear();
        slots.putAll(newSlots);
        for (int i = 1; i <= 5; i++) slots.putIfAbsent(i, new SlotData(null, 0));

        // Update HUD visibility state
        updateHasAnySpellBound();
    }

    /** Update a single slot (delta update). */
    public static synchronized void setSlot(int index, String spellId, int cooldownTicks) {
        slots.put(index, new SlotData(spellId, cooldownTicks));
    }

    /** Returns a defensive copy view for read-only use by HUD code. */
    public static synchronized Map<Integer, SlotData> getAllSlots() {
        return Collections.unmodifiableMap(new HashMap<>(slots));
    }

    public static synchronized SlotData getSlot(int index) {
        return slots.getOrDefault(index, new SlotData(null, 0));
    }

    // ---- Slot data ----
    public static final class SlotData {
        public final String spellId;
        public final int cooldownTicks;

        public SlotData(String spellId, int cooldownTicks) {
            this.spellId = spellId;
            this.cooldownTicks = cooldownTicks;
        }

        public int getCooldownSeconds() {
            return (int) Math.ceil(cooldownTicks / 20.0);
        }

        public boolean isEmpty() {
            return spellId == null || spellId.isEmpty();
        }
    }

    // ---- Utility ----
    private static volatile boolean hasAnySpellBound = false;

    public static boolean hasAnySpellBound() {
        return hasAnySpellBound;
    }

    private static void updateHasAnySpellBound() {
        hasAnySpellBound = slots.values().stream().anyMatch(s -> s.spellId != null && !s.spellId.isEmpty());
    }
}
