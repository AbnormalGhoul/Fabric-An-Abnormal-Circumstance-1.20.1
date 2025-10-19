package net.abnormal.anabnormalcircumstance.util;

import io.netty.buffer.Unpooled;
import net.minecraft.client.util.InputUtil;
import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import net.abnormal.anabnormalcircumstance.item.interfaces.UniqueAbilityItem;

public class KeyBindingHandler {
    public static KeyBinding UNIQUE_ITEM_ABILITY;

    public static void register() {
        UNIQUE_ITEM_ABILITY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + AnAbnormalCircumstance.MOD_ID + ".unique_item_ability",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category." + AnAbnormalCircumstance.MOD_ID + ".keys"
        ));
    }

    public static void onClientTick(MinecraftClient client) {
        if (client.player == null) return;
        PlayerEntity player = client.player;

        // Check if key was pressed this tick
        while (UNIQUE_ITEM_ABILITY.wasPressed()) {
            // Check if the player is holding an item that implements UniqueAbilityItem
            if (player.getMainHandStack().getItem() instanceof UniqueAbilityItem
                    || player.getOffHandStack().getItem() instanceof UniqueAbilityItem) {

                // Send a packet to the server to trigger the item's ability
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                ClientPlayNetworking.send(
                        new Identifier(AnAbnormalCircumstance.MOD_ID, "unique_item_ability"),
                        buf
                );
            }
        }
    }
}