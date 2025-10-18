package net.abnormal.anabnormalcircumstance.block.entity.renderer;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HephaestusAltarBlockEntityRenderState {

    public static void syncToClients(World world, BlockPos pos) {
        if (world == null) return;
        world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
    }


}
