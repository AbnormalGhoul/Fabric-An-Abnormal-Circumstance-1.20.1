package net.abnormal.anabnormalcircumstance.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.screen.BrewingStandScreenHandler$PotionSlot")
public abstract class BrewingStandPotionSlotMixin extends Slot {

    public BrewingStandPotionSlotMixin(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Inject(method = "canInsert(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void anabnormalcircumstance$allowSparks(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Identifier id = Registries.ITEM.getId(stack.getItem());
        if (id.getNamespace().equals("anabnormalcircumstance")) {
            if (id.getPath().contains("spark")) {
                cir.setReturnValue(true);
            }
        }
    }
}
