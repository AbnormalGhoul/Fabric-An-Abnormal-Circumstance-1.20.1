package net.abnormal.anabnormalcircumstance.datagen;

import net.abnormal.anabnormalcircumstance.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ItemTags.TRIMMABLE_ARMOR)
                .add(
                    ModItems.ADAMANTITE_HELMET, ModItems.ADAMANTITE_CHESTPLATE, ModItems.ADAMANTITE_LEGGINGS, ModItems.ADAMANTITE_BOOTS,
                    ModItems.MITHRIL_HELMET, ModItems.MITHRIL_CHESTPLATE, ModItems.MITHRIL_LEGGINGS, ModItems.MITHRIL_BOOTS
                );
    }
}
