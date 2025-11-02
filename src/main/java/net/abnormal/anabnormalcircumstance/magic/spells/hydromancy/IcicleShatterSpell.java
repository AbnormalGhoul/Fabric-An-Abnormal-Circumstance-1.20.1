package net.abnormal.anabnormalcircumstance.magic.spells.hydromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

/**
 * Icicle Shatter:
 * Damages and freezes all nearby entities in a small radius.
 */
public class IcicleShatterSpell extends Spell {
    public IcicleShatterSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.HYDROMANCY, SpellTier.TIER_2, 30, 30, icon, "Icicle Shatter");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        World world = caster.getWorld();
        world.playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 3.0f, 1.0f);

        Box area = new Box(caster.getBlockPos()).expand(3.0);
        List<Entity> nearby = world.getOtherEntities(caster, area, e -> e instanceof LivingEntity);

        for (Entity e : nearby) {
            LivingEntity target = (LivingEntity) e;
            target.damage(world.getDamageSources().freeze(), 4.0f);
            target.setFrozenTicks(100); // ~5 seconds freeze
        }
        return true;
    }
}
