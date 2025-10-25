package net.abnormal.anabnormalcircumstance.item;

import net.abnormal.anabnormalcircumstance.component.ModComponents;
import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellRegistry;
import net.abnormal.anabnormalcircumstance.network.PacketHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

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

        // Successfully bind spell
        slotComp.setSpellForTier(tier, spellId);
        user.sendMessage(Text.literal("§aBound " + spell.getElement() + " to Tier " + tier.getLevel() + " slot."), true);

        // Consume scroll only on success
        stack.decrement(1);

        if (user instanceof ServerPlayerEntity serverPlayer) {
            PacketHandler.syncSpellStateToClient(serverPlayer,
                    ModComponents.MANA.getNullable(user), slotComp);
        }

        return TypedActionResult.success(stack, world.isClient());
    }
}
