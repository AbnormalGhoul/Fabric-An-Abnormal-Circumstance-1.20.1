package net.abnormal.anabnormalcircumstance.util;

import com.mojang.brigadier.CommandDispatcher;
import net.abnormal.anabnormalcircumstance.component.ModComponents;
import net.abnormal.anabnormalcircumstance.component.ManaComponent;
import net.abnormal.anabnormalcircumstance.component.SpellSlotsComponent;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.abnormal.anabnormalcircumstance.network.PacketHandler;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public final class AbnormalMagicCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("abnormal_magic")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(
                                literal("reset_cooldown")
                                        .then(
                                                CommandManager.argument("player", EntityArgumentType.player())
                                                        .executes(ctx -> {
                                                            ServerPlayerEntity target =
                                                                    EntityArgumentType.getPlayer(ctx, "player");

                                                            resetCooldowns(target);

                                                            ctx.getSource().sendFeedback(
                                                                    () -> Text.literal(
                                                                            "Reset all spell cooldowns for "
                                                                                    + target.getName().getString()
                                                                    ),
                                                                    true
                                                            );

                                                            return 1;
                                                        })
                                        )
                        )
        );
    }

    private static void resetCooldowns(ServerPlayerEntity player) {
        SpellSlotsComponent slots = ModComponents.SLOTS.get(player);
        ManaComponent mana = ModComponents.MANA.get(player);

        for (SpellTier tier : SpellTier.values()) {
            slots.setCooldownTicks(tier, 0);
        }

        // Sync immediately so client HUD updates
        PacketHandler.syncSpellStateToClient(player, mana, slots);
    }
}
