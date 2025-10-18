package net.abnormal.anabnormalcircumstance.block.custom;

import net.abnormal.anabnormalcircumstance.block.entity.custom.HephaestusAltarBlockEntity;
import net.abnormal.anabnormalcircumstance.block.entity.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HephaestusAltarBlock extends BlockWithEntity {

    public HephaestusAltarBlock(Settings settings) {
        super(settings.strength(-1.0F, 3600000.0F));
    }

    // Use @SuppressWarnings for deprecated methods if no alternatives exist
    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(
                Block.createCuboidShape(0, 0, 0, 16, 2, 16),
                Block.createCuboidShape(1, 2, 1, 15, 3, 15),
                Block.createCuboidShape(3, 3, 3, 13, 11, 13),
                Block.createCuboidShape(5, 11, 5, 11, 12, 11),
                Block.createCuboidShape(4, 15, 4, 12, 16, 12)
        );
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HephaestusAltarBlockEntity(pos, state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof HephaestusAltarBlockEntity) {
                ItemScatterer.spawn(world, pos, (HephaestusAltarBlockEntity)blockEntity);
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = ((HephaestusAltarBlockEntity) world.getBlockEntity(pos));

            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.HEPHAESTUS_ALTAR_BLOCK_ENTITY_BLOCK,
                (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }
}
