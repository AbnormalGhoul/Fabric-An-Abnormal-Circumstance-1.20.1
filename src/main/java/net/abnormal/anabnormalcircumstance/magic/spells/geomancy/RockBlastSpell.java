package net.abnormal.anabnormalcircumstance.magic.spells.geomancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RockBlastSpell extends Spell {
    public RockBlastSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.GEOMANCY, SpellTier.TIER_1, 30, 45, icon, "Rock Blast");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        World world = caster.getWorld();
        Vec3d look = caster.getRotationVec(1.0F).normalize();

        for (int i = 0; i < 3; i++) {
            SmallFireballEntity rock = new SmallFireballEntity(world, caster, look.x, look.y, look.z);
            rock.setPosition(caster.getX(), caster.getEyeY() - 0.1, caster.getZ());
            world.spawnEntity(rock);
        }

        world.playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_STONE_BREAK, SoundCategory.PLAYERS, 2.0f, 1.0f);
        return true;
    }
}
