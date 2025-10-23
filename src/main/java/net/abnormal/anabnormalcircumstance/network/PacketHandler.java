package net.abnormal.anabnormalcircumstance.network;

import io.netty.buffer.Unpooled;
import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.component.ManaComponent;
import net.abnormal.anabnormalcircumstance.component.ModComponents;
import net.abnormal.anabnormalcircumstance.component.SpellSlotsComponent;
import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellRegistry;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

/**
 * Small networking helper:
 * - registers server-side receiver for "cast_spell"
 * - has a compact server->client sync packet "sync_spell_state"
 *
 * Packet formats:
 * - cast_spell (client -> server): int tierLevel
 * - sync_spell_state (server -> client): int mana, for each slot: boolean hasSpell; if has: string spellId; int cooldownTicks
 */
public final class PacketHandler {
    public static final Identifier CAST_SPELL = new Identifier(AnAbnormalCircumstance.MOD_ID, "cast_spell");
    public static final Identifier SYNC_STATE = new Identifier(AnAbnormalCircumstance.MOD_ID, "sync_spell_state");

    private PacketHandler() {}

    public static void register() {
        // server receives cast requests from client
        ServerPlayNetworking.registerGlobalReceiver(CAST_SPELL, (server, player, handler, buf, responseSender) -> {
            int tierLevel = buf.readInt();
            server.execute(() -> {
                SpellTier tier = tierFromLevel(tierLevel);
                if (tier == null) return;
                SpellSlotsComponent slots = ModComponents.SLOTS.get(player);
                ManaComponent mana = ModComponents.MANA.get(player);
                if (slots == null || mana == null) return;
                Identifier spellId = slots.getSpellForTier(tier);
                if (spellId == null) return; // no spell in that slot
                Spell spell = SpellRegistry.get(spellId);
                if (spell == null) return;
                int cd = slots.getCooldownTicks(tier);
                if (cd > 0) return; // still on cooldown
                if (mana.getMana() < spell.getManaCost()) {
                    // insufficient mana - optionally send feedback (chat/sound)
                    return;
                }
                // perform cast server-side
                boolean success = spell.cast((ServerPlayerEntity) player);
                if (success) {
                    mana.setMana(mana.getMana() - spell.getManaCost());
                    slots.setCooldownTicks(tier, spell.getCooldownTicks());
                    // sync HUD/state to client
                    syncSpellStateToClient((ServerPlayerEntity) player, mana, slots);
                }
            });
        });

        // client receives server sync state
        ClientPlayNetworking.registerGlobalReceiver(SYNC_STATE, (client, handler, buf, responseSender) -> {
            // client thread
            int mana = buf.readInt();
            // slots count assumed to be 5
            int slotCount = buf.readInt();
            java.util.Map<Integer, SlotData> slots = new java.util.HashMap<>();
            for (int i = 0; i < slotCount; i++) {
                boolean has = buf.readBoolean();
                String spellId = has ? buf.readString(32767) : null;
                int cd = buf.readInt();
                slots.put(i + 1, new SlotData(spellId, cd));
            }
            client.execute(() -> {
                // update client-side cache
                net.abnormal.anabnormalcircumstance.magic.client.ClientComponentAccess.setClientMana(mana);
                net.abnormal.anabnormalcircumstance.magic.client.ClientComponentAccess.setClientSlotData(slots);
            });
        });
    }

    /**
     * Server->client: send current mana + equipped spells + cooldowns
     */
    public static void syncSpellStateToClient(ServerPlayerEntity player, ManaComponent mana, SpellSlotsComponent slots) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(mana == null ? 0 : mana.getMana());
        // write 5 slots
        buf.writeInt(SpellTier.values().length);
        for (SpellTier t : SpellTier.values()) {
            var id = (slots == null) ? null : slots.getSpellForTier(t);
            if (id == null) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeString(id.toString());
            }
            int cd = slots == null ? 0 : slots.getCooldownTicks(t);
            buf.writeInt(cd);
        }
        ServerPlayNetworking.send(player, SYNC_STATE, buf);
    }

    private static SpellTier tierFromLevel(int level) {
        for (SpellTier s : SpellTier.values()) if (s.getLevel() == level) return s;
        return null;
    }

    // helper to parse slot info on client
    private static class SlotData {
        final String spellId;
        final int cooldown;
        SlotData(String spellId, int cooldown) { this.spellId = spellId; this.cooldown = cooldown; }
    }
}
