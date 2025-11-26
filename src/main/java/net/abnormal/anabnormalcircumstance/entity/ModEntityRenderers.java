package net.abnormal.anabnormalcircumstance.entity;

import net.abnormal.anabnormalcircumstance.entity.client.render.*;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ModEntityRenderers {
    public static void register() {
        EntityRendererRegistry.register(ModEntities.SILVER_ARROW, SilverArrowEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.JAVELIN_PROJECTILE, JavelinRenderer::new);

        EntityRendererRegistry.register(ModEntities.ORC_WARRIOR, OrcWarriorRenderer::new);
        EntityRendererRegistry.register(ModEntities.ORC_JAVELIN_THROWER, OrcJavelinThrowerRenderer::new);

        EntityRendererRegistry.register(ModEntities.BROOD_WARRIOR, BroodWarriorRenderer::new);


    }
}
