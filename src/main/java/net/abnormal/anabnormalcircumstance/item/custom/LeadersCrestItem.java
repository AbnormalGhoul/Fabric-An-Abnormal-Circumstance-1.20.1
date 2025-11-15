package net.abnormal.anabnormalcircumstance.item.custom;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class LeadersCrestItem extends Item implements Trinket {
    public LeadersCrestItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        Trinket.super.onEquip(stack, slot, entity);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, net.minecraft.client.item.TooltipContext context) {
        tooltip.add(Text.literal("Mark of a Leader").formatted(Formatting.GRAY));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
