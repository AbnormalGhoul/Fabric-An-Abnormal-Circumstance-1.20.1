package net.abnormal.anabnormalcircumstance.magic.client;

import net.abnormal.anabnormalcircumstance.network.PacketHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import io.netty.buffer.Unpooled;

public class ClientTickHandler {

    private static boolean jumpPressedLastTick = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            KeyBinding jumpKey = client.options.jumpKey;
            boolean pressed = jumpKey.isPressed();

            if (pressed && !jumpPressedLastTick && !client.player.isOnGround()) {
                if (client.getNetworkHandler() != null) {
                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(PacketHandler.JUMP, buf));
                }
            }

            jumpPressedLastTick = pressed;
        });
    }
}