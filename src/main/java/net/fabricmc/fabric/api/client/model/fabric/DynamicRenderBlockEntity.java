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

import net.minecraft.block.entity.BlockEntity;


/**
 * When a {@link ModelRenderer} is present, this interface enables performant
 * batched rendering of block models that require frequent buffering.<p>
 * 
 * This feature will only be active when all the following conditions are met:
 * <li>A @{@link ModelRenderer} is active. This can be tested via {@link ModelRendererAccess#isRendererActive()}</li>
 * <li>The block to be rendered has an associated {@link BlockEntity}</li> 
 * <li>The {@link BlockEntity} implements this interface.</li>
 * <li>{@link #isDynamicRenderSupported()} returns true</li><p>
 * 
 * When these conditions are met, the renderer will do the following:
 * <li>At least once per client tick (20X per second) the plug-in will call
 * {@link DynamicRenderBlockEntity#checkForRenderUpdate()} from the main client thread.</li>
 * <li>If the call returns a non-null result, the renderer will call {@link DynamicRenderBlockEntity#produceModelVertexData()}, 
 * passing the result as input. <em>This call may occur on a thread other than the main client thread,</em> 
 * depending on the renderer implementation.</li>
 * <li>The renderer will buffer the provided quads and render them for each subsequent frame until {@link DynamicRenderBlockEntity#checkForRenderUpdate(int)} 
 * and returns a non-null result. When that happens, the process repeats.</li><p>
 * 
 * <b>Notes for {@link DynamicRenderBlockEntity} implementors:</b><p>
 *
 * Any block model associated with the block entity will continue to render.  The quads provided
 * via {@link DynamicRenderBlockEntity#produceModelVertexData()} are additive. Similarly, models
 * created with {@link ModelBuilder#buildDynamic()} will have their dynamic quads buffered during
 * chunk rebuild.<p>
 * 
 *  If your model has some parts that are static, some that depend on nearby world state, and some
 *  parts that change each tick or frame (as is often the case with machine blocks), then you will
 *  get best performance by sending the static quads to the model builder before the dynamic model
 *  is built, and including any quads that don't require per-frame or per-tick updates in the the
 *  dynamic block model.<p>
 * 
 * Obviously, {@link DynamicRenderBlockEntity#produceModelVertexData()} will be called frequently.
 * Models for fast rendering should be simple or otherwise implement caching or other optimizations
 * to ensure good performance.<p>
 * 
 * As noted above, model output may occur off the main client thread. This is why no block view parameter is 
 * available during the call to .  The block entity must gather any and all model state that relies on 
 * world state during the call to {@link DynamicModelBlockEntity#getModelData()}
 * and persist that state in the render data returned by that method.<p>
 * 
 * The renderer will <em>not</em> prevent any BlockEntityRenderer associated with 
 * the BlockEntity from being rendered. But there is no requirement that a BlockEntity using this feature have 
 * a specific BlockEntityRenderer associated with it.<p>
 * 
 * 
 * <b>Notes for {@link ModelRenderer} implementors:</b><p>
 * 
 * The Fabric API causes {@link BlockEntity} to implement both this interface
 * and {@link DynamicModelBlockEntity}, with {@link #isDynamicRenderSupported()}
 * returning false by default.  This means a renderer can (and should) always safely 
 * cast any {@link BlockEntity} reference to {@link DynamicRenderBlockEntity} and
 * check {@link #isDynamicRenderSupported()} without <code>instanceof</code> tests.<p>
 * 
 * The renderer implementation is responsible for <em>all</em> other contracts created
 * by this interface. The Fabric API includes no hooks for this purpose in order
 * to give renderer authors complete flexibility in approach and feature support.
 */
public interface DynamicRenderBlockEntity {
    /**
     * Must be true for fast block entity rendering to be enabled.
     * Does not need to check for an active render plug-in.  
     */
    boolean isDynamicRenderSupported();
    
    /**
     * Called once per client tick, or per-frame if {@link #checkEveryFrame()} is true.
     * If the result is true, the render plug-in will re-buffer the model
     * associated with this Block/BlockEntity and render the results for
     * all subsequent frames until the next true result.<p>
     * 
     * When called, the implementing BlockEntity is responsible for all checks
     * to determine the need for an updated render and for updating any counters
     * or flags it uses for this purpose.<p>
     * 
     * This method will (hopefully) only be called when the block is potentially visibly 
     * in the scene currently being rendered, and different plug-ins could have different
     * algorithms for determine visibility. For this reason, calls to this method
     * may have irregular timing and should never be relied on as a signal for
     * any other purpose.
     * 
     * @param tickCounter  The renderer will send a value that increases 
     * every client tick. The counter is globally updated - not specific to this
     * BlockEntity. The counter may or may not be associated with any other
     * Minecraft counter and should only be used to determine if a refresh is required.<p>
     *  
     * @param fractionalTick  Indicates how much of the current client tick in progress has 
     * elapsed.  Intended for per-frame dynamic renders with time-dependent animations.<p>
     * 
     * @param forceRefresh  True if the renderer needs the model to re-buffer quads even if
     * the block entity state is current.  May be used, for example, if user presses F3 + A to
     * force a render reload. <p>
     * 
     * @return  Non-null object if the dynamic quads for this BlockEntity should be re-buffered.
     * Renderers must not use or retain this result except to pass it back to the BlockEntity
     * in {@link #produceModelVertexData()}.  Implementations may safely reuse state objects to
     * prevent wasteful memory allocation.
     */
    Object getDynamicRenderData(int tickCounter, float fractionalTick, boolean forceRefresh);
    
    /**
     * Some animated blocks may benefit from per-frame model updates. Those blocks
     * may override this method to check for render updates each frame.
     * This imposes a performance burden on the renderer and should be used sparingly.
     */
    default boolean checkEveryFrame() {
        return false;
    }
    
    /**
     * Accepts dynamic model content. See {@link ModelVertexConsumer} for details.
     */
    void produceDynamicVertexData(Object modelState, ModelVertexConsumer consumer);
}
