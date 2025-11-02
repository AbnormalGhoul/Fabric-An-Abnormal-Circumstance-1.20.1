package net.abnormal.anabnormalcircumstance.magic.spells.hydromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

/**
 * Bile Water:
 * Empowers the user's next attack to apply blindness and weakness.
 */
public class BileWaterSpell extends Spell {
    public BileWaterSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.HYDROMANCY, SpellTier.TIER_2, 40, 60, icon, "Bile Water");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        caster.getWorld().playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.PLAYERS, 3.0f, 0.7f);
        // Set a flag or component marking that next melee hit will apply effects.
        // Example (pseudo):
        // PlayerComponents.BILE_WATER_STATE.get(caster).activateForNextHit();
        return true;
    }
}
