package net.abnormal.anabnormalcircumstance.magic.spells.pyromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireballSpell extends Spell {
    public FireballSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.PYROMANCY, SpellTier.TIER_2, 35, 60, icon, "Fireball");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        World world = caster.getWorld();
        Vec3d look = caster.getRotationVec(1.0F);
        FireballEntity fireball = new FireballEntity(world, caster, look.x, look.y, look.z, 5);
        fireball.setPosition(caster.getX(), caster.getEyeY(), caster.getZ());
        world.spawnEntity(fireball);
        world.playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 2.0f, 1.0f);
        return true;
    }
}
