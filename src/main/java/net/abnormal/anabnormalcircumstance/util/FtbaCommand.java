package net.abnormal.anabnormalcircumstance.util;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.TeamManager;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class FtbaCommand {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Register /ftba add <team> <player>
     * Requires operator permission level 2.
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("ftba")
                        .requires(src -> src.hasPermissionLevel(2))
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("team", StringArgumentType.string())
                                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                                .executes(ctx -> {
                                                    ServerCommandSource source = ctx.getSource();
                                                    String teamArg = StringArgumentType.getString(ctx, "team");
                                                    ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                                    UUID playerId = player.getUuid();

                                                    TeamManager tm = FTBTeamsAPI.api().getManager();
                                                    if (tm == null) {
                                                        source.sendError(Text.literal("§cFTB Teams manager unavailable."));
                                                        return Command.SINGLE_SUCCESS;
                                                    }

                                                    Optional<Team> targetTeamOpt = Optional.empty();
                                                    try {
                                                        UUID teamUuid = UUID.fromString(teamArg);
                                                        targetTeamOpt = tm.getTeamByID(teamUuid);
                                                    } catch (IllegalArgumentException ignored) {
                                                    }

                                                    if (targetTeamOpt.isEmpty()) {
                                                        targetTeamOpt = tm.getTeamByName(teamArg);
                                                    }

                                                    if (targetTeamOpt.isEmpty()) {
                                                        targetTeamOpt = tm.getTeams().stream()
                                                                .filter(t -> {
                                                                    try {
                                                                        return t.getName() != null && t.getName().getString().equalsIgnoreCase(teamArg);
                                                                    } catch (Exception e) {
                                                                        return false;
                                                                    }
                                                                })
                                                                .findFirst();
                                                    }

                                                    if (targetTeamOpt.isEmpty()) {
                                                        source.sendError(Text.literal("§cTeam '" + teamArg + "' does not exist."));
                                                        return Command.SINGLE_SUCCESS;
                                                    }

                                                    Team targetTeam = targetTeamOpt.get();
                                                    Optional<Team> oldTeamOpt = tm.getTeamForPlayerID(playerId);

                                                    try {
                                                        // Remove from old team (if present and different)
                                                        if (oldTeamOpt.isPresent()) {
                                                            Team oldTeam = oldTeamOpt.get();
                                                            if (oldTeam.getId().equals(targetTeam.getId())) {
                                                                source.sendError(Text.literal("§ePlayer is already in that team."));
                                                                return Command.SINGLE_SUCCESS;
                                                            }

                                                            boolean removed = tryModifyMembers(oldTeam, s -> s.remove(playerId));
                                                            if (!removed) {
                                                                LOGGER.warn("Could not remove player {} from old team {}", playerId, oldTeam.getId());
                                                            } else {
                                                                oldTeam.markDirty();
                                                            }
                                                        }

                                                        // Add to target team
                                                        boolean added = tryModifyMembers(targetTeam, s -> s.add(playerId));
                                                        if (!added) {
                                                            source.sendError(Text.literal("§ePlayer is already in team " + teamArg + " or adding failed."));
                                                            return Command.SINGLE_SUCCESS;
                                                        } else {
                                                            targetTeam.markDirty();
                                                            tm.markDirty();

                                                            source.sendFeedback(() -> Text.literal("§aAdded " + player.getName().getString() + " to team " + teamArg + "."), true);
                                                            player.sendMessage(Text.literal("§aYou have been added to team " + teamArg + "!"), false);
                                                        }

                                                    } catch (Exception e) {
                                                        LOGGER.error("Unhandled exception in /ftba add: ", e);
                                                        source.sendError(Text.literal("§cAn internal error occurred while executing the command. Check server log for details."));
                                                    }

                                                    return Command.SINGLE_SUCCESS;
                                                }))))
        );
    }

    // Functional interface to apply a mutation on a Set<UUID>
    private interface Mutator {
        boolean apply(Set<UUID> set) throws Exception;
    }

    // Try to modify the members set of a Team: direct mutation, then Set field replacement, then Map field replacement.
    private static boolean tryModifyMembers(Team team, Mutator mutator) {
        try {
            // 1) Try direct mutation of the returned set (may be unmodifiable)
            try {
                Set<UUID> members = team.getMembers();
                if (members == null) members = new HashSet<>();
                boolean result = mutator.apply(members);
                if (result) {
                    return true;
                }
                // if result false and members appears mutable, nothing to do
                if (members instanceof HashSet) return false;
            } catch (UnsupportedOperationException ignored) {
                // fallthrough to reflection approaches
            }

            // 2) Try to replace a Set field on the team instance
            if (replaceSetField(team, mutator)) {
                return true;
            }

            // 3) Try to replace a Map field that holds player->rank entries (adjust keys)
            if (replaceMapFieldKeys(team, mutator)) {
                return true;
            }

            return false;
        } catch (Exception e) {
            LOGGER.error("Exception while modifying members for team {}: {}", team.getId(), e.toString());
            return false;
        }
    }

    // Find a Set field on the team and replace it with a mutable copy after applying mutator
    private static boolean replaceSetField(Team team, Mutator mutator) {
        Class<?> cls = team.getClass();
        while (cls != null) {
            Field[] fields = cls.getDeclaredFields();
            for (Field f : fields) {
                try {
                    if (!Set.class.isAssignableFrom(f.getType())) continue;
                    f.setAccessible(true);
                    Object val = f.get(team);
                    Set<UUID> current = val == null ? new HashSet<>() : new HashSet<>((Set<UUID>) val);
                    boolean changed = mutator.apply(current);
                    if (!changed) return false;
                    f.set(team, current);
                    return true;
                } catch (Throwable t) {
                    // ignore and continue searching
                }
            }
            cls = cls.getSuperclass();
        }
        return false;
    }

    // Find a Map field on the team that looks like player->rank and replace it with a modified copy of keys.
    private static boolean replaceMapFieldKeys(Team team, Mutator mutator) {
        Class<?> cls = team.getClass();
        while (cls != null) {
            Field[] fields = cls.getDeclaredFields();
            for (Field f : fields) {
                try {
                    if (!Map.class.isAssignableFrom(f.getType())) continue;
                    f.setAccessible(true);
                    Object val = f.get(team);
                    Map<?, ?> original = val == null ? new HashMap<>() : (Map<?, ?>) val;

                    // quick key check: keys should be UUID
                    boolean looksLikePlayersMap = true;
                    for (Object k : original.keySet()) {
                        if (k != null && !(k instanceof UUID)) {
                            looksLikePlayersMap = false;
                            break;
                        }
                    }
                    if (!looksLikePlayersMap) {
                        // if empty map, still consider it as possible players map
                        // continue (we'll still attempt)
                    }

                    // build key set, apply mutator
                    Set<UUID> keys = new HashSet<>();
                    for (Object k : original.keySet()) {
                        if (k instanceof UUID) keys.add((UUID) k);
                    }

                    boolean changed = mutator.apply(keys);
                    if (!changed) return false;

                    // create new map: copy original, remove keys not present, add new keys reusing a sample value
                    Map<Object, Object> newMap = new HashMap<>();
                    Object sampleValue = null;
                    for (Map.Entry<?, ?> e : original.entrySet()) {
                        newMap.put(e.getKey(), e.getValue());
                        if (sampleValue == null && e.getValue() != null) sampleValue = e.getValue();
                    }

                    // remove keys that no longer exist
                    newMap.keySet().removeIf(k -> (k instanceof UUID) && !keys.contains((UUID) k));

                    // add keys that are new
                    for (UUID k : keys) {
                        if (!newMap.containsKey(k)) {
                            newMap.put(k, sampleValue); // reuse existing value (rank) if available; may be null
                        }
                    }

                    // set the new map onto the field
                    f.set(team, newMap);
                    return true;
                } catch (Throwable t) {
                    // ignore and continue searching
                }
            }
            cls = cls.getSuperclass();
        }
        return false;
    }
}