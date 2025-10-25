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
 * Component keys are created in the component entrypoint (not during static init)
 * to avoid early static initialization issues.
 */
public final class ModComponents implements EntityComponentInitializer {

    // Keys are initialized in registerEntityComponentFactories (component entrypoint)
    public static ComponentKey<ManaComponent> MANA;
    public static ComponentKey<SpellSlotsComponent> SLOTS;

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        // Create keys here (safe because this is the CCA entrypoint)
        MANA = ComponentRegistryV3.INSTANCE.getOrCreate(
                new Identifier(AnAbnormalCircumstance.MOD_ID, "mana"), ManaComponent.class);

        SLOTS = ComponentRegistryV3.INSTANCE.getOrCreate(
                new Identifier(AnAbnormalCircumstance.MOD_ID, "spell_slots"), SpellSlotsComponent.class);

        // Attach components to players
        registry.registerForPlayers(MANA, player -> new ManaComponentImpl(), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(SLOTS, player -> new SpellSlotsComponentImpl(), RespawnCopyStrategy.ALWAYS_COPY);

        // Register server tick events here (after keys/components are created)
        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                ManaComponent mana = MANA.get(player);
                SpellSlotsComponent slots = SLOTS.get(player);
                if (mana != null) mana.tick(player);
                if (slots != null) slots.tick(player);

                if (server.getTicks() % 20 == 0) {
                    PacketHandler.syncSpellStateToClient(player, mana, slots);
                }
            }
        });
    }

    // keep this method removed/unused — server tick registration moved into the entrypoint
}
