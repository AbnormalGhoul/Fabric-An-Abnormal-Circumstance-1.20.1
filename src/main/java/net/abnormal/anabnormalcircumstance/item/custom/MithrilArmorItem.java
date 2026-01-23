package net.abnormal.anabnormalcircumstance.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class MithrilArmorItem extends ArmorItem {

    public MithrilArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("Full Set Bonus: Decreased Spell Cooldown by flat 10 seconds").formatted(Formatting.AQUA));
        tooltip.add(Text.literal("Full Set Bonus: Increased Mana Regen by 1.5x").formatted(Formatting.DARK_AQUA));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
