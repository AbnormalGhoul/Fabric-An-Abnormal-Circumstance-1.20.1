package net.abnormal.anabnormalcircumstance.network;

import io.netty.buffer.Unpooled;
import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.component.ManaComponent;
import net.abnormal.anabnormalcircumstance.component.ModComponents;
import net.abnormal.anabnormalcircumstance.component.SpellSlotsComponent;
import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellRegistry;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.abnormal.anabnormalcircumstance.magic.client.ClientComponentAccess;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Lightweight networking helper for the magic system.
 *
 * - cast_spell: client -> server (int tierLevel)
 * - sync_spell_state: server -> client (int mana, int slotCount, for each slot: boolean hasSpell, string spellId, int cooldownTicks)
 */
public final class PacketHandler {
    public static final Identifier CAST_SPELL = new Identifier(AnAbnormalCircumstance.MOD_ID, "cast_spell");
    public static final Identifier SYNC_STATE = new Identifier(AnAbnormalCircumstance.MOD_ID, "sync_spell_state");

    private PacketHandler() {}

    public static void register() {
        // server receiver for casting spells
        ServerPlayNetworking.registerGlobalReceiver(CAST_SPELL, (server, player, handler, buf, responseSender) -> {
            int tierLevel = buf.readInt();
            server.execute(() -> {

                SpellTier tier = tierFromLevel(tierLevel);
                if (tier == null) return;

                SpellSlotsComponent slots = ModComponents.SLOTS.get(player);
                ManaComponent mana = ModComponents.MANA.get(player);

                Identifier spellId = slots.getSpellForTier(tier);
                if (spellId == null) {
                    player.sendMessage(net.minecraft.text.Text.literal("No Spell Bound to this Tier"), true);
                    return;
                }

                Spell spell = SpellRegistry.get(spellId);
                if (spell == null) {
                    player.sendMessage(net.minecraft.text.Text.literal("Invalid Spell Bound to this Tier"), true);
                    return;
                }

                int cd = slots.getCooldownTicks(tier);
                if (cd > 0) {
                    player.sendMessage(net.minecraft.text.Text.literal("Spell is on Cooldown"), true);
                    return; // still on cooldown
                }
                if (mana.getMana() < spell.getManaCost()) {
                    player.sendMessage(net.minecraft.text.Text.literal("Not Enough Mana"), true);
                    return;
                }
                // cast server-side (implement spell casting in Spell.cast(...))
                boolean success = spell.cast((ServerPlayerEntity) player);
                if (success) {
                    mana.setMana(mana.getMana() - spell.getManaCost());
                    slots.setCooldownTicks(tier, spell.getCooldownTicks());
                    // Immediately sync server state to client
                    syncSpellStateToClient((ServerPlayerEntity) player, mana, slots);
                }
            });
        });

        // client receiver for the state sync packet
        ClientPlayNetworking.registerGlobalReceiver(SYNC_STATE, (client, handler, buf, responseSender) -> {
            // Read on network thread, then schedule client thread update
            int mana = buf.readInt();
            int slotCount = buf.readInt();
            Map<Integer, ClientComponentAccess.SlotData> newSlots = new HashMap<>();
            for (int i = 0; i < slotCount; i++) {
                boolean has = buf.readBoolean();
                String spellId = has ? buf.readString(32767) : null;
                int cd = buf.readInt();
                newSlots.put(i + 1, new ClientComponentAccess.SlotData(spellId, cd));
            }
            client.execute(() -> {
                ClientComponentAccess.setClientMana(mana);
                ClientComponentAccess.updateAllSlots(newSlots);
            });
        });
    }

    /**
     * Server -> client state sync.
     */
    public static void syncSpellStateToClient(ServerPlayerEntity player, ManaComponent mana, SpellSlotsComponent slots) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(mana == null ? 0 : mana.getMana());
        buf.writeInt(SpellTier.values().length);
        for (SpellTier t : SpellTier.values()) {
            Identifier id = (slots == null) ? null : slots.getSpellForTier(t);
            if (id == null) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeString(id.toString());
            }
            int cd = (slots == null) ? 0 : slots.getCooldownTicks(t);
            buf.writeInt(cd);
        }
        ServerPlayNetworking.send(player, SYNC_STATE, buf);
    }

    private static SpellTier tierFromLevel(int level) {
        for (SpellTier s : SpellTier.values()) if (s.getLevel() == level) return s;
        return null;
    }
}
