package net.abnormal.anabnormalcircumstance;

import net.abnormal.anabnormalcircumstance.block.ModBlocks;
import net.abnormal.anabnormalcircumstance.block.entity.ModBlockEntities;
import net.abnormal.anabnormalcircumstance.magic.SpellRegistry;
import net.abnormal.anabnormalcircumstance.magic.spells.aeromancy.*;
import net.abnormal.anabnormalcircumstance.magic.spells.geomancy.*;
import net.abnormal.anabnormalcircumstance.magic.spells.hydromancy.*;
import net.abnormal.anabnormalcircumstance.magic.spells.pyromancy.*;
import net.abnormal.anabnormalcircumstance.network.PacketHandler;
import net.abnormal.anabnormalcircumstance.recipe.ModBrewingRecipes;
import net.abnormal.anabnormalcircumstance.effect.ModEffects;
import net.abnormal.anabnormalcircumstance.entity.ModEntities;
import net.abnormal.anabnormalcircumstance.event.ModAdvancementHandler;
import net.abnormal.anabnormalcircumstance.event.ModEvents;
import net.abnormal.anabnormalcircumstance.event.StunEventHandler;
import net.abnormal.anabnormalcircumstance.item.ModItemGroups;
import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.abnormal.anabnormalcircumstance.item.interfaces.UniqueAbilityItem;
import net.abnormal.anabnormalcircumstance.recipe.ModRecipes;
import net.abnormal.anabnormalcircumstance.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnAbnormalCircumstance implements ModInitializer {
	public static final String MOD_ID = "anabnormalcircumstance";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

        PacketHandler.register();
        registerSpells();

        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
        ModEffects.registerEffects();
        ModItemGroups.registerItemGroups();

        ModEvents.registerEvents();
        ModEntities.registerModEntities();
        ModBlockEntities.registerBlockEntities();
        ModScreenHandlers.registerScreenHandlers();

        StunEventHandler.register();
        ModAdvancementHandler.register();
        ModBrewingRecipes.registerAll();
        ModRecipes.registerRecipes();

        // Register unique ability packet
        ServerPlayNetworking.registerGlobalReceiver(
                new Identifier(MOD_ID, "unique_item_ability"),
                (server, player, handler, buf, responseSender) -> {
                    server.execute(() -> {
                        // Handle activation for any item implementing the interface
                        if (player.getMainHandStack().getItem() instanceof UniqueAbilityItem unique)
                            unique.useUniqueAbility(player);
                        else if (player.getOffHandStack().getItem() instanceof UniqueAbilityItem unique)
                            unique.useUniqueAbility(player);
                    });
                }
        );

        LOGGER.info("An Abnormal Circumstance Mod Initialized");

    }

    private void registerSpells() {

        // Hydromancy Spells
        SpellRegistry.register(new WaterVeilSpell(
                new Identifier(MOD_ID, "hydro_water_veil"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/hydro_icon.png")
        ));
        SpellRegistry.register(new HealingFluidsSpell(
                new Identifier(MOD_ID, "hydro_healing_fluids"),
                new Identifier(MOD_ID, "textures/gui/spells/hydro_icon.png")
        ));
        SpellRegistry.register(new DiversGraceSpell(
                new Identifier(MOD_ID, "hydro_divers_grace"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/hydro_icon.png")
        ));
        SpellRegistry.register(new ControlWeatherSpell(
                new Identifier(MOD_ID, "hydro_control_weather"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/hydro_icon.png")
        ));
        SpellRegistry.register(new CleansingFluidsSpell(
                new Identifier(MOD_ID, "hydro_cleansing_fluids"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/hydro_icon.png")
        ));
        SpellRegistry.register(new BileWaterSpell(
                new Identifier(MOD_ID, "hydro_bile_water"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/hydro_icon.png")
        ));
        SpellRegistry.register(new IcicleShatterSpell(
                new Identifier(MOD_ID, "hydro_icicle_shatter"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/hydro_icon.png")
        ));

        // Pyromancy Spells
        SpellRegistry.register(new FireAspectSpell(
                new Identifier(MOD_ID, "pyro_fire_aspect"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/pyro_icon.png")
        ));
        SpellRegistry.register(new CinderCloakingSpell(
                new Identifier(MOD_ID, "pyro_cinder_cloaking"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/pyro_icon.png")
        ));
        SpellRegistry.register(new FireballSpell(
                new Identifier(MOD_ID, "pyro_fireball"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/pyro_icon.png")
        ));
        SpellRegistry.register(new FlameChargeSpell(
                new Identifier(MOD_ID, "pyro_flame_charge"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/pyro_icon.png")
        ));
        SpellRegistry.register(new RingOfFireSpell(
                new Identifier(MOD_ID, "pyro_ring_of_fire"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/pyro_icon.png")
        ));
        SpellRegistry.register(new BloodPactSpell(
                new Identifier(MOD_ID, "pyro_blood_pact"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/pyro_icon.png")
        ));
        SpellRegistry.register(new MoltenFurrySpell(
                new Identifier(MOD_ID, "pyro_molten_furry"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/pyro_icon.png")
        ));

        // Geomancy Spells
        SpellRegistry.register(new RockBlastSpell(
                new Identifier(MOD_ID, "geo_rock_blast"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/geo_icon.png")
        ));
        SpellRegistry.register(new QuickClawsSpell(
                new Identifier(MOD_ID, "geo_quick_claws"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/geo_icon.png")
        ));
        SpellRegistry.register(new GrowthSpell(
                new Identifier(MOD_ID, "geo_growth"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/geo_icon.png")
        ));
        SpellRegistry.register(new RockPolishSpell(
                new Identifier(MOD_ID, "geo_rock_polish"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/geo_icon.png")
        ));
        SpellRegistry.register(new StompSpell(
                new Identifier(MOD_ID, "geo_stomp"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/geo_icon.png")
        ));
        SpellRegistry.register(new SeismicCrownSpell(
                new Identifier(MOD_ID, "geo_seismic_crown"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/geo_icon.png")
        ));
        SpellRegistry.register(new EarthquakeSpell(
                new Identifier(MOD_ID, "geo_earthquake"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/geo_icon.png")
        ));
        SpellRegistry.register(new ImmovableObjectSpell(
                new Identifier(MOD_ID, "geo_immovable_object"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/geo_icon.png")
        ));

        // Aeromancy Spells
        SpellRegistry.register(new LightAsAFeatherSpell(
                new Identifier(MOD_ID, "aero_light_as_a_feather"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/aero_icon.png")
        ));
        SpellRegistry.register(new WindWallSpell(
                new Identifier(MOD_ID, "aero_wind_wall"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/aero_icon.png")
        ));
        SpellRegistry.register(new SilentStepSpell(
                new Identifier(MOD_ID, "aero_silent_step"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/aero_icon.png")
        ));
        SpellRegistry.register(new UpdraftsSpell(
                new Identifier(MOD_ID, "aero_updrafts"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/aero_icon.png")
        ));
        SpellRegistry.register(new GaleWindsSpell(
                new Identifier(MOD_ID, "aero_gale_winds"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/aero_icon.png")
        ));
        SpellRegistry.register(new HurricanesCallSpell(
                new Identifier(MOD_ID, "aero_hurricanes_call"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/aero_icon.png")
        ));
        SpellRegistry.register(new StormlordsWillSpell(
                new Identifier(MOD_ID, "aero_stormlords_will"),
                new Identifier(MOD_ID, "textures/gui/spells/icons/aero_icon.png")
        ));

    }

}