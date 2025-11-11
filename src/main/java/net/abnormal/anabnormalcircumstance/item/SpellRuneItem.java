package net.abnormal.anabnormalcircumstance.item;

import net.abnormal.anabnormalcircumstance.component.ModComponents;
import net.abnormal.anabnormalcircumstance.network.PacketHandler;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

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

        // If player has no spells equipped, notify and do not consume the rune
        boolean anyBound = false;
        for (SpellTier t : SpellTier.values()) {
            if (slots.getSpellForTier(t) != null) {
                anyBound = true;
                break;
            }
        }
        if (!anyBound) {
            if (user instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.sendMessage(Text.literal("§cNo Spells Bound"), true);
            } else {
                user.sendMessage(Text.literal("§cNo Spells Bound"), true);
            }
            return TypedActionResult.fail(stack);
        }

        List<ItemStack> returnScrolls = new ArrayList<>();

        // Collect Tier 4 and Tier 5 equipped spells into scroll stacks (server-side only)
        if (user instanceof ServerPlayerEntity) {
            for (SpellTier t : new SpellTier[]{SpellTier.TIER_4, SpellTier.TIER_5}) {
                var id = slots.getSpellForTier(t);
                if (id != null) {
                    ItemStack scrollStack = new ItemStack(ModItems.SPELL_SCROLL); // adjust name if different
                    NbtCompound tag = scrollStack.getOrCreateNbt();
                    tag.putString("spell_id", id.toString());
                    returnScrolls.add(scrollStack);
                }
            }
        }

        // Clear all equipped spells
        slots.clearAllSpells();

        // Play sound effect
        world.playSound(
                null,
                user.getBlockPos(),
                SoundEvents.BLOCK_SCULK_SHRIEKER_BREAK,
                SoundCategory.BLOCKS,
                3.0F,
                1.0F
        );

        // Consume rune on use
        stack.decrement(1);

        if (user instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.sendMessage(Text.literal("§bAll spells unequipped."), true);

            // Give returned scrolls to player (try inventory, otherwise drop)
            for (ItemStack s : returnScrolls) {
                boolean added = serverPlayer.getInventory().insertStack(s);
                if (!added) {
                    serverPlayer.getWorld().spawnEntity(new ItemEntity(serverPlayer.getWorld(), serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), s));
                }
            }

            PacketHandler.syncSpellStateToClient(serverPlayer, ModComponents.MANA.get(user), slots);
        } else {
            // Fallback for non-server players (shouldn't normally run because earlier client check)
            for (ItemStack s : returnScrolls) {
                world.spawnEntity(new ItemEntity(world, user.getX(), user.getY(), user.getZ(), s));
            }
        }

        return TypedActionResult.success(stack, world.isClient());
    }
}