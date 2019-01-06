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

package net.fabricmc.fabric.api.client.model;

import net.fabricmc.fabric.api.client.render.RenderConfiguration;
import net.fabricmc.fabric.api.client.render.RenderPlugin;
import net.minecraft.block.entity.BlockEntity;

/**
 * When a {@link RenderPlugin} is present and the plug-in supports
 * this feature, this interface enables performant batched 
 * rendering of block models that require frequent buffering.<p>
 * 
 * This feature will only be active when all the following conditions are met:
 * <li>A @{@link RenderPlugin} is active. This can be tested via {@link RenderConfiguration#isRenderPluginActive()}</li>
 * <li>{@link RenderPlugin#isFastBlockEntityRenderSupported()} returns true</li>
 * <li>The block to be rendered has an associated {@link BlockEntity}</li> 
 * <li>The {@link BlockEntity} implements this interface.</li>
 * <li>The {@link BlockEntity} also implements {@link RenderDataProvidingBlockEntity}</li>
 * <li>{@link #isFastRenderSupported()} returns true</li><p>
 * 
 * When these conditions are met, the plug-in will do the following:
 * <li>At least once per client tick (20X per second) the plug-in will call
 * {@link FastRenderableBlockEntity#checkForRenderUpdate(int)} from the main client thread.</li>
 * <li>If that method returns true, the plug-in will then call {@link RenderDataProvidingBlockEntity#getRenderData()},
 * also from the main client thread.</li>
 * <li>The plug-in will retrieve (or will already have) the {@link FabricBakedModel} for the block
 * and current block state associated with the block entity. This action will not be
 * visible to the block or block entity.</li>
 * <li>The plug-in will call {@link FabricBakedModel#produceFabricBakedQuads(ModelBlockView, net.minecraft.block.BlockState, net.minecraft.util.math.BlockPos, java.util.Random, long, java.util.function.Consumer)}
 * with the render data returned by the block entity. <em>The ModelBlockView parameter will be null.</em>  
 * This call <em>may occur on a thread other than the main client thread,</em> depending on the plug-in.</li>
 * <li>The plug-in will buffer the quads returned by {@link FabricBakedModel#produceFabricBakedQuads()} and
 * render them for each subsequent frame until {@link FastRenderableBlockEntity#checkForRenderUpdate(int)} returns true.
 * When that happens, the process repeats.</li><p>
 * 
 * <b>Notes for {@link FastRenderableBlockEntity} implementors:</b><p>
 *
 * The plug-in is expected to render the model as it would any other.  If the plug-in supports 
 * emissive lighting, layered textures,  or custom shaders, those features should also work in this context.<p>
 * 
 * However, {@link FabricBakedModel#produceFabricBakedQuads()} will be called much more frequently.
 * Models for fast rendering should be simple or otherwise rely on caching or other optimizations
 * to ensure good performance.<p>
 * 
 * As noted above, model output may occur off the main client thread. This is why the block view parameter to
 * {@link FabricBakedModel#produceFabricBakedQuads()} will be null.  The block entity must gather
 * any and all model state that relies on world state during the call to {@link RenderDataProvidingBlockEntity#getRenderData()}
 * and persist that state in the render data returned by that method.<p>
 * 
 * When this feature is active, the plug-in will <em>not</em> prevent any BlockEntityRenderer associated with 
 * the BlockEntity from being rendered. But there is no requirement that a BlockEntity using this feature have 
 * a specific BlockEntityRenderer associated with it.<p>
 * 
 * Mods that rely on this feature for rendering need to consider how their blocks will render when a
 * RenderPlugin is not present, or when the active plug-in does not support this feature.  One strategy
 * is to implement a fallback render in a BlockEntityRenderer and then disable that render when this feature is available.
 * A simpler but less inclusive approach is to make support for this feature a requirement to run the mod.<p>
 * 
 * The render plug-in is likely to retrieve the baked block model only once per render chunk rebuild and cache it. 
 * This should not cause a problem unless there are hacks causing the block model dispatcher to return different 
 * models for the same block state. Such approaches are not recommended, with or without this feature.<p>
 * 
 * 
 * <b>Notes for {@link RenderPlugin} implementors:</b><p>
 * 
 * The Fabric API causes {@link BlockEntity} to implement both this interface
 * and {@link RenderDataProvidingBlockEntity}, with {@link #isFastRenderSupported()}
 * returning false by default.  This means a plug-in can (and should) always safely 
 * cast any {@link BlockEntity} reference to {@link FastRenderableBlockEntity} and
 * check {@link #isFastRenderSupported()} without <code>instanceof</code> tests.<p>
 * 
 * The plug-in is responsible for implementing <em>all</em> other contracts created
 * by this interface. The Fabric API includes no hooks for this purpose in order
 * to give plug-in authors complete flexibility in approach and feature support.
 * Plug-ins are encouraged but not required to support this interface.
 */
public interface FastRenderableBlockEntity {
    
    /**
     * Must be true for fast block entity rendering to be enabled.
     * Does not need to check for an active render plug-in.  
     */
    boolean isFastRenderSupported();
    
    /**
     * Called once per client tick, unless some other frequency is
     * requested and honored by the active render plug-in via {@link #bestRefreshRate()}.
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
     * @param tickCounter  The render plug-in will send a value that increases 
     * every client tick. The counter is globally updated - not specific to this
     * BlockEntity. The counter may or may not be associated with any other
     * Minecraft counter and should only be used to determine if a refresh is required.<p>
     *  
     * @return  True if the block model for this BlockEntity should be re-buffered.
     */
    boolean checkForRenderUpdate(int tickCounter);
    
    /**
     * Some animated blocks may benefit from more frequent model updates. Those blocks
     * may override this method to activate special handling by the rendering plug-in.
     * However, a render plug-in is not required to implement this feature.<p>
     * 
     * Values higher than 20 mean a more frequent refresh is requested,
     * specifically {@link #bestRefreshRate()}/second, up to the maximum frame rate.<p>
     *
     * For obvious practical reasons, plug-ins will not refresh models more
     * frequently than once per frame.  Setting this value to 1000 or {@link Integer#MAX_VALUE}
     * is functionally equivalent to requesting a refresh once every frame,
     * and should be handled in that way by plug-in that support this feature.<p>
     * 
     * Values less than 20 are ignored - the Block Entity is checked every client tick.
     * Block Entities that need less frequent refreshes can easily maintain an 
     * internal counter and compare the incoming tick counter in {@link #checkForRenderUpdate(int)}
     * to skip frames until a set number of ticks has elapsed.
     */
    default int bestRefreshRate() {
        return 20;
    }
}
