package net.abnormal.anabnormalcircumstance.item.custom;


import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.abnormal.anabnormalcircumstance.entity.ModEntities;
import net.abnormal.anabnormalcircumstance.entity.custom.SilverArrowEntity;

import java.util.List;

public class SilverArrowItem extends ArrowItem {
    public SilverArrowItem(Settings settings) {
        super(settings);
    }

    @Override
    public PersistentProjectileEntity createArrow(World world, ItemStack stack, net.minecraft.entity.LivingEntity shooter) {
        SilverArrowEntity arrow = new SilverArrowEntity(ModEntities.SILVER_ARROW, world, shooter);
        return arrow;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("A finely crafted arrow with a gleaming silver head").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Imbued with purity that pierces the foul essence of the undead").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Deals 33% more damage to Undead creatures").formatted(Formatting.GRAY));

        super.appendTooltip(stack, world, tooltip, context);
    }
}