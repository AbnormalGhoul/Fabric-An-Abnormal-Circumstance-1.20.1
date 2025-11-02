package net.abnormal.anabnormalcircumstance.magic.spells.hydromancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

/**
 * Control Weather:
 * Changes weather to thunder and grants the user a temporary lightning strike buff.
 */
public class ControlWeatherSpell extends Spell {
    public ControlWeatherSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.HYDROMANCY, SpellTier.TIER_4, 75, 1800, icon, "Control Weather");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        if (!(caster.getWorld() instanceof ServerWorld world)) return false;

        world.playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 10.0f, 0.8f);
        world.setWeather(0, 6000, true, true); // Thunder for 5 min

        // TODO: mark player with lightning buff (50% chance to summon lightning on attack)
        // Example (pseudo):
        // PlayerComponents.LIGHTNING_BUFF.get(caster).activate(30 * 20);

        return true;
    }
}
