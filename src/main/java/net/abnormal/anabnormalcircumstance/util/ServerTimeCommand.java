package net.abnormal.anabnormalcircumstance.util;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;

public class ServerTimeCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommands(dispatcher);
        });
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {

        dispatcher.register(
                CommandManager.literal("server_time")
                        .requires(src -> src.hasPermissionLevel(2))

                        // DEFAULT COMMAND: /server_time
                        .executes(ctx -> {
                            LocalTime now = LocalTime.now();
                            String formatted = String.format(
                                    "%02d:%02d:%02d",
                                    now.getHour(), now.getMinute(), now.getSecond()
                            );
                            ctx.getSource().sendFeedback(() -> Text.literal(formatted), false);
                            return now.getHour(); // return hour as numeric result
                        })

                        // /server_time time
                        .then(CommandManager.literal("time")
                                .executes(ctx -> {
                                    LocalTime now = LocalTime.now();
                                    String formatted = String.format(
                                            "%02d:%02d:%02d",
                                            now.getHour(), now.getMinute(), now.getSecond()
                                    );
                                    ctx.getSource().sendFeedback(() -> Text.literal(formatted), false);
                                    return now.getHour(); // numeric result (hour)
                                })
                        )

                        .then(CommandManager.literal("hour")
                                .executes(ctx -> {
                                    int hour = LocalTime.now().getHour();
                                    ctx.getSource().sendFeedback(() -> Text.literal("" + hour), false);
                                    return hour;
                                })
                        )
                        .then(CommandManager.literal("minute")
                                .executes(ctx -> {
                                    int minute = LocalTime.now().getMinute();
                                    ctx.getSource().sendFeedback(() -> Text.literal("" + minute), false);
                                    return minute;
                                })
                        )
                        .then(CommandManager.literal("second")
                                .executes(ctx -> {
                                    int second = LocalTime.now().getSecond();
                                    ctx.getSource().sendFeedback(() -> Text.literal("" + second), false);
                                    return second;
                                })
                        )
                        .then(CommandManager.literal("is_weekend")
                                .executes(ctx -> {
                                    DayOfWeek day = LocalDate.now().getDayOfWeek();
                                    boolean weekend = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY);

                                    int result = weekend ? 1 : 0;

                                    ctx.getSource().sendFeedback(
                                            () -> Text.literal("" + weekend),
                                            false
                                    );
                                    return result;
                                })
                        )
        );
    }
}