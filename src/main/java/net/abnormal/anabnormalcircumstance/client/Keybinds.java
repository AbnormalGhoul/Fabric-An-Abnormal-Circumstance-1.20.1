package net.abnormal.anabnormalcircumstance.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    public static KeyBinding UNIQUE_BLADE_ABILITY = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.anabnormalcircumstance.unique_blade_ability", GLFW.GLFW_KEY_R, "category.anabnormalcircumstance")
    );
}