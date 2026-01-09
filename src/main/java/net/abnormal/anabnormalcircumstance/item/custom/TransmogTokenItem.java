package net.abnormal.anabnormalcircumstance.item.custom;

import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TransmogTokenItem extends Item {
    // /give @p anabnormalcircumstance:transmog_token{"anabnormalcircumstance:transmog_item":"anabnormalcircumstance:red_hammer"}

    public static final String TRANSMOG_KEY = "anabnormalcircumstance:transmog_item";

    public TransmogTokenItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {

        // Token must be in OFFHAND
        if (hand != Hand.OFF_HAND) {
            return TypedActionResult.pass(player.getStackInHand(hand));
        }

        if (world.isClient()) {
            return TypedActionResult.success(player.getStackInHand(hand));
        }

        ItemStack token = player.getOffHandStack();
        ItemStack mainhand = player.getMainHandStack();

        // Must be holding Arcane Blade
        if (!mainhand.isOf(ModItems.ARCANE_BLADE)) {
            return TypedActionResult.fail(token);
        }

        NbtCompound tokenNbt = token.getNbt();
        if (tokenNbt == null || !tokenNbt.contains(TRANSMOG_KEY)) {
            return TypedActionResult.fail(token);
        }

        String itemId = tokenNbt.getString(TRANSMOG_KEY);
        Identifier id = Identifier.tryParse(itemId);

        if (id == null || !Registries.ITEM.containsId(id)) {
            return TypedActionResult.fail(token);
        }

        // Apply transmog to Arcane Blade
        NbtCompound bladeNbt = mainhand.getOrCreateNbt();
        bladeNbt.putString(TRANSMOG_KEY, itemId);

        // Notify player
        player.sendMessage(Text.literal("Blade has been Transmogrified!").formatted(Formatting.DARK_PURPLE), true);

        // Play sound
        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                SoundCategory.PLAYERS,
                1.0F,
                1.2F
        );

        // Consume token
        token.decrement(1);

        return TypedActionResult.success(token);
    }
}
