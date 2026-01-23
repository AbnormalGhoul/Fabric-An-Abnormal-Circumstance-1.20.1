package net.abnormal.anabnormalcircumstance.network;

import io.netty.buffer.Unpooled;
import net.abnormal.anabnormalcircumstance.component.ManaComponent;
import net.abnormal.anabnormalcircumstance.component.ModComponents;
import net.abnormal.anabnormalcircumstance.component.SpellSlotsComponent;
import net.abnormal.anabnormalcircumstance.item.util.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.magic.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public final class PacketHandler {

    public static final Identifier CAST_SPELL =
            new Identifier("anabnormalcircumstance", "cast_spell");
    public static final Identifier SYNC_STATE =
            new Identifier("anabnormalcircumstance", "sync_spell_state");
    public static final Identifier JUMP =
            new Identifier("anabnormalcircumstance", "stormlords_jump");
    public static final Identifier UNIQUE_ITEM_ABILITY =
            new Identifier("anabnormalcircumstance", "unique_item_ability");

    private PacketHandler() {}

    // Registration
    public static void register() {

        ServerPlayNetworking.registerGlobalReceiver(
                JUMP,
                PacketHandler::handleJump
        );

        ServerPlayNetworking.registerGlobalReceiver(
                CAST_SPELL,
                PacketHandler::handleCastSpell
        );

        ServerPlayNetworking.registerGlobalReceiver(
                UNIQUE_ITEM_ABILITY,
                PacketHandler::handleUniqueAbility
        );
    }

    // Packet handlers
    private static void handleJump(
            net.minecraft.server.MinecraftServer server,
            ServerPlayerEntity player,
            net.minecraft.server.network.ServerPlayNetworkHandler handler,
            PacketByteBuf buf,
            net.fabricmc.fabric.api.networking.v1.PacketSender responseSender
    ) {
        server.execute(() -> {
            if (!player.isAlive() || player.isSpectator()) return;
            net.abnormal.anabnormalcircumstance.magic.spells.aeromancy
                    .StormlordsWillSpell.requestAirJump(player);
        });
    }

    private static void handleUniqueAbility(
            net.minecraft.server.MinecraftServer server,
            ServerPlayerEntity player,
            net.minecraft.server.network.ServerPlayNetworkHandler handler,
            PacketByteBuf buf,
            net.fabricmc.fabric.api.networking.v1.PacketSender responseSender
    ) {
        server.execute(() -> {
            if (!player.isAlive() || player.isSpectator()) return;

            if (player.getMainHandStack().getItem() instanceof UniqueAbilityItem unique) {
                unique.useUniqueAbility(player);
            } else if (player.getOffHandStack().getItem() instanceof UniqueAbilityItem unique) {
                unique.useUniqueAbility(player);
            }
        });
    }

    private static void handleCastSpell(
            net.minecraft.server.MinecraftServer server,
            ServerPlayerEntity player,
            net.minecraft.server.network.ServerPlayNetworkHandler handler,
            PacketByteBuf buf,
            net.fabricmc.fabric.api.networking.v1.PacketSender responseSender
    ) {
        // buffer safety
        if (buf.readableBytes() < Integer.BYTES) return;
        int tierLevel = buf.readInt();

        server.execute(() -> {
            if (!player.isAlive() || player.isSpectator()) return;

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

            if (slots.getCooldownTicks(tier) > 0) {
                player.sendMessage(Text.literal("Spell is on Cooldown"), true);
                return;
            }

            if (mana.getMana() < spell.getManaCost()) {
                player.sendMessage(Text.literal("Not Enough Mana"), true);
                return;
            }

            if (!spell.cast(player)) return;

            mana.setMana(mana.getMana() - spell.getManaCost());

            int modifiedCooldown =
                    SpellCooldownUtil.applyCooldownModifiers(
                            player,
                            spell.getCooldownTicks()
                    );

            slots.setCooldownTicks(tier, modifiedCooldown);

            syncSpellStateToClient(player, mana, slots);
            broadcastSpellCastMessage(player, spell);
        });
    }

    // Sync packet
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

    // Helpers
    private static SpellTier tierFromLevel(int level) {
        for (SpellTier tier : SpellTier.values()) {
            if (tier.getLevel() == level) return tier;
        }
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
        Text msg = Text.literal(
                caster.getName().getString() + " has casted " + spell.getDisplayName()
        ).formatted(color);

        double radiusSq = 15 * 15;
        for (ServerPlayerEntity other :
                caster.getServer().getPlayerManager().getPlayerList()) {
            if (other.squaredDistanceTo(caster) <= radiusSq) {
                other.sendMessage(msg, false);
            }
        }
    }
}
