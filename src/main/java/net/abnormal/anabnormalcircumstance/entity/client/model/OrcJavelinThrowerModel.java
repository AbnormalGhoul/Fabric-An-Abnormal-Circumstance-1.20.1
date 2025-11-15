package net.abnormal.anabnormalcircumstance.entity.client.model;

import net.abnormal.anabnormalcircumstance.entity.custom.mob.OrcJavelinThrowerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OrcJavelinThrowerModel extends GeoModel<OrcJavelinThrowerEntity> {

    @Override
    public Identifier getModelResource(OrcJavelinThrowerEntity animatable) {
        return new Identifier("anabnormalcircumstance", "geo/orc_javelin_thrower.geo.json");
    }

    @Override
    public Identifier getTextureResource(OrcJavelinThrowerEntity animatable) {
        return new Identifier("anabnormalcircumstance", "textures/entity/orc_javelin_thrower.png");
    }

    @Override
    public Identifier getAnimationResource(OrcJavelinThrowerEntity animatable) {
        return new Identifier("anabnormalcircumstance", "animations/orc_javelin_thrower.animation.json");
    }

    @Override
    public void setCustomAnimations(OrcJavelinThrowerEntity animatable, long instanceId, AnimationState<OrcJavelinThrowerEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("hi_head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
