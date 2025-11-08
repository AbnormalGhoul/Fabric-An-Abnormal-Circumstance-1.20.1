package net.abnormal.anabnormalcircumstance.block.entity.custom;

import net.abnormal.anabnormalcircumstance.block.entity.ImplementedInventory;
import net.abnormal.anabnormalcircumstance.block.entity.ModBlockEntities;
import net.abnormal.anabnormalcircumstance.screen.HephaestusAltarScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.text.Text;
import net.minecraft.sound.SoundEvents;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.boss.BossBar.Color;
import net.minecraft.entity.boss.BossBar.Style;
import net.abnormal.anabnormalcircumstance.recipe.AltarRecipe;
import java.util.List;

public class HephaestusAltarBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;

    // Creation phase state
    private boolean isCreating = false;
    private int creationTicks = 0;
    private static final int CREATION_DURATION_TICKS = 60 * 20 * 15; // 15 minutes
    private ServerBossBar bossBar;

    // Store the recipe being crafted during the creation phase
    private AltarRecipe currentRecipe = null;

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

    private AltarRecipe getMatchingRecipe(World world) {
        for (AltarRecipe recipe : net.abnormal.anabnormalcircumstance.recipe.ModRecipes.ALTAR_RECIPES) {
            if (recipe.matches(this, world)) {
                return recipe;
            }
        }
        return null;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;

        if (isCreating) {
            creationTicks++;
            updateBossBar(world);

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
            AltarRecipe recipe = getMatchingRecipe(world);
            if (recipe != null && inventory.get(4).isEmpty()) {
                startCreation(world, recipe);
            }
        }
    }

    private void startCreation(World world, AltarRecipe recipe) {
        isCreating = true;
        creationTicks = 0;
        bossBar.setVisible(true);
        updateBossBar(world);

        currentRecipe = recipe;

        List<ServerPlayerEntity> players = ((ServerWorld)world).getPlayers(player -> player.currentScreenHandler != null && player.currentScreenHandler instanceof net.abnormal.anabnormalcircumstance.screen.HephaestusAltarScreenHandler && ((HephaestusAltarScreenHandler)player.currentScreenHandler).blockEntity == this);
        for (ServerPlayerEntity player : players) {
            player.closeHandledScreen();
        }

        inventory.set(4, ItemStack.EMPTY);

        markDirty();
        if (world != null) {
            world.updateListeners(this.pos, world.getBlockState(this.pos), world.getBlockState(this.pos), 3);
            sendBlockEntityUpdateToTrackingPlayers(world);
        }

        world.getServer().getPlayerManager().broadcast(
                Text.literal("A Divine Forging has been started at the Great Altar").formatted(net.minecraft.util.Formatting.RED), false
        );

        world.playSound(null, pos, SoundEvents.BLOCK_END_PORTAL_SPAWN, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    private void finishCreation(World world) {
        isCreating = false;
        creationTicks = 0;
        bossBar.setVisible(false);

        for (int i = 0; i < 9; i++) {
            if (i == 4) continue;
            inventory.set(i, ItemStack.EMPTY);
        }
        if (currentRecipe != null) {
            inventory.set(4, currentRecipe.getOutput(world.getRegistryManager()));
        } else {
            inventory.set(4, ItemStack.EMPTY);
        }
        currentRecipe = null;

        markDirty();
        if (world != null) {
            world.updateListeners(this.pos, world.getBlockState(this.pos), world.getBlockState(this.pos), 3);
            sendBlockEntityUpdateToTrackingPlayers(world);
            // Force a block update for all clients
            net.abnormal.anabnormalcircumstance.block.entity.renderer.HephaestusAltarBlockEntityRenderState.syncToClients(world, pos);
        }

        world.playSound(null, pos, SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    private void updateBossBar(World world) {
        bossBar.setPercent(1.0f - (float)creationTicks / CREATION_DURATION_TICKS);
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

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.world != null && !this.world.isClient) {
            this.world.updateListeners(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
            sendBlockEntityUpdateToTrackingPlayers(this.world);
        }
    }

    private void sendBlockEntityUpdateToTrackingPlayers(World world) {
        if (world == null || world.isClient) return;
        if (!(world instanceof ServerWorld)) return;

        Packet<ClientPlayPacketListener> pkt = BlockEntityUpdateS2CPacket.create(this);
        if (pkt == null) return;

        double radius = 64.0;
        List<ServerPlayerEntity> players = ((ServerWorld) world).getPlayers(player -> player.squaredDistanceTo(Vec3d.ofCenter(pos)) < radius * radius);
        for (ServerPlayerEntity player : players) {
            player.networkHandler.sendPacket(pkt);
        }

        try {
            ChunkPos chunkPos = new ChunkPos(pos);
            ((ServerWorld)world).getChunkManager().threadedAnvilChunkStorage.getPlayersWatchingChunk(chunkPos, true)
                    .forEach(player -> player.networkHandler.sendPacket(pkt));
        } catch (Throwable ignored) { }
    }

    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public NbtCompound toInitialChunkData() {
        NbtCompound nbt = new NbtCompound();
        this.writeNbt(nbt);
        return nbt;
    }
}