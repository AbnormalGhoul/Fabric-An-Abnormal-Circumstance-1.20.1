package net.abnormal.anabnormalcircumstance;

import net.abnormal.anabnormalcircumstance.block.entity.ModBlockEntities;
import net.abnormal.anabnormalcircumstance.block.entity.renderer.HephaestusAltarBlockEntityRenderer;
import net.abnormal.anabnormalcircumstance.entity.ModEntityRenderers;
import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.abnormal.anabnormalcircumstance.item.util.TransmogModels;
import net.abnormal.anabnormalcircumstance.magic.ModSpells;
import net.abnormal.anabnormalcircumstance.magic.client.ClientTickHandler;
import net.abnormal.anabnormalcircumstance.network.PacketHandlerClient;
import net.abnormal.anabnormalcircumstance.screen.HephaestusAltarScreen;
import net.abnormal.anabnormalcircumstance.screen.ModScreenHandlers;
import net.abnormal.anabnormalcircumstance.util.KeyBindingHandler;
import net.abnormal.anabnormalcircumstance.util.ModModelPredicate;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class AnAbnormalCircumstanceClient implements ClientModInitializer {
    @Override
    @SuppressWarnings("deprecation")
    public void onInitializeClient() {

        // General Registrations
        ModSpells.registerAll();
        ModModelPredicate.registerSpellScrollPredicate();
        ModEntityRenderers.register();
        KeyBindingHandler.register();

        // Specific Registrations
        ModModelPredicate.registerBow(ModItems.FIRST_LEAF);
        ModModelPredicate.registerBow(ModItems.ARCANE_BOW);
        ModModelPredicate.registerBow(ModItems.ARACHNID_BOW);
        ModModelPredicate.registerBow(ModItems.ETERNAL_BOW);
        ModModelPredicate.registerBow(ModItems.BASALT_BOW);
        HandledScreens.register(ModScreenHandlers.HEPHAESTUS_ALTAR_SCREEN_HANDLER, HephaestusAltarScreen::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.HEPHAESTUS_ALTAR_BLOCK_ENTITY_BLOCK, HephaestusAltarBlockEntityRenderer::new);

        // Packet Registrations
        ClientTickHandler.register();
        ClientTickEvents.END_CLIENT_TICK.register(KeyBindingHandler::onClientTick);
        HudRenderCallback.EVENT.register(new net.abnormal.anabnormalcircumstance.magic.client.SpellHudRenderer());
        PacketHandlerClient.register();

        // Arcane Blade transmog predicate
        ModelPredicateProviderRegistry.register(
                ModItems.ARCANE_BLADE,
                new Identifier("anabnormalcircumstance", "transmog"),
                (stack, world, entity, seed) -> {
                    if (!stack.hasNbt()) return 0.0F;
                    String raw = stack.getNbt().getString("anabnormalcircumstance:transmog_item");
                    if (raw.isEmpty()) return 0.0F;
                    Identifier id = Identifier.tryParse(raw);
                    return id == null ? 0.0F : TransmogModels.getModelValue(id);
                }
        );

        // Arcane Axe transmog predicate
        ModelPredicateProviderRegistry.register(
                ModItems.ARCANE_AXE,
                new Identifier("anabnormalcircumstance", "transmog"),
                (stack, world, entity, seed) -> {
                    if (!stack.hasNbt()) return 0.0F;
                    String raw = stack.getNbt().getString("anabnormalcircumstance:transmog_item");
                    if (raw.isEmpty()) return 0.0F;
                    Identifier id = Identifier.tryParse(raw);
                    return id == null ? 0.0F : TransmogModels.getModelValue(id);
                }
        );

        // Arcane Bow transmog predicate
        ModelPredicateProviderRegistry.register(
                ModItems.ARCANE_BOW,
                new Identifier("anabnormalcircumstance", "transmog"),
                (stack, world, entity, seed) -> {
                    if (!stack.hasNbt()) return 0.0F;
                    String raw = stack.getNbt().getString("anabnormalcircumstance:transmog_item");
                    if (raw.isEmpty()) return 0.0F;
                    Identifier id = Identifier.tryParse(raw);
                    return id == null ? 0.0F : TransmogModels.getModelValue(id);
                }
        );

        // Karambit transmog predicate
        ModelPredicateProviderRegistry.register(
                ModItems.KARAMBIT,
                new Identifier("anabnormalcircumstance", "transmog"),
                (stack, world, entity, seed) -> {
                    if (!stack.hasNbt()) return 0.0F;
                    String raw = stack.getNbt().getString("anabnormalcircumstance:transmog_item");
                    if (raw.isEmpty()) return 0.0F;
                    Identifier id = Identifier.tryParse(raw);
                    return id == null ? 0.0F : TransmogModels.getModelValue(id);
                }
        );

    }

    private void onClientTick(MinecraftClient client) {
        KeyBindingHandler.onClientTick(client);
    }
}