package net.abnormal.anabnormalcircumstance.item.custom;

import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.minecraft.client.item.TooltipContext;
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

import java.util.List;
import java.util.Set;

public class TransmogTokenItem extends Item {
    // /give @p anabnormalcircumstance:transmog_token{"anabnormalcircumstance:transmog_item":"anabnormalcircumstance:red_hammer"}

    public static final String TRANSMOG_KEY = "anabnormalcircumstance:transmog_item";

    // Allowed transmog targets for each base item
    private static final Set<String> SWORD_TRANSMOGS = Set.of(
            "anabnormalcircumstance:crown_blade",
            "anabnormalcircumstance:sylvestrian_blade",
            "anabnormalcircumstance:last_rose",
            "anabnormalcircumstance:great_sword",
            "anabnormalcircumstance:necromancer_blade",
            "anabnormalcircumstance:toxic_scythe",
            "anabnormalcircumstance:cataclysm",
            "anabnormalcircumstance:pox_spreader",
            "anabnormalcircumstance:holy_spear",
            "anabnormalcircumstance:magnetite_sword",
            "anabnormalcircumstance:demonic_blade",
            "anabnormalcircumstance:cursed_blade",
            "anabnormalcircumstance:oceanic_might",
            "anabnormalcircumstance:beecomb_sword",
            "anabnormalcircumstance:mana_spear",
            "anabnormalcircumstance:death_grip",
            "anabnormalcircumstance:witch_scythe",
            "anabnormalcircumstance:battle_standard",
            "anabnormalcircumstance:flowering_madness"
    );

    private static final Set<String> AXE_TRANSMOGS = Set.of(
            "anabnormalcircumstance:gargoyle_axe",
            "anabnormalcircumstance:magma_club",
            "anabnormalcircumstance:abyssal_axe",
            "anabnormalcircumstance:mana_axe",
            "anabnormalcircumstance:fire_mace",
            "anabnormalcircumstance:dark_moon",
            "anabnormalcircumstance:soul_steel_hatchet",
            "anabnormalcircumstance:forge_hammer",
            "anabnormalcircumstance:hellspawn_axe"
    );

    private static final Set<String> BOW_TRANSMOGS = Set.of(
            "anabnormalcircumstance:arachnid_bow",
            "anabnormalcircumstance:eternal_bow",
            "anabnormalcircumstance:basalt_bow"
    );

    private static final Set<String> KARAMBIT_TRANSMOGS = Set.of(
            "anabnormalcircumstance:druid_staff",
            "anabnormalcircumstance:vulkan_blade"
    );

    public TransmogTokenItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {

        if (hand != Hand.OFF_HAND) {
            return TypedActionResult.pass(player.getStackInHand(hand));
        }

        if (world.isClient()) {
            return TypedActionResult.success(player.getStackInHand(hand));
        }

        ItemStack token = player.getOffHandStack();
        ItemStack mainhand = player.getMainHandStack();

        boolean isBlade = mainhand.isOf(ModItems.ARCANE_BLADE);
        boolean isAxe = mainhand.isOf(ModItems.ARCANE_AXE);
        boolean isBow   = mainhand.isOf(ModItems.ARCANE_BOW);
        boolean isKarambit   = mainhand.isOf(ModItems.KARAMBIT);

        // Must be holding a valid base item
        if (!isBlade && !isAxe && !isBow && !isKarambit) {
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

        // Check if the chosen transmog is allowed for the base type
        if (isBlade && !SWORD_TRANSMOGS.contains(itemId)) {
            player.sendMessage(Text.literal("That appearance cannot be applied to the Arcane Blade!")
                    .formatted(Formatting.RED), true);
            return TypedActionResult.fail(token);
        }

        if (isAxe && !AXE_TRANSMOGS.contains(itemId)) {
            player.sendMessage(Text.literal("That appearance cannot be applied to the Arcane Axe!")
                    .formatted(Formatting.RED), true);
            return TypedActionResult.fail(token);
        }

        if (isBow && !BOW_TRANSMOGS.contains(itemId)) {
            player.sendMessage(
                    Text.literal("That appearance cannot be applied to the Arcane Bow!")
                            .formatted(Formatting.RED),
                    true
            );
            return TypedActionResult.fail(token);
        }

        if (isKarambit && !KARAMBIT_TRANSMOGS.contains(itemId)) {
            player.sendMessage(
                    Text.literal("That appearance cannot be applied to the Karambit!")
                            .formatted(Formatting.RED),
                    true
            );
            return TypedActionResult.fail(token);
        }

        // Apply transmog NBT
        NbtCompound stackNbt = mainhand.getOrCreateNbt();
        stackNbt.putString(TRANSMOG_KEY, itemId);

        player.sendMessage(Text.literal("Transmog applied!").formatted(Formatting.DARK_PURPLE), true);

        world.playSound(null, player.getBlockPos(),
                SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                SoundCategory.PLAYERS, 1.0F, 1.2F);

        token.decrement(1);

        return TypedActionResult.success(token);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Used to Transmogrify Arcane Items").formatted(Formatting.DARK_PURPLE));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
