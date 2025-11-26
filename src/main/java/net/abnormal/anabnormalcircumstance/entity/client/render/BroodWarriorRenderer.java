package net.abnormal.anabnormalcircumstance.entity.client.render;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.entity.client.model.BroodWarriorModel;
import net.abnormal.anabnormalcircumstance.entity.custom.mob.BroodWarriorEntity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BroodWarriorRenderer extends GeoEntityRenderer<BroodWarriorEntity> {

    public BroodWarriorRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BroodWarriorModel());
        this.shadowRadius = 0.7f;
    }

    @Override
    public Identifier getTextureLocation(BroodWarriorEntity animatable) {
        return new Identifier(AnAbnormalCircumstance.MOD_ID, "textures/entity/brood_warrior.png");
    }
}
