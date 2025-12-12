package net.abnormal.anabnormalcircumstance.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AbnormalRelicItem extends Item {
    public AbnormalRelicItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("A sacred relic of a great tragedy").formatted(Formatting.GOLD));
        tooltip.add(Text.literal("It seems to have a deep connection to the nether").formatted(Formatting.RED));
        super.appendTooltip(stack, world, tooltip, context);
    }
}

