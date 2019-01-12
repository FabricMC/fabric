package net.fabricmc.fabric.api.client.model.fabric;

import net.minecraft.block.BlockRenderLayer;

public interface ModelMaterialBuilder {
    ModelMaterial build();
    
    void setBlendMode(int textureLayer, BlockRenderLayer blendMode);
    
    default void setBlendMode(BlockRenderLayer blendMode) {
        setBlendMode(0, blendMode);
    }
    
    void setTextureDepth(int depth);
    
    void setShading(int textureLayer, ShadingMode shading);
    
    default void setShading(ShadingMode shading) {
        setShading(0, shading);
    }
    
    void setEmissive(int textureLayer, boolean isEmissive);
    
    default void setEmissive(boolean isEmissive) {
        setEmissive(0, isEmissive);
    }
}
