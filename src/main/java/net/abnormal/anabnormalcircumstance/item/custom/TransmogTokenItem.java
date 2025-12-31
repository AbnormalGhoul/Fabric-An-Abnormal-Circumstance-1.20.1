package net.abnormal.anabnormalcircumstance.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * TransmogTokenItem
 *
 * Offhand-only consumable item that applies a visual override
 * to the player's main-hand item using NBT.
 *
 * The overridden item keeps all original stats and behavior.
 */
public class TransmogTokenItem extends Item {

    public static final String TRANSMOG_KEY = "TransmogItem";

    public TransmogTokenItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {

        // Must be used from offhand
        if (hand != Hand.OFF_HAND) {
            return TypedActionResult.fail(player.getStackInHand(hand));
        }

        ItemStack tokenStack = player.getOffHandStack();
        ItemStack targetStack = player.getMainHandStack();

        if (world.isClient) {
            return TypedActionResult.pass(tokenStack);
        }

        // Mainhand must contain an item
        if (targetStack.isEmpty()) {
            return TypedActionResult.fail(tokenStack);
        }

        NbtCompound tokenNbt = tokenStack.getNbt();
        if (tokenNbt == null || !tokenNbt.contains(TRANSMOG_KEY)) {
            return TypedActionResult.fail(tokenStack);
        }

        String transmogIdString = tokenNbt.getString(TRANSMOG_KEY);

        // Validate identifier
        Identifier transmogId = Identifier.tryParse(transmogIdString);
        if (transmogId == null || !Registries.ITEM.containsId(transmogId)) {
            return TypedActionResult.fail(tokenStack);
        }

        // Prevent transmogging into the same item
        Identifier targetId = Registries.ITEM.getId(targetStack.getItem());
        if (targetId.equals(transmogId)) {
            return TypedActionResult.fail(tokenStack);
        }

        // Apply visual override
        NbtCompound targetNbt = targetStack.getOrCreateNbt();
        targetNbt.putString(TRANSMOG_KEY, transmogId.toString());

        // Consume exactly one token
        tokenStack.decrement(1);

        return TypedActionResult.success(tokenStack);
    }
}

