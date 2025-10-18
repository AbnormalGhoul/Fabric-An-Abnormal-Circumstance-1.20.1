package net.abnormal.anabnormalcircumstance.item.custom;

import net.abnormal.anabnormalcircumstance.util.UniqueBladeCooldownManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class UniqueBladeItem extends SwordItem {
    protected final long abilityCooldownMillis;

    public UniqueBladeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings, long abilityCooldownMillis) {
        super(material, attackDamage, attackSpeed, settings);
        this.abilityCooldownMillis = abilityCooldownMillis;
    }

    // Passive effect hook
    public void onPassiveTick(PlayerEntity player, ItemStack stack) {}

    // Called when keybind is pressed
    public void tryActivateAbility(PlayerEntity player, ItemStack stack) {
        if (!UniqueBladeCooldownManager.isOnCooldown(player)) {
            activateAbility(player, stack);
            UniqueBladeCooldownManager.setCooldown(player, abilityCooldownMillis);
        }
    }

    // Active ability implementation
    protected abstract void activateAbility(PlayerEntity player, ItemStack stack);

    // On hit effect
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            onHitEffect(player, target, stack);
        }
        return super.postHit(stack, target, attacker);
    }

    protected void onHitEffect(PlayerEntity player, LivingEntity target, ItemStack stack) {}
}