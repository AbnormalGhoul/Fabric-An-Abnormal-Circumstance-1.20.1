// StormlordsWillPacket.java
package net.abnormal.anabnormalcircumstance.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class StormlordsWillPacket {
    public static final Identifier JUMP = new Identifier("anabnormalcircumstance", "stormlords_jump");

    /** Registers the server-side packet handler */
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(JUMP, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                // Tell StormlordsWillSpell to process an air jump for this player
                net.abnormal.anabnormalcircumstance.magic.spells.aeromancy.StormlordsWillSpell.requestAirJump(player);
            });
        });
    }
}
