package net.abnormal.anabnormalcircumstance.magic.spells.aeromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class GaleWindsSpell extends Spell {
    public GaleWindsSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.AEROMANCY, SpellTier.TIER_3, 20, 30, icon, "Gale Winds");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        Vec3d direction = caster.getRotationVec(1.0F);
        caster.addVelocity(direction.x * 1.5, direction.y * 0.5, direction.z * 1.5);
        caster.velocityModified = true;
        caster.getWorld().playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 2.0f, 1.3f);
        return true;
    }
}
