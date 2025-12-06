package net.abnormal.anabnormalcircumstance.item;

import net.abnormal.anabnormalcircumstance.component.ModComponents;
import net.abnormal.anabnormalcircumstance.component.SpellSlotsComponent;
import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellRegistry;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.abnormal.anabnormalcircumstance.network.PacketHandler;
import net.minecraft.advancement.Advancement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class SpellScrollItem extends Item {
    public SpellScrollItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.pass(stack);

        NbtCompound tag = stack.getNbt();
        if (tag == null || !tag.contains("spell_id")) {
            user.sendMessage(Text.literal("§cThis scroll is blank."), true);
            return TypedActionResult.fail(stack);
        }

        Identifier spellId = Identifier.tryParse(tag.getString("spell_id"));
        Spell spell = SpellRegistry.get(spellId);
        if (spell == null) {
            user.sendMessage(Text.literal("§cUnknown spell."), true);
            return TypedActionResult.fail(stack);
        }

        var slotComp = ModComponents.SLOTS.getNullable(user);
        if (slotComp == null) {
            user.sendMessage(Text.literal("§cFailed to access spell slots."), true);
            return TypedActionResult.fail(stack);
        }

        var tier = spell.getTier();
        if (slotComp.getSpellForTier(tier) != null) {
            user.sendMessage(Text.literal("§eYour Tier " + tier.getLevel() + " slot is already occupied."), true);
            return TypedActionResult.fail(stack);
        }

        // Additional requirements for Tier 4 and Tier 5
        if (tier.getLevel() >= 4) {
            if (!(user instanceof ServerPlayerEntity serverPlayer)) {
                user.sendMessage(Text.literal("§cServer-side check failed."), true);
                return TypedActionResult.fail(stack);
            }

            int requiredUpTo = (tier.getLevel() == 4) ? 3 : 4; // Tier4 -> need 1..3, Tier5 -> need 1..4

            if (!hasBlessing(serverPlayer, spell.getElement())) {
                user.sendMessage(Text.literal("§cYou are not Blessed with " +
                        spell.getElement().name().toLowerCase()), true);
                return TypedActionResult.fail(stack);
            }

            if (!hasRequiredElementSlots(slotComp, spell.getElement(), requiredUpTo)) {
                user.sendMessage(Text.literal("§cYou require more devotion in the way of " +
                        spell.getElement().name().toLowerCase()), true);
                return TypedActionResult.fail(stack);
            }

        }

        // Successfully bind spell
        slotComp.setSpellForTier(tier, spellId);
        user.sendMessage(Text.literal("§aBound " + spell.getElement() + " to Tier " + tier.getLevel() + " slot."), true);

        // Play sound effect
        world.playSound(
                null,                        // null means all players nearby hear it
                user.getBlockPos(),                 // sound position
                SoundEvents.ITEM_BOOK_PAGE_TURN,    // pick any fitting sound
                SoundCategory.PLAYERS,              // player sound category
                3.0F,                               // volume
                1.0F                                // pitch
        );

        // Consume scroll only on success
        stack.decrement(1);

        if (user instanceof ServerPlayerEntity serverPlayer) {
            PacketHandler.syncSpellStateToClient(serverPlayer,
                    ModComponents.MANA.getNullable(user), slotComp);

            // Grant advancements for reaching Tier 5
            if (tier.getLevel() == 5) {
                grantAdvancement(serverPlayer, "05_pinnacle_of_magic");
            }
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasNbt() && stack.getNbt().contains("spell_id")) {
            Identifier id = Identifier.tryParse(stack.getNbt().getString("spell_id"));
            Spell spell = SpellRegistry.get(id);

            if (spell != null) {
                tooltip.add(Text.literal("§bBound Spell: §f" + spell.getDisplayName()));
                tooltip.add(Text.literal("§5" + spell.getTier().name() + " " + spell.getElement().name()));
                tooltip.add(Text.literal("§3Mana Usage: §f" + spell.getManaCost()));
                tooltip.add(Text.literal("§3Cooldown: §f" + (spell.getCooldownTicks() / 20.0) + "s"));

                if (!Screen.hasShiftDown()) {
                    tooltip.add(Text.literal("§ePress SHIFT to view description"));
                } else {
                    String desc = spell.getDescription();
                    int maxWidth = 40;
                    List<String> lines = wrapText(desc, maxWidth);
                    for (String line : lines) {
                        tooltip.add(Text.literal("§7" + line));
                    }
                }

            } else {
                tooltip.add(Text.literal("§cInvalid Spell Data"));
            }

        } else {
            tooltip.add(Text.literal("§7Blank Scroll"));
        }
    }

    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();

        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            if (current.length() + word.length() + 1 > maxWidth) {
                lines.add(current.toString());
                current = new StringBuilder(word);
            } else {
                if (!current.isEmpty()) {
                    current.append(" ");
                }
                current.append(word);
            }
        }

        if (!current.isEmpty()) {
            lines.add(current.toString());
        }

        return lines;
    }

    // Check that the player has Tier 1..requiredUpToLevel spells equipped of the same element
    private boolean hasRequiredElementSlots(SpellSlotsComponent slotComp, SpellElement element, int requiredUpToLevel) {
        for (int level = 1; level <= requiredUpToLevel; level++) {
            SpellTier tier = SpellTier.values()[level - 1];
            Identifier id = slotComp.getSpellForTier(tier);
            if (id == null) return false;
            Spell s = SpellRegistry.get(id);
            if (s == null) return false;
            if (s.getElement() != element) return false;
        }
        return true;
    }

    // Check that the player has the corresponding blessing advancement
    private boolean hasBlessing(ServerPlayerEntity player, SpellElement element) {
        MinecraftServer server = player.getServer();
        if (server == null) return false;

        String advName = switch (element) {
            case PYROMANCY -> "04_blessing_of_pyromancy";
            case HYDROMANCY -> "03_blessing_of_hydromancy";
            case GEOMANCY -> "02_blessing_of_geomancy";
            case AEROMANCY -> "01_blessing_of_aeromancy";
        };

        Identifier advId = new Identifier("anabnormalcircumstance", advName);
        Advancement adv = server.getAdvancementLoader().get(advId);
        if (adv == null) return false;
        return player.getAdvancementTracker().getProgress(adv).isDone();
    }

    private void grantAdvancement(ServerPlayerEntity player, String advId) {
        MinecraftServer server = player.getServer();
        if (server == null) return;
        Advancement adv = server.getAdvancementLoader().get(new Identifier("anabnormalcircumstance", advId));
        if (adv == null) return;
        // criterion name in the JSON is "impossible"
        player.getAdvancementTracker().grantCriterion(adv, "impossible");
    }
}