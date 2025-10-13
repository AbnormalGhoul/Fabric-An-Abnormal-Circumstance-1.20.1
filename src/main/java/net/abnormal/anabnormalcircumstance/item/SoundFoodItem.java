package net.abnormal.anabnormalcircumstance.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class SoundFoodItem extends Item {
    public SoundFoodItem(FoodComponent foodComponent, Settings settings) {
        super(settings.food(foodComponent));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player && world.isClient) {
            player.playSound(SoundEvents.ENTITY_SQUID_DEATH, 5.0F, 3.0F);
        }
        return super.finishUsing(stack, world, user);
    }
}
