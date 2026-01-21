package net.abnormal.anabnormalcircumstance.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class MithrilIngotItem extends Item {
    public MithrilIngotItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Full Set Bonus: -10s Spell Cooldown & x1.5 Mana Regen").formatted(Formatting.AQUA));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
