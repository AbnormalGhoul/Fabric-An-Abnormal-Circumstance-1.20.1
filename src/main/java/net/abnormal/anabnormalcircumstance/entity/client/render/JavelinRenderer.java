package net.abnormal.anabnormalcircumstance.entity.client.render;

import net.abnormal.anabnormalcircumstance.entity.client.model.JavelinModel;
import net.abnormal.anabnormalcircumstance.entity.custom.projectile.JavelinProjectileEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class JavelinRenderer extends GeoEntityRenderer<JavelinProjectileEntity> {

    public JavelinRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new JavelinModel());
        this.shadowRadius = 0.2f;
    }

    @Override
    public Identifier getTextureLocation(JavelinProjectileEntity animatable) {
        return new Identifier("anabnormalcircumstance", "textures/entity/javelin.png");
    }

    @Override
    public void render(JavelinProjectileEntity entity, float yaw, float tickDelta, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumers, int light) {

        matrixStack.push();

        // Rotate to velocity direction
        float pitch = entity.getPitch(tickDelta);
        matrixStack.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_X.rotationDegrees(pitch));

        float bodyYaw = entity.getYaw(tickDelta);
        matrixStack.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(bodyYaw));

        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumers, light);

        matrixStack.pop();
    }
}
