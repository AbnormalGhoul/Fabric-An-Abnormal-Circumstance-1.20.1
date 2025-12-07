package net.abnormal.anabnormalcircumstance.magic;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.magic.spells.aeromancy.*;
import net.abnormal.anabnormalcircumstance.magic.spells.geomancy.*;
import net.abnormal.anabnormalcircumstance.magic.spells.hydromancy.*;
import net.abnormal.anabnormalcircumstance.magic.spells.pyromancy.*;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ModSpells {
    private static boolean registered = false;

    public static void registerAll() {
        if (registered) return; // prevents double registration
        registered = true;

        // Hydromancy
        SpellRegistry.register(new WaterVeilSpell(id("hydro_water_veil"), icon("hydro_icon")));
        SpellRegistry.register(new HealingFluidsSpell(id("hydro_healing_fluids"), icon("hydro_icon")));
        SpellRegistry.register(new DiversGraceSpell(id("hydro_divers_grace"), icon("hydro_icon")));
        SpellRegistry.register(new ControlWeatherSpell(id("hydro_control_weather"), icon("hydro_icon")));
        SpellRegistry.register(new CleansingFluidsSpell(id("hydro_cleansing_fluids"), icon("hydro_icon")));
        SpellRegistry.register(new BileWaterSpell(id("hydro_bile_water"), icon("hydro_icon")));
        SpellRegistry.register(new IcicleShatterSpell(id("hydro_icicle_shatter"), icon("hydro_icon")));

        // Pyromancy
        SpellRegistry.register(new FireAspectSpell(id("pyro_fire_aspect"), icon("pyro_icon")));
        SpellRegistry.register(new CinderCloakingSpell(id("pyro_cinder_cloaking"), icon("pyro_icon")));
        SpellRegistry.register(new FireballSpell(id("pyro_fireball"), icon("pyro_icon")));
        SpellRegistry.register(new FlameChargeSpell(id("pyro_flame_charge"), icon("pyro_icon")));
        SpellRegistry.register(new PyroclasmWaveSpell(id("pyro_pyroclasm_wave"), icon("pyro_icon")));
        SpellRegistry.register(new BloodPactSpell(id("pyro_blood_pact"), icon("pyro_icon")));
        SpellRegistry.register(new PhoenixRebirthSpell(id("pyro_phoenix_rebirth"), icon("pyro_icon")));
        SpellRegistry.register(new MoltenFurySpell(id("pyro_molten_fury"), icon("pyro_icon")));

        // Geomancy
        SpellRegistry.register(new RockBlastSpell(id("geo_rock_blast"), icon("geo_icon")));
        SpellRegistry.register(new QuickClawsSpell(id("geo_quick_claws"), icon("geo_icon")));
        SpellRegistry.register(new GrowthSpell(id("geo_growth"), icon("geo_icon")));
        SpellRegistry.register(new RockPolishSpell(id("geo_rock_polish"), icon("geo_icon")));
        SpellRegistry.register(new StompSpell(id("geo_stomp"), icon("geo_icon")));
        SpellRegistry.register(new SeismicCrownSpell(id("geo_seismic_crown"), icon("geo_icon")));
        SpellRegistry.register(new EarthquakeSpell(id("geo_earthquake"), icon("geo_icon")));
        SpellRegistry.register(new ImmovableObjectSpell(id("geo_immovable_object"), icon("geo_icon")));

        // Aeromancy
        SpellRegistry.register(new LightAsAFeatherSpell(id("aero_light_as_a_feather"), icon("aero_icon")));
        SpellRegistry.register(new SoaringStrideSpell(id("aero_soaring_stride"), icon("aero_icon")));
        SpellRegistry.register(new SilentStepSpell(id("aero_silent_step"), icon("aero_icon")));
        SpellRegistry.register(new UpdraftsSpell(id("aero_updrafts"), icon("aero_icon")));
        SpellRegistry.register(new GaleWindsSpell(id("aero_gale_winds"), icon("aero_icon")));
        SpellRegistry.register(new HurricanesCallSpell(id("aero_hurricanes_call"), icon("aero_icon")));
        SpellRegistry.register(new StormlordsWillSpell(id("aero_stormlords_will"), icon("aero_icon")));
    }

    private static Identifier id(String path) {
        return new Identifier(AnAbnormalCircumstance.MOD_ID, path);
    }

    private static Identifier icon(String name) {
        return new Identifier(AnAbnormalCircumstance.MOD_ID, "textures/gui/spells/icons/" + name + ".png");
    }
}
