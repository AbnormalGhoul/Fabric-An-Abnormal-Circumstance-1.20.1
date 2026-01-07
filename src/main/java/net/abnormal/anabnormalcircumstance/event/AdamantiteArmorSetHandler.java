package net.abnormal.anabnormalcircumstance.event;

import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.abnormal.anabnormalcircumstance.util.ArmorSetUtils;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;

public final class AdamantiteArmorSetHandler {

    private AdamantiteArmorSetHandler() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {

                boolean hasSet = ArmorSetUtils.hasFullAdamantite(player);
                boolean hasEffect = player.hasStatusEffect(ModEffects.ADAMANTITE_POWER);

                if (hasSet && !hasEffect) {
                    player.addStatusEffect(new StatusEffectInstance(
                            ModEffects.ADAMANTITE_POWER,
                            220,
                            0,
                            false,
                            false,
                            true
                    ));
                }

                if (!hasSet && hasEffect) {
                    player.removeStatusEffect(ModEffects.ADAMANTITE_POWER);
                }
            }
        });
    }
}
