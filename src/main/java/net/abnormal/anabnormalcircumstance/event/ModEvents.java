package net.abnormal.anabnormalcircumstance.event;

import net.abnormal.anabnormalcircumstance.magic.spells.hydromancy.BileWaterSpell;
import net.abnormal.anabnormalcircumstance.magic.spells.hydromancy.ControlWeatherSpell;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class ModEvents {
    public static void registerEvents() {
        ModAttackEvent.register();

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient && entity instanceof LivingEntity target) {
                BileWaterSpell.onEntityAttacked(player, target, player.getDamageSources().playerAttack(player));
                ControlWeatherSpell.onEntityAttacked(player, target, player.getDamageSources().playerAttack(player));
            }
            return ActionResult.PASS;
        });

        // Tick every world and spawn aura particles for players with active buff
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            // world is a ServerWorld
            for (ServerPlayerEntity player : world.getPlayers()) {
                ControlWeatherSpell.tick(player);
            }
        });
    }
}
