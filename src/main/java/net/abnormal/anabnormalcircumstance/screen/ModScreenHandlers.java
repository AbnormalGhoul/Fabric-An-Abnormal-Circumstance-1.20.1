package net.abnormal.anabnormalcircumstance.screen;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {

    public static final ScreenHandlerType<HephaestusAltarScreenHandler> HEPHAESTUS_ALTAR_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(AnAbnormalCircumstance.MOD_ID, "hephaestus_altar_screen_handler"),
                    new ExtendedScreenHandlerType<>(HephaestusAltarScreenHandler::new));

    public static void registerScreenHandlers() {
        AnAbnormalCircumstance.LOGGER.info("Registering Screen Handlers for " + AnAbnormalCircumstance.MOD_ID);
    }
}
