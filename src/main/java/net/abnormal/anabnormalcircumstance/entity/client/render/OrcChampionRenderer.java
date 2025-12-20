package net.abnormal.anabnormalcircumstance.entity.client.render;

import net.abnormal.anabnormalcircumstance.AnAbnormalCircumstance;
import net.abnormal.anabnormalcircumstance.entity.client.model.OrcChampionModel;
import net.abnormal.anabnormalcircumstance.entity.custom.mob.OrcChampionEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OrcChampionRenderer extends GeoEntityRenderer<OrcChampionEntity> {

    public OrcChampionRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new OrcChampionModel());
        this.shadowRadius = 0.9f;
    }

    @Override
    public Identifier getTextureLocation(OrcChampionEntity animatable) {
        return new Identifier(AnAbnormalCircumstance.MOD_ID, "textures/entity/orc_champion.png");
    }

    @Override
    public void render(OrcChampionEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }}
