package net.abnormal.anabnormalcircumstance.magic.spells.aeromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class HurricanesCallSpell extends Spell {
    public HurricanesCallSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.AEROMANCY, SpellTier.TIER_3, 35, 60, icon, "Hurricane’s Call");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        List<LivingEntity> entities = caster.getWorld().getEntitiesByClass(
                LivingEntity.class,
                new Box(caster.getBlockPos()).expand(8),
                e -> e != caster && e.isAlive()
        );

        for (LivingEntity entity : entities) {
            Vec3d away = entity.getPos().subtract(caster.getPos()).normalize().multiply(1.5);
            entity.addVelocity(away.x, 0.8, away.z);
            entity.velocityModified = true;
        }

        caster.getWorld().playSound(null, caster.getBlockPos(), SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.PLAYERS, 3.0f, 1.0f);
        return true;
    }
}
