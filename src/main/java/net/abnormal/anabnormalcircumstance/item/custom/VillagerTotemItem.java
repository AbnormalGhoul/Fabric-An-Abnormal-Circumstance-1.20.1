package net.abnormal.anabnormalcircumstance.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class VillagerTotemItem extends Item {
    public VillagerTotemItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, net.minecraft.entity.player.PlayerEntity player, LivingEntity entity, Hand hand) {
        if (entity instanceof ZombieVillagerEntity zombieVillager) {
            zombieVillager.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 1));
            stack.decrement(1);
            player.playSound(SoundEvents.ENTITY_WARDEN_DEATH, 5.0F, 1.0F);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}