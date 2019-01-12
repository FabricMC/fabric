package net.fabricmc.fabric.api.client.model.fabric;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;

public interface ModelBuilder extends ModelVertexConsumer {
    BakedModel buildStatic(boolean isItem, Sprite sprite, ModelTransformation transformation, ModelItemPropertyOverrideList itemPropertyOverrides);
    
    BakedModel buildDynamic(DynamicModelVertexProducer producer, Sprite sprite, ModelTransformation transformation, ModelItemPropertyOverrideList itemPropertyOverrides);
}
