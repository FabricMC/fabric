/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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

package net.fabricmc.fabric.api.rendering.data.v1;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;

/**
 * Do not use.  Read on for explanation and alternative.<p>
 * 
 * This module was meant to ensure thread-safe access to BlockEntity state
 * needed by chunk rebuilds that may run off the main client thread.<p>
 * 
 * Read access to block entities in render world views is already thread-safe,
 * or at least safe enough - a {@code HashMap.get()} call will, at worst, give 
 * a result that is inconsistent with the block state being rendered.<p>
 * 
 * Earlier implementations tried to avoid that problem by capturing the block entity 
 * render state on the client thread while each chunk render cache was built
 * and preserving that state for later use.<p>
 * 
 * Unfortunately, there is nothing that prevents the per-chunk block entity map
 * from being modified <em>while</em> the cache is being built. This caused
 * intermittent {@code ConcurrentModificationException} crashes. The most common
 * cause for this is a common rendering optimization mod that moves chunk rebuilds
 * off the main client thread.  This works in vanilla without synchronization because
 * all {@code BlockEntity} map <em>updates</em> are still happening on the main thread
 * and the vanilla chunk cache creation code does not iterate the {@code BlockEntity} map.<p>
 * 
 * It would be possible to work around this problem by building our state cache using 
 * per-position queries instead of iterating the map, but this introduces an additional
 * performance burden and, more importantly, does not solve the original problem: the resulting
 * render state derived from block entities may be inconsistent with the block states in the 
 * render cache!  In other words, it defeats the primary purpose of this module.<p>
 * 
 * The same would be true for an approach that alters the vanilla data structures to use
 * {@code ConcurrentHashMap} because that a concurrent map (by design) does not guarantee 
 * a consistent view during iteration. <p>
 * 
 * Fully synchronizing all map access would work, but doing so would be highly invasive
 * and likely brittle and incompatible with mods that rely on the current vanilla structures.<p>
 *
 * This mean model implementations that rely on {@code BlockEntity} state must check for nulls, 
 * check for inconsistency with the block state in the world view and be well-guarded generally.<p>
 * 
 * Given that these precautions must be taken in any case, this module adds needless complexity.<p>
 * 
 * Mods that previously called:
 * 
 * <pre>
 *     ((RenderAttachedBlockView)blockView).getBlockEntityRenderAttachment(pos)</pre>
 * 
 * should now instead simply use:
 * 
 * <pre>
 *     blockView.getBlockEntity(pos)</pre>
 * 
 * ...and take the aformentioned precautions in designing and retrieving state to be used during rendering.
 */
@Deprecated
public interface RenderAttachedBlockView extends ExtendedBlockView {
	/**
	 * Do not use.<p>
	 * 
	 * See header for explanation and recommended alternative.<p>
	 */
	@Deprecated
    default Object getBlockEntityRenderAttachment(BlockPos pos) {
        BlockEntity be = this.getBlockEntity(pos);
        return be == null ? null : ((RenderAttachmentBlockEntity) be).getRenderAttachmentData();
    }
}
