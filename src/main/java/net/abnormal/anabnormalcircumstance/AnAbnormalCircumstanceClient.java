package net.abnormal.anabnormalcircumstance;

import net.abnormal.anabnormalcircumstance.entity.ModEntities;
import net.abnormal.anabnormalcircumstance.entity.client.SilverArrowEntityRenderer;
import net.abnormal.anabnormalcircumstance.screen.HephaestusAltarScreen;
import net.abnormal.anabnormalcircumstance.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.ArrowEntityRenderer;

public class AnAbnormalCircumstanceClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.HEPHAESTUS_ALTAR_SCREEN_HANDLER, HephaestusAltarScreen::new);
        EntityRendererRegistry.register(ModEntities.SILVER_ARROW, SilverArrowEntityRenderer::new);
    }
}