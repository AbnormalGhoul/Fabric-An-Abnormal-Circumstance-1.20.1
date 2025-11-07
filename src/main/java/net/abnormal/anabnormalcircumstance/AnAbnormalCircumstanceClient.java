package net.abnormal.anabnormalcircumstance;

import net.abnormal.anabnormalcircumstance.block.entity.ModBlockEntities;
import net.abnormal.anabnormalcircumstance.block.entity.renderer.HephaestusAltarBlockEntityRenderer;
import net.abnormal.anabnormalcircumstance.entity.ModEntities;
import net.abnormal.anabnormalcircumstance.entity.client.SilverArrowEntityRenderer;
import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.abnormal.anabnormalcircumstance.magic.ModSpells;
import net.abnormal.anabnormalcircumstance.network.PacketHandler;
import net.abnormal.anabnormalcircumstance.screen.HephaestusAltarScreen;
import net.abnormal.anabnormalcircumstance.screen.ModScreenHandlers;
import net.abnormal.anabnormalcircumstance.util.KeyBindingHandler;
import net.abnormal.anabnormalcircumstance.util.ModModelPredicate;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class AnAbnormalCircumstanceClient implements ClientModInitializer {
    @Override
    @SuppressWarnings("deprecation")
    public void onInitializeClient() {

        ModSpells.registerAll();
        ModModelPredicate.registerSpellScrollPredicate();
        ModModelPredicate.registerBow(ModItems.FIRST_LEAF);
        HandledScreens.register(ModScreenHandlers.HEPHAESTUS_ALTAR_SCREEN_HANDLER, HephaestusAltarScreen::new);
        EntityRendererRegistry.register(ModEntities.SILVER_ARROW, SilverArrowEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.HEPHAESTUS_ALTAR_BLOCK_ENTITY_BLOCK, HephaestusAltarBlockEntityRenderer::new);

        KeyBindingHandler.register();
        ClientTickEvents.END_CLIENT_TICK.register(KeyBindingHandler::onClientTick);
        HudRenderCallback.EVENT.register(new net.abnormal.anabnormalcircumstance.magic.client.SpellHudRenderer());
        PacketHandler.register();

    }

    private void onClientTick(MinecraftClient client) {
        KeyBindingHandler.onClientTick(client);
    }
}