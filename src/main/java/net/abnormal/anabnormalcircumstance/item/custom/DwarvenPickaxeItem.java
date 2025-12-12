package net.abnormal.anabnormalcircumstance.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class DwarvenPickaxeItem extends PickaxeItem {
    public DwarvenPickaxeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            // Check if held in main hand
            if (player.getMainHandStack() == stack) {
                // Give Haste II for 2 seconds
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 45, 1, true, false, true));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 45, 1, true, false, true));
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Forged in the deep caverns of the Antrum").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("It hums with ancient craftsmanship and unmatched precision").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("Grants Haste II & Slowness II while held").formatted(Formatting.AQUA));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
