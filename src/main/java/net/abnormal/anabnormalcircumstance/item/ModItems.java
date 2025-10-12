package net.abnormal.anabnormalcircumstance.item;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.impl.itemgroup.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item BLANK_SCROLL = registerItem("blank_scroll", new Item(new Item.Settings()));
    public static final Item AMETHYST_ARROW = registerItem("amethyst_arrow", new Item(new Item.Settings()));



    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(AnAbnormalCircumstance.MOD_ID, name), item);
    }

    public static void registerModItems() {
        AnAbnormalCircumstance.LOGGER.info("Registering Mod Items for " + AnAbnormalCircumstance.MOD_ID);
    }
}
