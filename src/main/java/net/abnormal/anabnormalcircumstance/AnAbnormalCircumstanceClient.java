package net.abnormal.anabnormalcircumstance;

import net.abnormal.anabnormalcircumstance.block.entity.ModBlockEntities;
import net.abnormal.anabnormalcircumstance.block.entity.renderer.HephaestusAltarBlockEntityRenderer;
import net.abnormal.anabnormalcircumstance.entity.ModEntityRenderers;
import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.abnormal.anabnormalcircumstance.item.custom.TransmogModels;
import net.abnormal.anabnormalcircumstance.item.custom.TransmogTokenItem;
import net.abnormal.anabnormalcircumstance.magic.ModSpells;
import net.abnormal.anabnormalcircumstance.magic.client.ClientTickHandler;
import net.abnormal.anabnormalcircumstance.network.PacketHandler;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
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
        HandledScreens.register(ModScreenHandlers.HEPHAESTUS_ALTAR_SCREEN_HANDLER, HephaestusAltarScreen::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.HEPHAESTUS_ALTAR_BLOCK_ENTITY_BLOCK, HephaestusAltarBlockEntityRenderer::new);

        // Packet Registrations
        ClientTickHandler.register();
        ClientTickEvents.END_CLIENT_TICK.register(KeyBindingHandler::onClientTick);
        HudRenderCallback.EVENT.register(new net.abnormal.anabnormalcircumstance.magic.client.SpellHudRenderer());
        PacketHandlerClient.register();

        // Transmog Predicate Registration junk
        ModelPredicateProviderRegistry.register(
                ModItems.ARCANE_BLADE,
                new Identifier("anabnormalcircumstance", "transmog"),
                (ItemStack stack, net.minecraft.client.world.ClientWorld world,
                 net.minecraft.entity.LivingEntity entity, int seed) -> {

                    if (!stack.hasNbt()) return 0.0F;

                    String raw = stack.getNbt()
                            .getString("anabnormalcircumstance:transmog_item");

                    if (raw.isEmpty()) return 0.0F;

                    Identifier id = Identifier.tryParse(raw);
                    if (id == null) return 0.0F;

                    return TransmogModels.getModelValue(id);
                }
        );


    }

    private void onClientTick(MinecraftClient client) {
        KeyBindingHandler.onClientTick(client);
    }
}