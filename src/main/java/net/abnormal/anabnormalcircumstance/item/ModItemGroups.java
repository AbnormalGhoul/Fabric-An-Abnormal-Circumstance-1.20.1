package net.abnormal.anabnormalcircumstance.item;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup AN_ABNORMAL_CIRCUMSTANCE = Registry.register(Registries.ITEM_GROUP,
            new Identifier(AnAbnormalCircumstance.MOD_ID, "ananbnormalcircumstance"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.ananbnormalcircumstance"))
                    .icon(() -> new ItemStack(ModItems.BLANK_SCROLL)).entries((displayContext, entries) -> {

                        entries.add(ModItems.BLANK_SCROLL);
                        entries.add(ModItems.AMETHYST_ARROW);
                        entries.add(ModBlocks.DARK_SAND);
                        // Add more entries here

                    }).build());

    public static void registerItemGroups() {
        AnAbnormalCircumstance.LOGGER.info("Registering Mod Item Groups for " + AnAbnormalCircumstance.MOD_ID);
    }
}
