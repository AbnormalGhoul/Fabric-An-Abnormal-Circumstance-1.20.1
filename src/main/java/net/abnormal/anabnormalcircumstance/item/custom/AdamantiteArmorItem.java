package net.abnormal.anabnormalcircumstance.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class AdamantiteArmorItem extends ArmorItem {

    public AdamantiteArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Full Set Bonus: Every 3rd hit you gain +3 damage").formatted(Formatting.RED));
        tooltip.add(Text.literal("Full Set Bonus: Every Hostile Entity Kill the player is rejuvenated").formatted(Formatting.DARK_RED));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
