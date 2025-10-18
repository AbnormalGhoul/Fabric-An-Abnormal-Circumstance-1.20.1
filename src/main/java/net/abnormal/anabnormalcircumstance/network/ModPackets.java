package net.abnormal.anabnormalcircumstance.network;

import net.abnormal.anabnormalcircumstance.item.custom.UniqueBladeItem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ModPackets {
    public static final Identifier UNIQUE_BLADE_ACTIVATE = new Identifier("anabnormalcircumstance", "unique_blade_activate");

    // Add this identifier
    public static final Identifier PLAY_BLADE_SOUND = new Identifier("anabnormalcircumstance", "play_blade_sound");

    // Utility to send sound packet
    public static void sendBladeSound(ServerPlayerEntity player, String soundId) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(soundId);
        ServerPlayNetworking.send(player, PLAY_BLADE_SOUND, buf);
    }

    // Register client receiver
    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(PLAY_BLADE_SOUND, (client, handler, buf, responseSender) -> {
            String soundId = buf.readString();
            client.execute(() -> {
                var soundEvent = net.minecraft.registry.Registries.SOUND_EVENT.get(new Identifier(soundId));
                if (soundEvent != null && client.player != null) {
                    client.player.playSound(soundEvent, 1.0F, 1.0F);
                }
            });
        });
    }

    public static void registerServer() {
        ServerPlayNetworking.registerGlobalReceiver(UNIQUE_BLADE_ACTIVATE, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                ItemStack stack = player.getMainHandStack();
                if (stack.getItem() instanceof UniqueBladeItem blade) {
                    blade.tryActivateAbility((ServerPlayerEntity) player, stack);
                }
            });
        });
    }
}