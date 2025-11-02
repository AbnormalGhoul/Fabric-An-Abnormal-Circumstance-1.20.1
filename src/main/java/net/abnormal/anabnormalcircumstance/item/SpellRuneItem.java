package net.abnormal.anabnormalcircumstance.item;

import net.abnormal.anabnormalcircumstance.component.ModComponents;
import net.abnormal.anabnormalcircumstance.network.PacketHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

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

        // Play sound effect
        world.playSound(
                null,                                 // null means all players nearby hear it
                user.getBlockPos(),                          // sound position
                SoundEvents.BLOCK_SCULK_SHRIEKER_BREAK,      // pick any fitting sound
                SoundCategory.BLOCKS,                        // player sound category
                3.0F,                                        // volume
                1.0F                                         // pitch
        );

        // Consume rune on use
        stack.decrement(1);

        if (user instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.sendMessage(Text.literal("§bAll spells unequipped."), true);
            PacketHandler.syncSpellStateToClient(serverPlayer, ModComponents.MANA.get(user), slots);
        }

        return TypedActionResult.success(stack, world.isClient());
    }
}
