package net.abnormal.anabnormalcircumstance;

import net.abnormal.anabnormalcircumstance.client.Keybinds;
import net.abnormal.anabnormalcircumstance.entity.ModEntities;
import net.abnormal.anabnormalcircumstance.entity.client.SilverArrowEntityRenderer;
import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.abnormal.anabnormalcircumstance.item.custom.UniqueBladeItem;
import net.abnormal.anabnormalcircumstance.network.ModPackets;
import net.abnormal.anabnormalcircumstance.screen.HephaestusAltarScreen;
import net.abnormal.anabnormalcircumstance.screen.ModScreenHandlers;
import net.abnormal.anabnormalcircumstance.util.UniqueBladeCooldownManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class AnAbnormalCircumstanceClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.HEPHAESTUS_ALTAR_SCREEN_HANDLER, HephaestusAltarScreen::new);
        EntityRendererRegistry.register(ModEntities.SILVER_ARROW, SilverArrowEntityRenderer::new);

        Keybinds.UNIQUE_BLADE_ABILITY.getDefaultKey();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                ItemStack stack = client.player.getMainHandStack();
                if (stack.getItem() instanceof UniqueBladeItem blade) {
                    long remaining = UniqueBladeCooldownManager.getRemaining(client.player);
                    if (remaining > 0) {
                        int seconds = (int) Math.ceil(remaining / 1000.0);
                        client.player.sendMessage(Text.literal("Ability Cooldown: " + seconds + "s"), true);
                    }
                }
            }
            if (Keybinds.UNIQUE_BLADE_ABILITY.wasPressed()) {
                if (client.player != null) {
                    // Send a packet to the server to request activation
                    ClientPlayNetworking.send(ModPackets.UNIQUE_BLADE_ACTIVATE, PacketByteBufs.create());
                }
            }
        });
    }
}