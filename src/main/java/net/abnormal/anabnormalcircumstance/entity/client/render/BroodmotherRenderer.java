package net.abnormal.anabnormalcircumstance.entity.client.render;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.entity.client.model.BroodmotherModel;
import net.abnormal.anabnormalcircumstance.entity.custom.mob.BroodmotherEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BroodmotherRenderer extends GeoEntityRenderer<BroodmotherEntity> {

    public BroodmotherRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BroodmotherModel());
        this.shadowRadius = 0.9f;
    }

    @Override
    public Identifier getTextureLocation(BroodmotherEntity animatable) {
        return new Identifier(AnAbnormalCircumstance.MOD_ID, "textures/entity/broodmother.png");
    }

    @Override
    public void render(BroodmotherEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }}
