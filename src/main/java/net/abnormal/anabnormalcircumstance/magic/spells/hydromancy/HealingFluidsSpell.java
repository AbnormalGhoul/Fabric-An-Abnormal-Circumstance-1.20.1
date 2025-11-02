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
 * Healing Fluids:
 * Heals the caster and nearby allies within 5 blocks by 8 hearts.
 */
public class HealingFluidsSpell extends Spell {
    public HealingFluidsSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.HYDROMANCY, SpellTier.TIER_3, 40, 45, icon, "Healing Fluids");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        World world = caster.getWorld();
        world.playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundCategory.PLAYERS, 2.0f, 1.2f);

        Box area = new Box(caster.getBlockPos()).expand(5.0);
        List<Entity> nearby = world.getOtherEntities(caster, area, e -> e instanceof LivingEntity);

        for (Entity e : nearby) {
            LivingEntity target = (LivingEntity) e;
            target.heal(16.0f); // 8 hearts = 16 HP
        }

        caster.heal(16.0f);
        return true;
    }
}
