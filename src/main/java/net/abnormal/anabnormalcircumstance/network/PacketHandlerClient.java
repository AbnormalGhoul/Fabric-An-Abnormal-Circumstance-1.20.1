package net.abnormal.anabnormalcircumstance.network;

import net.abnormal.anabnormalcircumstance.magic.client.ClientComponentAccess;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public final class PacketHandlerClient {

    private PacketHandlerClient() {}

    // Registers only the client-side receivers
    public static void register() {

        ClientPlayNetworking.registerGlobalReceiver(PacketHandler.SYNC_STATE, (client, handler, buf, responseSender) -> {

            int mana = buf.readInt();
            int slotCount = buf.readInt();

            Map<Integer, ClientComponentAccess.SlotData> slots = new HashMap<>();

            for (int i = 0; i < slotCount; i++) {
                boolean hasSpell = buf.readBoolean();
                String spellId = hasSpell ? buf.readString(32767) : null;
                int cooldown = buf.readInt();
                slots.put(i + 1, new ClientComponentAccess.SlotData(spellId, cooldown));
            }

            client.execute(() -> {
                ClientComponentAccess.setClientMana(mana);
                ClientComponentAccess.updateAllSlots(slots);
            });
        });
    }
}
