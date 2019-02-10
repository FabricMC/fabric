/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.client.model.fabric;

import java.lang.ref.WeakReference;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;

/**
 * Interface for baked models that output meshes with enhanced rendering features.
 * Can also be used to generate or customize outputs based on world state instead of
 * or in addition to block state when render chunks are rebuilt.<p>
 * 
 * Note for {@link Renderer} implementors: Fabric causes BakedModel to extend this
 * interface with {@link #isVanillaModel()} == true and to produce standard vertex data. 
 * This means any BakedModel instance can be safely cast to this interface without an instanceof check.
 */
public interface FabricBakedModel {
    /**
     * This method will be called during chunk rebuilds to generate both the static and
     * dynamic portions of a block model when the model implements this interface and
     * {@link #isVanillaModel()} returns false. <p>
     * 
     * This method will always be called exactly one time per block position 
     * per chunk rebuild, irrespective of which or how many faces or block render layers are included 
     * in the model. Models must output all quads/meshes in a single pass.<p>
     * 
     * Renderer will handle face occlusion and filter quads on faces 
     * obscured by neighboring blocks.  Models only need to consider "sides" to the
     * extent the model is driven by connection with neighbor blocks or other world state.<p>
     * 
     * The RenderView parameter provides access to cached block state, fluid state, 
     * and lighting information. Models should avoid using {@link ModelBlockView#getBlockEntity(BlockPos)}
     * to ensure thread safety because this method is called outside the main client thread.
     * Models that require Block Entity data should implement {@link DynamicModelBlockEntity}.
     * Look to {@link ModelBlockView#getCachedRenderData(BlockPos)} for more information.<p>
     * 
     * Note: with {@link BakedModel#getQuads(BlockState, net.minecraft.util.math.Direction, Random)}, the random 
     * parameter is normally initialized with the same seed prior to each face layer.
     * Model authors should note this method is called only once per block, and reseed if needed.
     * For wrapped vanilla baked models, it will probably be easier to use {@link RenderContext#fallbackModelConsumer()}.<p>
     */
    void produceTerrainQuads(ModelBlockView blockView, BlockState state, BlockPos pos, Random random, long seed, RenderContext context);

    /**
     * When true, signals renderer this producer is a vanilla baked model without
     * any enhanced features from this API. Allows the renderer to optimize or
     * route vanilla models through the unmodified vanilla pipeline if desired.<p>
     * 
     * Fabric overrides to true for vanilla baked models.  
     * Enhanced models that use this API should return false,
     * otherwise the API will not recognize the model.<p>
     */
    boolean isVanillaModel(); 
    
    /**
     * If non-null, the result will be used to render block-breaking instead of the output
     * from {@link BakedModel#getQuads(BlockState, net.minecraft.util.math.Direction, Random)}.<p>
     * 
     * This method will always be called from the main client thread. No special concurrency
     * precautions apply. (It's safe to access BlockEntity state from here.)<p>
     * 
     * There are at least three use cases when overriding is helpful:
     * <li>Dynamic models that have variable shape based on world state.</li>
     * <li>Models painted with multi-block textures that render poorly when the sub-texture 
     * uv coordinates are re-mapped to the block breaking texture.</li>
     * <li>Multi-layer quads that would normally output two or three co-planar quads from
     * {@link BakedModel#getQuads(BlockState, net.minecraft.util.math.Direction, Random)} -
     * the extra quads are useless overhead and a simpler damage model can be substituted.</li><p>
     * 
     * Note that damage rendering does not care about the specific textures on the model, 
     * it simply re-maps quads to the block-breaking texture. No enhanced rendering features apply.<p>
     * 
     * This method may never be called for any particular model (depends on player behavior)
     * but if it is called, the calls will be rapid: once per frame. Implementations 
     * with dynamic, non-trivial damage models should consider caching the result.
     * {@link WeakReference} would be suitable for this.
     */
    default BakedModel getDamageModel(ExtendedBlockView blockView, BlockState state, BlockPos pos) {
        return null;
    }
    
    /**
     * This method will be called during item rendering to generate both the static and
     * dynamic portions of an item model when the model implements this interface and
     * {@link #isVanillaModel()} returns false.<p>
     * 
     * Vanilla item rendering is normally very limited. It ignores lightmaps, vertex colors,
     * and vertex normals. Renderers are expected to implement enhanced features for item 
     * models. If a feature is impractical due to performance or other concerns, then the
     * renderer must at least give acceptable visual results without the need for special-
     * case handling in model implementations.<p>
     * 
     * Calls to this method will generally happen on the main client thread but nothing 
     * prevents a mod or renderer from calling this method concurrently. Implementations 
     * should not mutate the ItemStack parameter, and best practice will be to make the 
     * method thread-safe.<p>
     * 
     * Implementing this method does NOT mitigate the need to implement a functional 
     * {@link BakedModel#getItemPropertyOverrides()} method, because this method will be called 
     * on the <em>result</em> of  {@link #getItemPropertyOverrides()}.  However, that 
     * method can simply return the base model because the output from this method will
     * be used for rendering.<p>
     * 
     * Renderer implementations should also use this method to obtain the quads used
     * for item enchantment glint rendering.  This means models can put geometric variation
     * logic here, instead of returning every possible shape from {@link #getItemPropertyOverrides()}
     * as vanilla baked models.
     */
    void produceItemQuads(ItemStack stack, Random random, long seed, RenderContext context);
    
    /**
     * Called to render block models with world state outside of chunk rebuild or block entity rendering.
     * Typically this happens when the block is being rendered as an entity, not as a block placed in the world.
     * Currently this happens for falling blocks and blocks being pushed by a piston, but renderers
     * should invoke this for all calls to {@link BlockModelRenderer#tesselate(ExtendedBlockView, BakedModel, BlockState, BlockPos, net.minecraft.client.render.BufferBuilder, boolean, Random, long)}
     * that occur outside of chunk rebuilds to allow for features added by mods, unless 
     * {@link #isVanillaModel()} returns true.<p>
     * 
     * This method will be called every frame. Model implementations should rely on pre-baked meshes 
     * as much as possible and keep transformation to a minimum.  The provided block position will 
     * typically be the <em>nearest</em> block position and not actual. For this reason, neighbor
     * state lookups are best avoided or will require special handling. Block entity lookups are 
     * likely to fail and/or give meaningless results.<p>
     * 
     * While this method is generally called from the main client thread, best practice will
     * be to make implementations thread-safe.
     */
    void produceBlockQuads(ExtendedBlockView blockView, BlockState state, BlockPos pos, Random random, long seed, RenderContext context);
    
    /**
     * Allows renderers (and potentially compound models) to optimize caching of model output
     * by identifying the inputs that affect model appearance. This is meant to be used in a 
     * terrain-rendering context.<p>
     * 
     * The value should be {@link #ALWAYS_CACHE}, {@link #NEVER_CACHE} or some additive combination
     * of the VARY_BY_XXXX constants defined below.<p>
     * 
     * It not required that models implement this feature, but models with complex meshes that could 
     * be cached should consider doing so.  Similarly, renderers are not required to implement
     * a caching feature.
     */
    default int cacheFlags() {
        return NEVER_CACHE;
    }
    
    /** Model is invariant and can always be safely cached. See {@link #cacheFlags()} */
    public static final int ALWAYS_CACHE = 0;
    
    /** Model output depends on block state input. See {@link #cacheFlags()} */
    public static final int VARY_BY_BLOCKSTATE = 1;
    
    /** Model output depends on random seed (which in turn depends on position). See {@link #cacheFlags()} */
    public static final int VARY_BY_SEED = 2;
    
    /** Model output depends on position values not captured by the random seed. See {@link #cacheFlags()} */
    public static final int VARY_BY_POS = 4;
    
    /** Model output depends on the state of directly adjacent neighbor blocks. See {@link #cacheFlags()} */
    public static final int VARY_BY_ADJACENT = 8;
    
    /** Model output depends on the state of diagonally adjacent neighbor blocks. See {@link #cacheFlags()} */
    public static final int VARY_BY_DIAGONAL = 16;
    
    /** Model output depends on {@link DynamicModelBlockEntity#getDynamicModelData()}. See {@link #cacheFlags()} */
    public static final int VARY_BY_BLOCK_ENTITY = 32;
    
    /** Model output is non-deterministic and should never be cached. See {@link #cacheFlags()} */
    public static final int NEVER_CACHE = -1;
}
