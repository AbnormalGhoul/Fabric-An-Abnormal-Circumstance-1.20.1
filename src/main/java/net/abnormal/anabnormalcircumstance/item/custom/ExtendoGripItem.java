package net.abnormal.anabnormalcircumstance.item.custom;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;

import java.util.List;
import java.util.UUID;

public class ExtendoGripItem extends Item {
    private static final UUID REACH_MODIFIER_ID = UUID.fromString("a12e6e48-42e8-4c9b-87c9-f882f4a18b01");
    private static final UUID ATTACK_RANGE_MODIFIER_ID = UUID.fromString("b34e8b19-72c2-4b3a-81c0-baa6dfb50f02");

    private static final EntityAttributeModifier REACH_MODIFIER =
            new EntityAttributeModifier(REACH_MODIFIER_ID, "Extendo Grip reach bonus", 2.0, EntityAttributeModifier.Operation.ADDITION);
    private static final EntityAttributeModifier ATTACK_RANGE_MODIFIER =
            new EntityAttributeModifier(ATTACK_RANGE_MODIFIER_ID, "Extendo Grip attack range bonus", 0.5, EntityAttributeModifier.Operation.ADDITION);

    public ExtendoGripItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (world.isClient() || !(entity instanceof PlayerEntity player)) return;

        boolean holding = selected || player.getOffHandStack() == stack;

        EntityAttributeInstance reachAttr = player.getAttributeInstance(ReachEntityAttributes.REACH);
        EntityAttributeInstance attackAttr = player.getAttributeInstance(ReachEntityAttributes.ATTACK_RANGE);

        if (reachAttr == null || attackAttr == null) return;

        if (holding) {
            // Apply modifiers while held
            if (!reachAttr.hasModifier(REACH_MODIFIER)) {
                reachAttr.addPersistentModifier(REACH_MODIFIER);
            }
            if (!attackAttr.hasModifier(ATTACK_RANGE_MODIFIER)) {
                attackAttr.addPersistentModifier(ATTACK_RANGE_MODIFIER);
            }
        } else {
            // Remove modifiers when not held
            if (reachAttr.hasModifier(REACH_MODIFIER)) {
                reachAttr.removeModifier(REACH_MODIFIER_ID);
            }
            if (attackAttr.hasModifier(ATTACK_RANGE_MODIFIER)) {
                attackAttr.removeModifier(ATTACK_RANGE_MODIFIER_ID);
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, net.minecraft.client.item.TooltipContext context) {
        tooltip.add(Text.literal("Extends attack and placement reach").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("+0.5 Attack Range, +2 Block Reach").formatted(Formatting.GRAY));
    }
}
