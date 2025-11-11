package net.abnormal.anabnormalcircumstance.event;

import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.abnormal.anabnormalcircumstance.item.ModItems;

import java.util.UUID;

public class MarkOfAChampionEvents {

    private static final UUID CHAMPION_HEALTH_ID = UUID.fromString("6c2e1c23-338a-4cfa-8b48-772b3a927a4b");
    private static final EntityAttributeModifier CHAMPION_HEALTH_MOD =
            new EntityAttributeModifier(CHAMPION_HEALTH_ID, "Mark of a Champion health bonus", 4.0, EntityAttributeModifier.Operation.ADDITION);
    // 4.0 = +2 hearts

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(MarkOfAChampionEvents::onWorldTick);
    }

    private static void onWorldTick(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            boolean hasMark = TrinketsApi.getTrinketComponent(player)
                    .map(component -> component.isEquipped(ModItems.MARK_OF_A_CHAMPION))
                    .orElse(false);

            EntityAttributeInstance maxHpAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            if (maxHpAttr == null) continue;

            boolean hasModifier = maxHpAttr.getModifier(CHAMPION_HEALTH_ID) != null;

            if (hasMark && !hasModifier) {
                maxHpAttr.addPersistentModifier(CHAMPION_HEALTH_MOD);
                // heal up to new max so the player doesn't stay at lower health
                player.setHealth(player.getMaxHealth());
            } else if (!hasMark && hasModifier) {
                maxHpAttr.removeModifier(CHAMPION_HEALTH_ID);
                // clamp health to new max
                if (player.getHealth() > player.getMaxHealth()) {
                    player.setHealth(player.getMaxHealth());
                }
            }
        }
    }
}
