package net.abnormal.anabnormalcircumstance.item;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup ABNORMAL_TRINKETS = Registry.register(Registries.ITEM_GROUP,
            new Identifier(AnAbnormalCircumstance.MOD_ID, "abnormal_trinkets_group"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.abnormal_trinkets_group"))
                    .icon(() -> new ItemStack(ModItems.FORGEFATHER_JUDGMENT)).entries((displayContext, entries) -> {

                        // Champion Weapons
                        entries.add(ModItems.FIRST_LEAF);
                        entries.add(ModItems.FORGEFATHER_JUDGMENT);
                        entries.add(ModItems.WILL_BREAKER);
                        entries.add(ModItems.REEDTHORN);
                        entries.add(ModItems.WHITE_SOLIN_BLADE);
                        entries.add(ModItems.BLACK_SOLIN_BLADE);
                        entries.add(ModItems.ICICLE_SHARD);

                        // Elemental Blades
                        entries.add(ModItems.AERO_BLADE);
                        entries.add(ModItems.HYDRO_BLADE);
                        entries.add(ModItems.PYRO_BLADE);
                        entries.add(ModItems.GEO_BLADE);

                        // Other Tools
                        entries.add(ModItems.KARAMBIT);
                        entries.add(ModItems.SILVER_ARROW);
                        entries.add(ModItems.TOTEM_OF_PURITY);

                        // Trinkets
                        entries.add(ModItems.WIDOWS_GEM);
                        entries.add(ModItems.CHAMPIONS_CREST);
                        entries.add(ModItems.LEADERS_CREST);

                        // Runes
                        entries.add(ModItems.CLAIM_RUNE);
                        entries.add(ModItems.ORIGIN_RUNE);
                        entries.add(ModItems.SPELL_RUNE);
                        entries.add(ModItems.NATION_RUNE);

                        // Carcan Item
                        entries.add(ModItems.CRAB_CLAW);

                    }).build());


    public static final ItemGroup ABNORMAL_SPELL_SCROLLS = Registry.register(Registries.ITEM_GROUP,
            new Identifier(AnAbnormalCircumstance.MOD_ID, "abnormal_spell_scrolls_group"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.abnormal_spell_scrolls_group"))
                    .icon(() -> new ItemStack(ModItems.SPELL_SCROLL)).entries((displayContext, entries) -> {

                        entries.add(ModItems.SPELL_SCROLL);


                        // Hydromancy Spells
                        ItemStack hydroScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        hydroScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:hydro_water_veil");
                        entries.add(hydroScroll);

                        ItemStack diversGraceScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        diversGraceScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:hydro_divers_grace");
                        entries.add(diversGraceScroll);

                        ItemStack bileWaterScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        bileWaterScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:hydro_bile_water");
                        entries.add(bileWaterScroll);

                        ItemStack icicleShatterScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        icicleShatterScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:hydro_icicle_shatter");
                        entries.add(icicleShatterScroll);

                        ItemStack cleansingFluidsScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        cleansingFluidsScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:hydro_cleansing_fluids");
                        entries.add(cleansingFluidsScroll);

                        ItemStack healingFluidsScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        healingFluidsScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:hydro_healing_fluids");
                        entries.add(healingFluidsScroll);

                        ItemStack controlWeatherScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        controlWeatherScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:hydro_control_weather");
                        entries.add(controlWeatherScroll);


                        // Pyromancy Spells
                        ItemStack fireAspectScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        fireAspectScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:pyro_fire_aspect");
                        entries.add(fireAspectScroll);

                        ItemStack cinderCloakingScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        cinderCloakingScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:pyro_cinder_cloaking");
                        entries.add(cinderCloakingScroll);

                        ItemStack bloodPactScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        bloodPactScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:pyro_blood_pact");
                        entries.add(bloodPactScroll);

                        ItemStack flameChargeScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        flameChargeScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:pyro_flame_charge");
                        entries.add(flameChargeScroll);

                        ItemStack ringOfFireScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        ringOfFireScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:pyro_pyroclasm_wave");
                        entries.add(ringOfFireScroll);

                        ItemStack fireballScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        fireballScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:pyro_fireball");
                        entries.add(fireballScroll);

                        ItemStack phoenixRebirthScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        phoenixRebirthScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:pyro_phoenix_rebirth");
                        entries.add(phoenixRebirthScroll);

                        ItemStack moltenFurryScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        moltenFurryScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:pyro_molten_fury");
                        entries.add(moltenFurryScroll);


                        // Geomancy Spells
                        ItemStack rockBlastScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        rockBlastScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:geo_rock_blast");
                        entries.add(rockBlastScroll);

                        ItemStack quickClawsScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        quickClawsScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:geo_quick_claws");
                        entries.add(quickClawsScroll);

                        ItemStack GrowthScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        GrowthScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:geo_growth");
                        entries.add(GrowthScroll);

                        ItemStack rockPolishScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        rockPolishScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:geo_rock_polish");
                        entries.add(rockPolishScroll);

                        ItemStack stompScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        stompScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:geo_stomp");
                        entries.add(stompScroll);

                        ItemStack seismicCrownScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        seismicCrownScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:geo_seismic_crown");
                        entries.add(seismicCrownScroll);

                        ItemStack earthquakeScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        earthquakeScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:geo_earthquake");
                        entries.add(earthquakeScroll);

                        ItemStack immovableObjectScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        immovableObjectScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:geo_immovable_object");
                        entries.add(immovableObjectScroll);


                        // Aeromancy Spells
                        ItemStack lightAsAFeatherScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        lightAsAFeatherScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:aero_light_as_a_feather");
                        entries.add(lightAsAFeatherScroll);

                        ItemStack updraftsScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        updraftsScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:aero_updrafts");
                        entries.add(updraftsScroll);

                        ItemStack soaringStrideScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        soaringStrideScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:aero_soaring_stride");
                        entries.add(soaringStrideScroll);

                        ItemStack silentStepScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        silentStepScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:aero_silent_step");
                        entries.add(silentStepScroll);

                        ItemStack galeWindsScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        galeWindsScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:aero_gale_winds");
                        entries.add(galeWindsScroll);

                        ItemStack hurricanesCallScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        hurricanesCallScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:aero_hurricanes_call");
                        entries.add(hurricanesCallScroll);

                        ItemStack stormlordsWillScroll = new ItemStack(ModItems.SPELL_SCROLL);
                        stormlordsWillScroll.getOrCreateNbt().putString("spell_id", "anabnormalcircumstance:aero_stormlords_will");
                        entries.add(stormlordsWillScroll);

                    }).build());


    public static final ItemGroup ABNORMAL_DUNGEON_LOOT = Registry.register(Registries.ITEM_GROUP,
            new Identifier(AnAbnormalCircumstance.MOD_ID, "abnormal_dungeon_loot_group"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.abnormal_dungeon_loot"))
                    .icon(() -> new ItemStack(ModItems.HYDRO_CATALYST)).entries((displayContext, entries) -> {

                        // Relic
                        entries.add(ModItems.ABNORMAL_RELIC);
                        entries.add(ModItems.CLAIM_BYPASS);

                        // Currency
                        entries.add(ModItems.PLATINUM_COIN);
                        entries.add(ModItems.GOLD_COIN);
                        entries.add(ModItems.SILVER_COIN);
                        entries.add(ModItems.COPPER_COIN);

                        // Mana Crystals
                        entries.add(ModItems.MANA_CORE);
                        entries.add(ModItems.MANA_CLUSTER);
                        entries.add(ModItems.MANA_GEM);
                        entries.add(ModItems.MANA_SHARD);

                        // Consumable Souls
                        entries.add(ModItems.ORC_CHAMPION_SOUL);
                        entries.add(ModItems.BROODMOTHER_SOUL);

                        // Orc Loot
                        entries.add(ModItems.ORC_CHAMPION_FANG);
                        entries.add(ModItems.ORC_HIDE);

                        // Spider Loot
                        entries.add(ModItems.BROODMOTHER_EGG);
                        entries.add(ModItems.ARACHNID_SILK);

                        // General Dungeon Loot
                        entries.add(ModItems.RUSTED_KEY);
                        entries.add(ModItems.BOTTLED_LIGHTNING);
                        entries.add(ModItems.DIAMOND_RING);
                        entries.add(ModItems.GOLDEN_EARRINGS);
                        entries.add(ModItems.SILVER_NECKLACE);


                        // Consumable Essences
                        entries.add(ModItems.HYDRO_ESSENCE);
                        entries.add(ModItems.PYRO_ESSENCE);
                        entries.add(ModItems.GEO_ESSENCE);
                        entries.add(ModItems.AERO_ESSENCE);

                        // Shrine Loot
                        entries.add(ModItems.HYDRO_CATALYST);
                        entries.add(ModItems.PYRO_CATALYST);
                        entries.add(ModItems.GEO_CATALYST);
                        entries.add(ModItems.AERO_CATALYST);

                        // Blocks
                        entries.add(ModBlocks.HEPHAESTUS_ALTAR);
                        entries.add(ModBlocks.DARK_SAND);

                    }).build());


    public static final ItemGroup ABNORMAL_SPARKS = Registry.register(Registries.ITEM_GROUP,
            new Identifier(AnAbnormalCircumstance.MOD_ID, "abnormal_sparks_group"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.abnormal_sparks_group"))
                    .icon(() -> new ItemStack(ModItems.SPARK_OF_REGENERATION)).entries((displayContext, entries) -> {

                        entries.add(ModItems.WATER_SPARK);
                        entries.add(ModItems.AWKWARD_SPARK);

                        entries.add(ModItems.SPARK_OF_SWIFTNESS);
                        entries.add(ModItems.SPARK_OF_LONG_SWIFTNESS);
                        entries.add(ModItems.SPARK_OF_STRONG_SWIFTNESS);

                        entries.add(ModItems.SPARK_OF_STRENGTH);
                        entries.add(ModItems.SPARK_OF_LONG_STRENGTH);
                        entries.add(ModItems.SPARK_OF_STRONG_STRENGTH);

                        entries.add(ModItems.SPARK_OF_REGENERATION);
                        entries.add(ModItems.SPARK_OF_LONG_REGENERATION);
                        entries.add(ModItems.SPARK_OF_STRONG_REGENERATION);

                        entries.add(ModItems.SPARK_OF_SLOW_FALLING);
                        entries.add(ModItems.SPARK_OF_LONG_SLOW_FALLING);

                        entries.add(ModItems.SPARK_OF_INVISIBILITY);
                        entries.add(ModItems.SPARK_OF_LONG_INVISIBILITY);

                        entries.add(ModItems.SPARK_OF_JUMP_BOOST);
                        entries.add(ModItems.SPARK_OF_LONG_JUMP_BOOST);
                        entries.add(ModItems.SPARK_OF_STRONG_JUMP_BOOST);

                        entries.add(ModItems.SPARK_OF_NIGHT_VISION);
                        entries.add(ModItems.SPARK_OF_LONG_NIGHT_VISION);

                        entries.add(ModItems.SPARK_OF_WATER_BREATHING);
                        entries.add(ModItems.SPARK_OF_LONG_WATER_BREATHING);

                    }).build());


    public static final ItemGroup ABNORMAL_SPAWN_EGGS = Registry.register(Registries.ITEM_GROUP,
            new Identifier(AnAbnormalCircumstance.MOD_ID, "abnormal_spawn_egg_group"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.abnormal_spawn_egg_group"))
                    .icon(() -> new ItemStack(ModItems.ORC_WARRIOR_SPAWN_EGG)).entries((displayContext, entries) -> {

                        // Orcs
                        entries.add(ModItems.ORC_WARRIOR_SPAWN_EGG);
                        entries.add(ModItems.ORC_JAVELIN_THROWER_SPAWN_EGG);

                        // Spiders
                        entries.add(ModItems.BROOD_WARRIOR_SPAWN_EGG);

                    }).build());


    public static void registerItemGroups() {
        AnAbnormalCircumstance.LOGGER.info("Registering Mod Item Groups for " + AnAbnormalCircumstance.MOD_ID);
    }
}
