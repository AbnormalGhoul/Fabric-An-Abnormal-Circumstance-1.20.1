package net.abnormal.anabnormalcircumstance.magic.spells.geomancy;

import net.abnormal.anabnormalcircumstance.magic.Spell;
import net.abnormal.anabnormalcircumstance.magic.SpellElement;
import net.abnormal.anabnormalcircumstance.magic.SpellTier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class GrowthSpell extends Spell {

    private static final int RADIUS = 5;

    public GrowthSpell(Identifier id, Identifier icon) {
        super(id, SpellElement.GEOMANCY, SpellTier.TIER_1, 10, 30, icon, "Growth", "Accelerates plant growth in the area, causing crops and plants to mature instantly.");
    }

    @Override
    public boolean cast(ServerPlayerEntity caster) {
        ServerWorld world = caster.getServerWorld();
        BlockPos center = caster.getBlockPos();

        // Grow nearby crops by 1 stage
        growNearbyCrops(world, center);

        // Play earth/dirt sound
        world.playSound(null, center, SoundEvents.BLOCK_GRASS_PLACE, SoundCategory.PLAYERS, 1.5f, 1.0f);

        // Show green particles around player
        spawnCastParticles(world, caster);

        return true;
    }

    private void growNearbyCrops(ServerWorld world, BlockPos center) {
        BlockPos.iterate(center.add(-RADIUS, -1, -RADIUS), center.add(RADIUS, 1, RADIUS))
                .forEach(pos -> {
                    BlockState state = world.getBlockState(pos);
                    Block block = state.getBlock();

                    // Find any "age" property of type IntProperty
                    state.getProperties().stream()
                            .filter(p -> p.getName().equals("age") && p instanceof net.minecraft.state.property.IntProperty)
                            .findFirst()
                            .ifPresent(prop -> {
                                var ageProp = (net.minecraft.state.property.IntProperty) prop;
                                int age = state.get(ageProp);
                                int maxAge = ageProp.getValues().stream().max(Integer::compareTo).orElse(age);

                                if (age < maxAge) {
                                    world.setBlockState(pos, state.with(ageProp, age + 1), Block.NOTIFY_ALL);

                                    // Burst of green particles at grown crop
                                    world.spawnParticles(
                                            ParticleTypes.HAPPY_VILLAGER,
                                            pos.getX() + 0.5,
                                            pos.getY() + 0.7,
                                            pos.getZ() + 0.5,
                                            6, 0.3, 0.3, 0.3, 0.1
                                    );
                                }
                            });
                });
    }


    private void spawnCastParticles(ServerWorld world, ServerPlayerEntity caster) {
        double centerX = caster.getX();
        double centerY = caster.getY() + 1.0;
        double centerZ = caster.getZ();

        for (int i = 0; i < 60; i++) {
            double angle = Math.random() * Math.PI * 2;
            double radius = 1.0 + Math.random() * 1.5;
            double x = centerX + Math.cos(angle) * radius;
            double z = centerZ + Math.sin(angle) * radius;
            double y = centerY + (Math.random() * 0.5);

            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 1, 0, 0, 0, 0);
        }
    }
}
