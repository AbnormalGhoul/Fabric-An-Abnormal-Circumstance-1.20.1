package net.abnormal.anabnormalcircumstance.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.List;

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

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.literal("The totem exudes a palpable aura of sanctity,"));
        tooltip.add(Text.literal("as if it has been imbued with the purest essence of life itself."));
        tooltip.add(Text.literal("Can be used to weaken Zombie Villagers"));
        super.appendTooltip(stack, world, tooltip, context);
    }
}