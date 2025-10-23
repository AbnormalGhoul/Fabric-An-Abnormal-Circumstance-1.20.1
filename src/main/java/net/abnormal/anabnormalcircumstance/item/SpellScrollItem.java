package net.abnormal.anabnormalcircumstance.item;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.component.ModComponents;
import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Identifier;

/**
 * Right-click to bind the spell contained in the scroll's NBT to the player's slot.
 * Scroll NBT format:
 *   - "spell_id" : string identifier like "anabnormalcircumstance:hydro_water_veil"
 */
public class SpellScrollItem extends Item {
    public SpellScrollItem(Settings settings) { super(settings); }

    @Override
    public TypedActionResult<ItemStack> use(net.minecraft.world.World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.pass(stack);
        NbtCompound tag = stack.getNbt();
        if (tag == null || !tag.contains("spell_id")) {
            if (user instanceof ServerPlayerEntity) user.sendMessage(Text.literal("This scroll is blank."), true);
            return TypedActionResult.fail(stack);
        }
        Identifier spellId = Identifier.tryParse(tag.getString("spell_id"));
        Spell spell = SpellRegistry.get(spellId);
        if (spell == null) {
            if (user instanceof ServerPlayerEntity) user.sendMessage(Text.literal("Unknown spell."), true);
            return TypedActionResult.fail(stack);
        }

        var slotComp = ModComponents.SLOTS.get(user);
        if (slotComp == null) return TypedActionResult.fail(stack);
        var tier = spell.getTier();
        if (slotComp.getSpellForTier(tier) != null) {
            if (user instanceof ServerPlayerEntity) user.sendMessage(Text.literal("Your Tier " + tier.getLevel() + " slot is already occupied."), true);
            return TypedActionResult.fail(stack);
        }
        slotComp.setSpellForTier(tier, spellId);
        if (user instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity)user).sendMessage(Text.literal("Bound " + spellId.getPath() + " to slot T" + tier.getLevel()), true);
            // Immediately sync changed state to client
            net.abnormal.anabnormalcircumstance.network.PacketHandler.syncSpellStateToClient((ServerPlayerEntity) user, ModComponents.MANA.get(user), slotComp);
        }
        return TypedActionResult.success(stack);
    }
}
