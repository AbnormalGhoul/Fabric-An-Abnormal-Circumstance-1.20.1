package net.abnormal.anabnormalcircumstance.magic.spells.geomancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.block.Fertilizable;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GrowthSpell extends Spell {
    public GrowthSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.GEOMANCY, SpellTier.TIER_1, 20, 30, icon, "Growth");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        World world = caster.getWorld();
        Vec3d look = caster.getRotationVec(1.0F);
        BlockHitResult hit = (BlockHitResult) caster.raycast(10, 0, false);
        BlockPos pos = hit.getBlockPos();

        if (world.getBlockState(pos).getBlock() instanceof Fertilizable fertilizable) {
            fertilizable.grow((net.minecraft.server.world.ServerWorld) world, world.random, pos, world.getBlockState(pos));
            world.playSound(null, pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);
            return true;
        }
        return false;
    }
}
