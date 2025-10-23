package net.abnormal.anabnormalcircumstance;

import net.abnormal.anabnormalcircumstance.block.ModBlocks;
import net.abnormal.anabnormalcircumstance.block.entity.ModBlockEntities;
import net.abnormal.anabnormalcircumstance.component.ModComponents;
import net.abnormal.anabnormalcircumstance.magic.SpellRegistry;
import net.abnormal.anabnormalcircumstance.magic.spells.WaterVeilSpell;
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

        PacketHandler.register();
        registerSpells();

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

//        ServerTickEvents.END_SERVER_TICK.register(server -> {
//            SpellCooldownManager.tick();
//
//            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
//                ModComponents.MANA.get(player).tick();
//            }
//        });

        LOGGER.info("An Abnormal Circumstance Mod Initialized");

    }

    private void registerSpells() {
        // Example icons (use your actual icon IDs):
        SpellRegistry.register(new net.abnormal.anabnormalcircumstance.magic.spells.WaterVeilSpell(
                new Identifier(MOD_ID, "hydro_water_veil"),
                new Identifier(MOD_ID, "textures/gui/spells/hydro_water_veil.png")
        ));
//        SpellRegistry.register(new net.abnormal.anabnormalcircumstance.magic.spells.HealingFluidsSpell(
//                new Identifier(MOD_ID, "hydro_healing_fluids"),
//                new Identifier(MOD_ID, "textures/gui/spells/hydro_healing_fluids.png")
//        ));
//        SpellRegistry.register(new net.abnormal.anabnormalcircumstance.magic.spells.QuickClawsSpell(
//                new Identifier(MOD_ID, "geo_quick_claws"),
//                new Identifier(MOD_ID, "textures/gui/spells/geo_quick_claws.png")
//        ));
    }

}