package net.abnormal.anabnormalcircumstance.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentStateManager;

import java.util.Objects;
import java.util.UUID;

public class ModAdvancementHandler {
    private static final UUID ORC_HEALTH_BOOST_ID = UUID.fromString("b3f9676c-11f8-4d64-8e0c-5df1cc1e9a02");
    private static final UUID BROOD_HEALTH_BOOST_ID = UUID.fromString("e2f29b5d-9c7d-4b38-90e7-5dcffdbba1a3");

    private static final Identifier ORC_ADV = new Identifier("anabnormalcircumstance", "01_orc_slayer");
    private static final Identifier BROOD_ADV = new Identifier("anabnormalcircumstance", "02_brood_slayer");

    private static final String PERSIST_KEY = "anabnormal_advancement_boosts";

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(ModAdvancementHandler::onServerTick);

        // You don't need to copy custom booleans via readCustomDataFromNbt.
        // If you have other per-player data that must be copied on death, handle it individually here.
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            // If you must keep certain runtime fields, copy them manually here.
            // But do NOT call readCustomDataFromNbt on the new player with a general NBT blob.
        });
    }

    private static void onServerTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            AdvancementBoostState state = getState(player);

            checkAndReward(player, ORC_ADV, ORC_HEALTH_BOOST_ID, "Orc Slayer Boost", state, true);
            checkAndReward(player, BROOD_ADV, BROOD_HEALTH_BOOST_ID, "Brood Slayer Boost", state, false);
        }
    }

    private static AdvancementBoostState getState(ServerPlayerEntity player) {
        // pick any world; persistent state manager is per-dimension but we will store it on overworld
        PersistentStateManager manager = player.getServerWorld()
                .getPersistentStateManager();
        AdvancementBoostState state = manager.getOrCreate(AdvancementBoostState::fromNbt,
                AdvancementBoostState::new, PERSIST_KEY);
        return state;
    }

    private static void checkAndReward(ServerPlayerEntity player, Identifier advId, UUID boostId, String name,
                                       AdvancementBoostState state, boolean isOrc) {
        Advancement adv = Objects.requireNonNull(player.getServer()).getAdvancementLoader().get(advId);
        if (adv == null) return;

        boolean alreadyHasBoost = isOrc ? state.hasOrc(player.getUuid()) : state.hasBrood(player.getUuid());

        if (!alreadyHasBoost) {
            AdvancementProgress progress = player.getAdvancementTracker().getProgress(adv);
            if (progress.isDone()) {
                giveHeartBoost(player, boostId, name);
                if (isOrc) state.addOrc(player.getUuid());
                else state.addBrood(player.getUuid());
            }
        } else {
            ensureHeartBoost(player, boostId, name);
        }
    }

    private static void giveHeartBoost(ServerPlayerEntity player, UUID uuid, String name) {
        var attr = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (attr == null) return;

        if (attr.getModifier(uuid) == null) {
            EntityAttributeModifier modifier =
                    new EntityAttributeModifier(uuid, name, 4.0, EntityAttributeModifier.Operation.ADDITION);
            attr.addPersistentModifier(modifier);
            player.setHealth(player.getMaxHealth());
        }
    }

    private static void ensureHeartBoost(ServerPlayerEntity player, UUID uuid, String name) {
        var attr = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (attr == null) return;
        if (attr.getModifier(uuid) == null) {
            giveHeartBoost(player, uuid, name);
        }
    }
}
