package net.abnormal.anabnormalcircumstance.event;

import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

public class WidowsGemEvents {

    private static final UUID DAMAGE_BONUS_ID = UUID.fromString("f84c8b72-3e6e-4a93-91d5-cb4e8a6eaf01");
    private static final EntityAttributeModifier DAMAGE_BONUS =
            new EntityAttributeModifier(DAMAGE_BONUS_ID, "Widow's Gem bonus", 3.0, EntityAttributeModifier.Operation.ADDITION);

    public static void register() {
        // Tick every world once per tick
        ServerTickEvents.END_WORLD_TICK.register(WidowsGemEvents::tickWorld);
    }

    private static void tickWorld(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            boolean hasGemEquipped = TrinketsApi.getTrinketComponent(player)
                    .map(component -> component.isEquipped(stack -> stack.getItem().getTranslationKey().contains("widows_gem")))
                    .orElse(false);

            EntityAttributeInstance attackAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            if (attackAttribute == null) return;

            boolean hasModifier = attackAttribute.getModifier(DAMAGE_BONUS_ID) != null;

            if (hasGemEquipped && !hasModifier) {
                attackAttribute.addPersistentModifier(DAMAGE_BONUS);
            } else if (!hasGemEquipped && hasModifier) {
                attackAttribute.removeModifier(DAMAGE_BONUS_ID);
            }
        }
    }
}
