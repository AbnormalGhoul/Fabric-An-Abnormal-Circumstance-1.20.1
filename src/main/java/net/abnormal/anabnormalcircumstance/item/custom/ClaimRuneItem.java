package net.abnormal.anabnormalcircumstance.item.custom;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Optional;

public class ClaimRuneItem extends Item {
    public ClaimRuneItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, net.minecraft.entity.player.PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient) {
            if (!(user instanceof ServerPlayerEntity player)) {
                return TypedActionResult.pass(stack);
            }

            // Make sure the player is on a team first (optional check)
            Optional<Team> optionalTeam = FTBTeamsAPI.api().getManager().getTeamForPlayer(player);
            if (optionalTeam.isEmpty()) {
                player.sendMessage(Text.literal("§cYou are not in a team!"), false);
                return TypedActionResult.fail(stack);
            }

            // Execute the FTBChunks command directly as the player
            MinecraftServer server = player.getServer();
            if (server != null) {
                ServerCommandSource source = player.getCommandSource().withSilent();
                String command = "ftbchunks admin extra_claim_chunks @s add 1";

                int result = server.getCommandManager().executeWithPrefix(source, command);
                if (result >= 0) {
                    player.sendMessage(Text.literal("§aYour team's claim limit has increased by 1!"), false);
                    world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                            SoundCategory.PLAYERS, 1.0F, 1.0F);

                    if (!player.isCreative()) {
                        stack.decrement(1);
                    }

                    return TypedActionResult.success(stack, world.isClient());
                } else {
                    player.sendMessage(Text.literal("§cFailed to run command."), false);
                    return TypedActionResult.fail(stack);
                }
            }
        }

        return TypedActionResult.pass(stack);
    }
}
