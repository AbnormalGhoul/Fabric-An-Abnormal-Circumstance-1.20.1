package net.abnormal.anabnormalcircumstance.magic.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import io.netty.buffer.Unpooled;
import net.abnormal.anabnormalcircumstance.network.StormlordsWillPacket;

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
                    client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(StormlordsWillPacket.JUMP, buf));
                }
            }

            jumpPressedLastTick = pressed;
        });
    }
}