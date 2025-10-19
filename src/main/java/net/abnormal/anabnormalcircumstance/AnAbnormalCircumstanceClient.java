package net.abnormal.anabnormalcircumstance;

import net.abnormal.anabnormalcircumstance.block.entity.ModBlockEntities;
import net.abnormal.anabnormalcircumstance.block.entity.renderer.HephaestusAltarBlockEntityRenderer;
import net.abnormal.anabnormalcircumstance.client.Keybinds;
import net.abnormal.anabnormalcircumstance.entity.ModEntities;
import net.abnormal.anabnormalcircumstance.entity.client.SilverArrowEntityRenderer;
import net.abnormal.anabnormalcircumstance.screen.HephaestusAltarScreen;
import net.abnormal.anabnormalcircumstance.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class AnAbnormalCircumstanceClient implements ClientModInitializer {
    @Override
    @SuppressWarnings("deprecation")
    public void onInitializeClient() {

        Keybinds.UNIQUE_ITEM_ABILITY.getDefaultKey();

//        ModModelPredicate.registerBow(ModItems.FIRST_LEAF);
        HandledScreens.register(ModScreenHandlers.HEPHAESTUS_ALTAR_SCREEN_HANDLER, HephaestusAltarScreen::new);
        EntityRendererRegistry.register(ModEntities.SILVER_ARROW, SilverArrowEntityRenderer::new);

        BlockEntityRendererRegistry.register(ModBlockEntities.HEPHAESTUS_ALTAR_BLOCK_ENTITY_BLOCK, HephaestusAltarBlockEntityRenderer::new);
    }
}