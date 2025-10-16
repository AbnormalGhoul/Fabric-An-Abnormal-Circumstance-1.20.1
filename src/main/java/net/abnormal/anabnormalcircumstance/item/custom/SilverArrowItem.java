package net.abnormal.anabnormalcircumstance.item.custom;


import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.abnormal.anabnormalcircumstance.entity.ModEntities;
import net.abnormal.anabnormalcircumstance.entity.custom.SilverArrowEntity;

public class SilverArrowItem extends ArrowItem {
    public SilverArrowItem(Settings settings) {
        super(settings);
    }

    @Override
    public PersistentProjectileEntity createArrow(World world, ItemStack stack, net.minecraft.entity.LivingEntity shooter) {
        SilverArrowEntity arrow = new SilverArrowEntity(ModEntities.SILVER_ARROW, world, shooter);
        return arrow;
    }
}