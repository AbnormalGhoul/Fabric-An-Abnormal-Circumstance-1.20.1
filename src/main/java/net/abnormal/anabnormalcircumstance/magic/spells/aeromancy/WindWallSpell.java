package net.abnormal.anabnormalcircumstance.magic.spells.aeromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class WindWallSpell extends Spell {
    public WindWallSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.AEROMANCY, SpellTier.TIER_1, 25, 30, icon, "Wind Wall");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        // Placeholder effect — would require projectile-blocking logic elsewhere
        caster.getWorld().playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.PLAYERS, 2.0f, 1.0f);
        return true;
    }
}
