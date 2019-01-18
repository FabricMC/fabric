package net.fabricmc.fabric.api.client.model.fabric;

@FunctionalInterface
public interface VertexReader {
    /**
     * Reads baked vertex data and outputs a standard block-format
     * baked quad in the given array and location. Uses texture
     * coordinates and colors from the indicated layer.<p>
     * 
     * Primary use case is generating damage models or other operations outside
     * the renderer. Models that are able to generate mesh geometry on the fly or 
     * retrieve it from a pre-baked source may find that a cleaner approach.<p>
     * 
     * Note that for damage models, the original texture sprite is needed
     * to derive the non-interpolated UV coordinates for re-texturing.
     * Model renderers only accept baked UV coordinates and so models
     * that use this approach still need to track the original sprites.
     */
    void outputStandardQuad(ModelMaterial material, int layerIndex, int [] source, int sourceIndex, int[] target, int targetIndex);
}
