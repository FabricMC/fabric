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
	
	import net.minecraft.world.BlockView;
	
	/**
	 * BlockView-extending interface to be used by FabricBakedModel for model
	 * customization. It ensures thread safety and exploits data cached in render 
	 * chunks for performance and data consistency.  <p>
	 * 
	 * There are two primary differences from BlockView consumers must understand:<p>
	 * 
	 * <li> {@link #getBlockEntity(net.minecraft.util.math.BlockPos)} will ALWAYS
	 * return null when the view is accessed outside the main client thread.
	 * Models that require access to Block Entity data at render time must implement
	 * FabricDynamicBakedModel and retrieve main-thread results via {@link #getCachedRenderData()}.</li><p>
	 * 
	 * <li> {@link #getBlockState(net.minecraft.util.math.BlockPos)} and {@link #getFluidState(net.minecraft.util.math.BlockPos)}
	 * will always reflect the state cached with the render chunk.  Block and fluid states
	 * can thus be different from main-thread world state due to lag between block update
	 * application from network packets and render chunk rebuilds. This ensures consistency
	 * of model state with the rest of the chunk being rendered.</li><p>
	 *
	 * This interface is only guaranteed to be present in the client environment.
	 */
	public interface RenderCacheView extends BlockView {
		
		/**
		 * For models that implement FabricDynamicBakedModel, this will be the most
		 * recent value provided by the implementation for the current block position.<p>

		 * Always null outside of block rendering (items, damage models, etc).
		 * Always null for models that do not implement FabricDynamicBakedModel or 
		 * if data has not been provided by the model.
		 *
		 * @param <T> The render data type specific to the consuming model.
		 */
		<T> T getCachedRenderData();
		
	}