package net.abnormal.anabnormalcircumstance;

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

import java.util.UUID;

public class ModAdvancementHandler {
    private static final UUID ORC_HEALTH_BOOST_ID = UUID.fromString("b3f9676c-11f8-4d64-8e0c-5df1cc1e9a02");
    private static final UUID BROOD_HEALTH_BOOST_ID = UUID.fromString("e2f29b5d-9c7d-4b38-90e7-5dcffdbba1a3");

    private static final String ORC_KEY = "anabnormalcircumstance_orc_heart_boost";
    private static final String BROOD_KEY = "anabnormalcircumstance_brood_heart_boost";

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(ModAdvancementHandler::onServerTick);

        // Copy persistent NBT between deaths
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            NbtCompound oldData = new NbtCompound();
            oldPlayer.writeCustomDataToNbt(oldData);

            NbtCompound newData = new NbtCompound();
            newPlayer.writeCustomDataToNbt(newData);

            if (oldData.getBoolean(ORC_KEY)) {
                newData.putBoolean(ORC_KEY, true);
            }
            if (oldData.getBoolean(BROOD_KEY)) {
                newData.putBoolean(BROOD_KEY, true);
            }

            newPlayer.readCustomDataFromNbt(newData);
        });
    }

    private static void onServerTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            Identifier orcId = new Identifier("anabnormalcircumstance", "orc_slayer");
            Identifier broodId = new Identifier("anabnormalcircumstance", "brood_slayer");

            checkAndReward(player, orcId, ORC_HEALTH_BOOST_ID, "Orc Slayer Boost", ORC_KEY);
            checkAndReward(player, broodId, BROOD_HEALTH_BOOST_ID, "Brood Slayer Boost", BROOD_KEY);
        }
    }

    private static void checkAndReward(ServerPlayerEntity player, Identifier id, UUID boostId, String name, String tagKey) {
        Advancement adv = player.getServer().getAdvancementLoader().get(id);
        if (adv == null) return;

        NbtCompound data = new NbtCompound();
        player.writeCustomDataToNbt(data);
        boolean alreadyHasBoost = data.getBoolean(tagKey);

        if (!alreadyHasBoost) {
            AdvancementProgress progress = player.getAdvancementTracker().getProgress(adv);
            if (progress.isDone()) {
                giveHeartBoost(player, boostId, name);
                data.putBoolean(tagKey, true);
                player.readCustomDataFromNbt(data);
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
