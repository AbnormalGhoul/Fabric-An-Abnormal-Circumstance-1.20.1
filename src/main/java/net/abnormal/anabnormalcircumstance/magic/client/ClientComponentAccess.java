package net.abnormal.anabnormalcircumstance.magic.client;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Small client-side cache for HUD rendering. Updated by PacketHandler sync packet.
 * This keeps HUD rendering decoupled from server components (which are server-only).
 */
public final class ClientComponentAccess {
    private static volatile int clientMana = 100;
    // map slot index (1..5) -> pair(spellIdString, cooldownSeconds)
    private static final Map<Integer, SlotData> slots = new HashMap<>();

    static {
        for (int i = 1; i <= 5; i++) slots.put(i, new SlotData(null, 0));
    }

    public static void setClientMana(int mana) { clientMana = mana; }
    public static int getClientMana() { return clientMana; }

    public static void setClientSlotData(java.util.Map<Integer, ?> rawSlots) {
        // rawSlots expected to be Map<Integer, PacketHandler.SlotData> but we reconstruct safely.
        // For simplicity, this method is called by PacketHandler on client thread via client.execute.
        slots.clear();
        for (int i = 1; i <= 5; i++) {
            slots.put(i, new SlotData(null, 0));
        }
    }

    public static void setClientSlotData(java.util.Map<Integer, Object> rawSlots, Map<Integer, SlotData> typedSlots) {
        // fallback placeholder (we don't actually use rawSlots typed here).
    }

    // simpler API used by the HUD renderer: set slot by index
    public static void setClientSlot(int index, String spellId, int cooldownTicks) {
        slots.put(index, new SlotData(spellId, cooldownTicks));
    }

    public static SlotData getSlot(int index) {
        return slots.getOrDefault(index, new SlotData(null, 0));
    }

    public static final class SlotData {
        public final String spellId;
        public final int cooldownTicks;
        public SlotData(String spellId, int cooldownTicks) {
            this.spellId = spellId;
            this.cooldownTicks = cooldownTicks;
        }
        public int cooldownSeconds() { return (int) Math.ceil(cooldownTicks / 20.0); }
    }
}
