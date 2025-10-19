package net.abnormal.anabnormalcircumstance.event;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;

import net.minecraft.util.Identifier;

public class ModEvents {

    public static void registerEvents() {
        ModAttackEvent.register();
    }
}