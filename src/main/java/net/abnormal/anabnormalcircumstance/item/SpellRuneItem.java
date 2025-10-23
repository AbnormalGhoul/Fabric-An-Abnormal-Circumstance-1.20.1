package net.abnormal.anabnormalcircumstance.item;

import net.abnormal.anabnormalcircumstance.component.ModComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;

/**
 * Right-click to clear all spell slots (server-side).
 */
public class SpellRuneItem extends Item {
    public SpellRuneItem(Settings settings) { super(settings); }

    @Override
    public TypedActionResult<ItemStack> use(net.minecraft.world.World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return TypedActionResult.pass(user.getStackInHand(hand));
        var slots = ModComponents.SLOTS.get(user);
        if (slots == null) return TypedActionResult.fail(user.getStackInHand(hand));
        slots.clearAllSpells();
        if (user instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity) user).sendMessage(Text.literal("All spells unequipped."), true);
            net.abnormal.anabnormalcircumstance.network.PacketHandler.syncSpellStateToClient((ServerPlayerEntity) user, ModComponents.MANA.get(user), slots);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
