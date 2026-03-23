package net.abnormal.anabnormalcircumstance.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class CobaltArmorItem extends ArmorItem {

    public CobaltArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Full Set Bonus: 20% attack speed boost").formatted(Formatting.BLUE));
        tooltip.add(Text.literal("Full Set Bonus: 10% speed boost").formatted(Formatting.DARK_BLUE));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
