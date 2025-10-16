package net.abnormal.anabnormalcircumstance.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.abnormal.anabnormalcircumstance.item.ModItems;

public class SilverArrowEntity extends PersistentProjectileEntity {
    public SilverArrowEntity(EntityType<? extends PersistentProjectileEntity> type, World world) {
        super(type, world);
    }

    public SilverArrowEntity(EntityType<? extends PersistentProjectileEntity> type, World world, LivingEntity owner) {
        super(type, owner, world);
    }

    @Override
    protected void onHit(LivingEntity target) {
        float damage = (float) this.getDamage();
        if (target.getGroup() == net.minecraft.entity.EntityGroup.UNDEAD) {
            damage *= 4.0f / 3.0f;
        }
        target.damage(this.getWorld().getDamageSources().arrow(this, this.getOwner()), damage);
        super.onHit(target);
    }

    @Override
    protected ItemStack asItemStack() {
        return new ItemStack(ModItems.SILVER_ARROW);
    }
}