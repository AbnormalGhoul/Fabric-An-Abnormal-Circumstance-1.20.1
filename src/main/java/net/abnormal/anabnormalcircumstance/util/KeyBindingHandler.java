package net.abnormal.anabnormalcircumstance.util;

import io.netty.buffer.Unpooled;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
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

    public static KeyBinding SPELL_TIER_1;
    public static KeyBinding SPELL_TIER_2;
    public static KeyBinding SPELL_TIER_3;
    public static KeyBinding SPELL_TIER_4;
    public static KeyBinding SPELL_TIER_5;

    public static void register() {
        UNIQUE_ITEM_ABILITY = registerKey("unique_item_ability", GLFW.GLFW_KEY_R);

        SPELL_TIER_1 = registerKey("spell_tier_1", GLFW.GLFW_KEY_G);
        SPELL_TIER_2 = registerKey("spell_tier_2", GLFW.GLFW_KEY_H);
        SPELL_TIER_3 = registerKey("spell_tier_3", GLFW.GLFW_KEY_B);
        SPELL_TIER_4 = registerKey("spell_tier_4", GLFW.GLFW_KEY_J);
        SPELL_TIER_5 = registerKey("spell_tier_5", GLFW.GLFW_KEY_N);
    }

    private static KeyBinding registerKey(String name, int key) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + AnAbnormalCircumstance.MOD_ID + "." + name,
                InputUtil.Type.KEYSYM,
                key,
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

        checkKey(SPELL_TIER_1, player, SpellTier.TIER_1);
        checkKey(SPELL_TIER_2, player, SpellTier.TIER_2);
        checkKey(SPELL_TIER_3, player, SpellTier.TIER_3);
        checkKey(SPELL_TIER_4, player, SpellTier.TIER_4);
        checkKey(SPELL_TIER_5, player, SpellTier.TIER_5);

    }


    private static void checkKey(KeyBinding key, PlayerEntity player, SpellTier tier) {
        while (key.wasPressed()) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(tier.getLevel()); // send tier to server
            ClientPlayNetworking.send(new Identifier(AnAbnormalCircumstance.MOD_ID, "cast_spell"), buf);
        }
    }
}