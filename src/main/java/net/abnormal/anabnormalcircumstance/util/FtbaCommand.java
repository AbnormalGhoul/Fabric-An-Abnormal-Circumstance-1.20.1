package net.abnormal.anabnormalcircumstance.util;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.ftb.mods.ftbteams.api.*;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class FtbaCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("ftba")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("team", StringArgumentType.string())
                                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(ctx -> {
                                                    String teamName = StringArgumentType.getString(ctx, "team");
                                                    ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "player");
                                                    ServerCommandSource source = ctx.getSource();

                                                    TeamManager teamManager = FTBTeamsAPI.api().getManager();
                                                    UUID playerId = target.getUuid();

                                                    // Try to find the team by name (case-insensitive)
                                                    Optional<Team> optionalTeam = teamManager.getTeams().stream()
                                                            .filter(t -> t.getName().getString().equalsIgnoreCase(teamName))
                                                            .findFirst();

                                                    if (optionalTeam.isEmpty()) {
                                                        source.sendError(Text.literal("§cTeam '" + teamName + "' does not exist."));
                                                        return Command.SINGLE_SUCCESS;
                                                    }

                                                    Team team = optionalTeam.get();

                                                    // If player is already in a team, remove them from that one
                                                    teamManager.getTeamForPlayerID(playerId).ifPresent(oldTeam -> {
                                                        if (!oldTeam.getId().equals(team.getId())) {
                                                            Set<UUID> members = oldTeam.getMembers();
                                                            members.remove(playerId);
                                                            oldTeam.markDirty();
                                                        }
                                                    });

                                                    // Add player to the new team
                                                    Set<UUID> members = team.getMembers();
                                                    if (members.add(playerId)) {
                                                        team.markDirty();
                                                        teamManager.markDirty();

                                                        source.sendFeedback(() -> Text.literal("§aAdded " + target.getName().getString()
                                                                + " to team " + teamName + "."), true);
                                                        target.sendMessage(Text.literal("§aYou have been added to team " + teamName + "!"), false);
                                                    } else {
                                                        source.sendError(Text.literal("§ePlayer is already in team " + teamName + "."));
                                                    }

                                                    return Command.SINGLE_SUCCESS;
                                                })))
                        )
        );
    }
}
