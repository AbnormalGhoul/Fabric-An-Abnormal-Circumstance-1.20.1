package net.abnormal.anabnormalcircumstance.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffect;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SparkItem extends Item {
    private final StatusEffect effect;
    private final int duration;
    private final int amplifier;

    public SparkItem(Settings settings, StatusEffect effect, int duration, int amplifier) {
        super(settings);
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (user.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.fail(stack);
        }

        if (!world.isClient) {

            // Set a 10-tick cooldown
            user.getItemCooldownManager().set(this, 10);

            // Apply the effect (reduce duration by 33%)
            int reducedDuration = (int) (duration * 0.6667);
            user.addStatusEffect(new StatusEffectInstance(effect, reducedDuration, amplifier));

            // Play potion splash sound
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);

            // Consume one item
            if (!user.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 1; // Instant use
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        String effectName = Text.translatable(effect.getTranslationKey()).getString();
        int seconds = (int) ((duration * 0.6667) / 20.0);

        // Convert amplifier (0-based) to level (1-based)
        int level = amplifier + 1;

        // Add Roman numeral for level
        String levelText = switch (level) {
            case 2 -> " II";
            case 3 -> " III";
            case 4 -> " IV";
            case 5 -> " V";
            case 6 -> " VI";
            case 7 -> " VII";
            case 8 -> " VIII";
            case 9 -> " IX";
            case 10 -> " X";
            default -> (level > 1 ? " " + level : "");
        };

        tooltip.add(Text.literal(effectName + levelText + " (" + seconds + "s)")
                .formatted(net.minecraft.util.Formatting.BLUE));
    }
}
