package net.abnormal.anabnormalcircumstance.network;

import io.netty.buffer.Unpooled;
import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.component.ManaComponent;
import net.abnormal.anabnormalcircumstance.component.ModComponents;
import net.abnormal.anabnormalcircumstance.component.SpellSlotsComponent;
import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellRegistry;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public final class PacketHandler {

    public static final Identifier CAST_SPELL = new Identifier(AnAbnormalCircumstance.MOD_ID, "cast_spell");
    public static final Identifier SYNC_STATE = new Identifier(AnAbnormalCircumstance.MOD_ID, "sync_spell_state");

    private PacketHandler() {}

   // Registers only the server-side global receivers.
    public static void register() {

        ServerPlayNetworking.registerGlobalReceiver(CAST_SPELL, (server, player, handler, buf, responseSender) -> {
            int tierLevel = buf.readInt();
            server.execute(() -> {

                SpellTier tier = tierFromLevel(tierLevel);
                if (tier == null) return;

                SpellSlotsComponent slots = ModComponents.SLOTS.get(player);
                ManaComponent mana = ModComponents.MANA.get(player);

                Identifier spellId = slots.getSpellForTier(tier);
                if (spellId == null) {
                    player.sendMessage(Text.literal("No Spell Bound to this Tier"), true);
                    return;
                }

                Spell spell = SpellRegistry.get(spellId);
                if (spell == null) {
                    player.sendMessage(Text.literal("Invalid Spell Bound to this Tier"), true);
                    return;
                }

                int cd = slots.getCooldownTicks(tier);
                if (cd > 0) {
                    player.sendMessage(Text.literal("Spell is on Cooldown"), true);
                    return;
                }
                if (mana.getMana() < spell.getManaCost()) {
                    player.sendMessage(Text.literal("Not Enough Mana"), true);
                    return;
                }

                boolean success = spell.cast(player);
                if (success) {
                    mana.setMana(mana.getMana() - spell.getManaCost());
                    slots.setCooldownTicks(tier, spell.getCooldownTicks());

                    syncSpellStateToClient(player, mana, slots);

                    broadcastSpellCastMessage(player, spell);
                }
            });
        });
    }

    /**
     * Server-to-client sync packet.
     */
    public static void syncSpellStateToClient(ServerPlayerEntity player, ManaComponent mana, SpellSlotsComponent slots) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeInt(mana.getMana());
        buf.writeInt(SpellTier.values().length);

        for (SpellTier tier : SpellTier.values()) {
            Identifier id = slots.getSpellForTier(tier);
            if (id == null) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeString(id.toString());
            }
            buf.writeInt(slots.getCooldownTicks(tier));
        }

        ServerPlayNetworking.send(player, SYNC_STATE, buf);
    }

    private static SpellTier tierFromLevel(int level) {
        for (SpellTier s : SpellTier.values())
            if (s.getLevel() == level) return s;
        return null;
    }

    private static Formatting colorFor(SpellElement element) {
        return switch (element) {
            case PYROMANCY -> Formatting.RED;
            case HYDROMANCY -> Formatting.AQUA;
            case GEOMANCY -> Formatting.GREEN;
            case AEROMANCY -> Formatting.GRAY;
        };
    }

    private static void broadcastSpellCastMessage(ServerPlayerEntity caster, Spell spell) {
        Formatting color = colorFor(spell.getElement());
        String casterName = caster.getName().getString();
        Text msg = Text.literal(casterName + " has casted " + spell.getDisplayName()).formatted(color);

        double radiusSq = 15 * 15;
        for (ServerPlayerEntity other : caster.getServer().getPlayerManager().getPlayerList()) {
            if (other.squaredDistanceTo(caster) <= radiusSq) {
                other.sendMessage(msg, false);
            }
        }
    }
}
