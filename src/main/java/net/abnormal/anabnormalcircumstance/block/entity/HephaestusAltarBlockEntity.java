package net.abnormal.anabnormalcircumstance.block.entity;

import net.abnormal.anabnormalcircumstance.screen.HephaestusAltarScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.text.Text;
import net.minecraft.sound.SoundEvents;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.util.math.Box;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.boss.BossBar.Color;
import net.minecraft.entity.boss.BossBar.Style;
import java.util.List;

public class HephaestusAltarBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;

    // Creation phase state
    private boolean isCreating = false;
    private int creationTicks = 0;
    private static final int CREATION_DURATION_TICKS = 15 * 20; // 15 minutes
    private ServerBossBar bossBar;

    public HephaestusAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HEPHAESTUS_ALTAR_BLOCK_ENTITY_BLOCK, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                if (index == 0) return isCreating ? 1 : 0;
                if (index == 1) return creationTicks;
                if (index == 2) return CREATION_DURATION_TICKS;
                return 0;
            }
            @Override
            public void set(int index, int value) {
                if (index == 0) isCreating = value == 1;
                if (index == 1) creationTicks = value;
            }
            @Override
            public int size() { return 3; }
        };
        bossBar = new ServerBossBar(Text.literal("Hephaestus's Altar Creation"), Color.BLUE, Style.PROGRESS);
        bossBar.setVisible(false);
    }

    // Recipe: 4 diamonds + 4 gold in any orientation -> totem of undying
    private boolean matchesRecipe() {
        int diamonds = 0, gold = 0;
        for (int i = 0; i < 9; i++) {
            if (i == 4) continue; // skip center slot
            ItemStack stack = inventory.get(i);
            if (stack.getItem() == Items.DIAMOND) diamonds += stack.getCount();
            else if (stack.getItem() == Items.GOLD_INGOT) gold += stack.getCount();
            else if (!stack.isEmpty()) return false;
        }
        return diamonds == 4 && gold == 4;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;

        if (isCreating) {
            creationTicks++;
            updateBossBar(world);

            // Particle effect: blue particles in a circle
            double radius = 1.5 + Math.sin(world.getTime() / 20.0) * 0.5;
            for (int i = 0; i < 8; i++) {
                double angle = i * Math.PI * 2 / 8 + (world.getTime() / 20.0);
                double px = pos.getX() + 0.5 + radius * Math.cos(angle);
                double py = pos.getY() + 1.2;
                double pz = pos.getZ() + 0.5 + radius * Math.sin(angle);
                ((ServerWorld)world).spawnParticles(ParticleTypes.END_ROD, px, py, pz, 1, 0, 0, 0, 0.0);
            }

            if (creationTicks >= CREATION_DURATION_TICKS) {
                finishCreation(world);
            }
        } else {
            // If not creating, check for recipe and start if possible
            if (matchesRecipe() && inventory.get(4).isEmpty()) {
                startCreation(world);
            }
        }
    }

    private void startCreation(World world) {
        isCreating = true;
        creationTicks = 0;
        bossBar.setVisible(true);
        updateBossBar(world);

        // Kick out all users from GUI
        List<ServerPlayerEntity> players = ((ServerWorld)world).getPlayers(player -> player.currentScreenHandler != null && player.currentScreenHandler instanceof net.abnormal.anabnormalcircumstance.screen.HephaestusAltarScreenHandler && ((HephaestusAltarScreenHandler)player.currentScreenHandler).blockEntity == this);
        for (ServerPlayerEntity player : players) {
            player.closeHandledScreen();
        }

        // Send chat message
        world.getServer().getPlayerManager().broadcast(Text.literal("A craft has started at Hephaestus's Altar!"), false);

        // Play sound
        world.playSound(null, pos, SoundEvents.BLOCK_END_PORTAL_SPAWN, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    private void finishCreation(World world) {
        isCreating = false;
        creationTicks = 0;
        bossBar.setVisible(false);

        // Remove input items
        for (int i = 0; i < 9; i++) {
            if (i == 4) continue;
            inventory.set(i, ItemStack.EMPTY);
        }
        // Place output in center slot
        inventory.set(4, new ItemStack(Items.TOTEM_OF_UNDYING));
        markDirty();
    }

    private void updateBossBar(World world) {
        bossBar.setPercent(1.0f - (float)creationTicks / CREATION_DURATION_TICKS);
        // Add all nearby players
        List<ServerPlayerEntity> players = ((ServerWorld)world).getPlayers(player -> player.squaredDistanceTo(Vec3d.ofCenter(pos)) < 150*150);
        bossBar.clearPlayers();
        for (ServerPlayerEntity player : players) {
            bossBar.addPlayer(player);
        }
    }

    public boolean isLocked() {
        return isCreating;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Hephaestus's Altar");
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putBoolean("IsCreating", isCreating);
        nbt.putInt("CreationTicks", creationTicks);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
        isCreating = nbt.getBoolean("IsCreating");
        creationTicks = nbt.getInt("CreationTicks");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new net.abnormal.anabnormalcircumstance.screen.HephaestusAltarScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
}
