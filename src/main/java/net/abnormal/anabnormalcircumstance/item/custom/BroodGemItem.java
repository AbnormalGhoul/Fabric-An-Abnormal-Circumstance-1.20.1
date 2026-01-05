package net.abnormal.anabnormalcircumstance.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class BroodGemItem extends Item implements Trinket {

    public BroodGemItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(
            ItemStack stack,
            SlotReference slot,
            LivingEntity entity,
            UUID uuid
    ) {
        return ImmutableMultimap.of(
                EntityAttributes.GENERIC_MAX_HEALTH,
                new EntityAttributeModifier(
                        uuid,
                        "Brood Gem health bonus",
                        4.0,
                        EntityAttributeModifier.Operation.ADDITION
                ),
                EntityAttributes.GENERIC_ARMOR,
                new EntityAttributeModifier(
                        UUID.nameUUIDFromBytes((uuid.toString() + ":armor").getBytes()),
                        "Brood Gem armor bonus",
                        2.0,
                        EntityAttributeModifier.Operation.ADDITION
                )
        );
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (!entity.getWorld().isClient && entity instanceof net.minecraft.server.network.ServerPlayerEntity player) {
            // Delay by 1 tick so Trinkets finishes applying attributes
            player.getServer().execute(() -> {
                player.setHealth(player.getMaxHealth());
            });
        }
    }


    @Override
    public void appendTooltip(
            ItemStack stack,
            World world,
            List<Text> tooltip,
            net.minecraft.client.item.TooltipContext context
    ) {
        tooltip.add(Text.literal("When equipped:").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("+2 Hearts").formatted(Formatting.BLUE));
        tooltip.add(Text.literal("+2 Armor").formatted(Formatting.BLUE));
    }
}
