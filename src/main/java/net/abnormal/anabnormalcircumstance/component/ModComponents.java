package net.abnormal.anabnormalcircumstance.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.network.PacketHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Registers and manages custom player components via Cardinal Components API.
 * Handles tick updates and periodic HUD synchronization.
 *
 * This class is automatically discovered and initialized by Cardinal Components.
 * No manual calls are needed in onInitialize().
 */
public final class ModComponents implements EntityComponentInitializer {

    // === COMPONENT KEYS ===
    public static final ComponentKey<ManaComponent> MANA =
            ComponentRegistryV3.INSTANCE.getOrCreate(
                    new Identifier(AnAbnormalCircumstance.MOD_ID, "mana"),
                    ManaComponent.class
            );

    public static final ComponentKey<SpellSlotsComponent> SLOTS =
            ComponentRegistryV3.INSTANCE.getOrCreate(
                    new Identifier(AnAbnormalCircumstance.MOD_ID, "spell_slots"),
                    SpellSlotsComponent.class
            );

    /**
     * Automatically called by Cardinal Components during mod initialization.
     * Registers custom components for PlayerEntity instances.
     */
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(MANA, player -> new ManaComponentImpl(), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(SLOTS, player -> new SpellSlotsComponentImpl(), RespawnCopyStrategy.ALWAYS_COPY);

        // Also register tick updates after components are initialized
        registerServerTickEvents();
    }

    /**
     * Registers periodic tick logic for component updates and client sync.
     */
    private static void registerServerTickEvents() {
        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                ManaComponent mana = MANA.get(player);
                SpellSlotsComponent slots = SLOTS.get(player);

                if (mana != null) mana.tick(player);
                if (slots != null) slots.tick(player);

                // Sync to client once per second (every 20 ticks)
                if (server.getTicks() % 20 == 0) {
                    PacketHandler.syncSpellStateToClient(player, mana, slots);
                }
            }
        });
    }
}
