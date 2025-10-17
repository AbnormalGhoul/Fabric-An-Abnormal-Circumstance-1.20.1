package net.abnormal.anabnormalcircumstance.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class KarambitItem extends Item {
    public KarambitItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("A curved blade favored for its agility,"));
        tooltip.add(Text.literal("Grants +3 increased attack damage"));
        tooltip.add(Text.literal("While held in the offhand"));
        super.appendTooltip(stack, world, tooltip, context);
    }
}