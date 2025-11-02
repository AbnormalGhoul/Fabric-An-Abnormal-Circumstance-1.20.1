package net.abnormal.anabnormalcircumstance.magic.spells.pyromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FlameChargeSpell extends Spell {
    public FlameChargeSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.PYROMANCY, SpellTier.TIER_2, 35, 45, icon, "Flame Charge");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        World world = caster.getWorld();
        Vec3d direction = caster.getRotationVec(1.0F).normalize();
        caster.addVelocity(direction.x * 2.5, direction.y * 0.5, direction.z * 2.5);
        caster.velocityModified = true;

        world.playSound(null, caster.getBlockPos(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 3.0f, 1.0f);
        // Damage / ignite effect would be handled in a collision check listener or tick callback
        return true;
    }
}
