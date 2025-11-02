package net.abnormal.anabnormalcircumstance.magic.spells.geomancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class EarthquakeSpell extends Spell {
    public EarthquakeSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.GEOMANCY, SpellTier.TIER_3, 50, 120, icon, "Earthquake");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        World world = caster.getWorld();
        List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, caster.getBoundingBox().expand(15), e -> e.isOnGround() && e != caster);

        for (LivingEntity target : targets) {
            target.damage(world.getDamageSources().magic(), 5.0f);
        }

        world.playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_STONE_BREAK, SoundCategory.PLAYERS, 4.0f, 0.8f);
        return true;
    }
}
