package net.abnormal.anabnormalcircumstance.screen;

import net.abnormal.anabnormalcircumstance.block.entity.custom.HephaestusAltarBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class HephaestusAltarScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    public final HephaestusAltarBlockEntity blockEntity;

    // Custom slot to prevent insertion into output slot
    private static class OutputSlot extends Slot {
        public OutputSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }
        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }
    }

    public HephaestusAltarScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, inventory.player.getWorld().getBlockEntity(buf.readBlockPos()),
                new ArrayPropertyDelegate(9));
    }

    public HephaestusAltarScreenHandler(int SyncId, PlayerInventory playerInventory, BlockEntity blockEntity, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.HEPHAESTUS_ALTAR_SCREEN_HANDLER, SyncId);
        checkSize(((Inventory) blockEntity), 9);

        this.inventory = ((Inventory) blockEntity);
        inventory.onOpen(playerInventory.player);

        this.propertyDelegate = propertyDelegate;
        this.blockEntity = ((HephaestusAltarBlockEntity) blockEntity);

        this.addSlot(new Slot(inventory, 0, 53, -1));
        this.addSlot(new Slot(inventory, 1, 107, -1));
        this.addSlot(new Slot(inventory, 2, 26, 17));
        this.addSlot(new Slot(inventory, 3, 134, 17));
        this.addSlot(new OutputSlot(inventory, 4, 80, 35)); // Output slot
        this.addSlot(new Slot(inventory, 5, 26, 53));
        this.addSlot(new Slot(inventory, 6, 134, 53));
        this.addSlot(new Slot(inventory, 7, 53, 71));
        this.addSlot(new Slot(inventory, 8, 107, 71));

        addPlayerInventory(playerInventory, 112); // 134 - 18 = 116
        addPlayerHotbar(playerInventory, 170);    // 192 - 18 = 174

        addProperties(propertyDelegate);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        if (invSlot == 4) return ItemStack.EMPTY;
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        // Prevent interaction if altar is locked
        if (blockEntity.isLocked()) return false;
        return this.inventory.canPlayerUse(player);
    }

    // Player inventory slots (3 rows x 9 columns), positioned below altar slots
    private void addPlayerInventory(PlayerInventory playerInventory, int startY) {
        int startX = 8;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, startX + col * 18, startY + row * 18));
            }
        }
    }

    // Player hotbar slots (1 row x 9 columns), positioned at the bottom
    private void addPlayerHotbar(PlayerInventory playerInventory, int startY) {
        int startX = 8;
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, startX + col * 18, startY));
        }
    }
}
