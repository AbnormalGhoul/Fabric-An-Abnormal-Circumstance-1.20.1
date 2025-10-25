package net.abnormal.anabnormalcircumstance.item;

import net.abnormal.anabnormalcircumstance.component.ModComponents;
import net.abnormal.anabnormalcircumstance.network.PacketHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * Right-click to clear all spell slots (server-side).
 */
public class SpellRuneItem extends Item {
    public SpellRuneItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.pass(stack);

        var slots = ModComponents.SLOTS.get(user);
        if (slots == null) return TypedActionResult.fail(stack);

        // Clear all spells
        slots.clearAllSpells();

        // Consume rune on use
        stack.decrement(1);

        if (user instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.sendMessage(Text.literal("§bAll spells unequipped."), true);
            PacketHandler.syncSpellStateToClient(serverPlayer, ModComponents.MANA.get(user), slots);
        }

        return TypedActionResult.success(stack, world.isClient());
    }
}
