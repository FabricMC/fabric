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

package net.fabricmc.fabric.api.client.model.loading.v1;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;

/**
 * Interface for block state resolvers.
 *
 * <p>Block state resolvers are responsible for mapping each {@link BlockState} of a block to an {@link UnbakedModel}.
 * Block state resolvers replace the {@code blockstates/} JSON files.
 * They allow defining custom block state formats for example.
 *
 * <p>If only custom models are needed, {@link ModelResolver} should be used to resolve specific model files.
 */
@FunctionalInterface
public interface BlockStateResolver {
	/**
	 * Resolves the models for all block states of the block.
	 *
	 * <p>For each block state, call {@link Context#setModel} to set its unbaked model.
	 * It must be called exactly once for each block state.
	 *
	 * <p>Note that the unbaked model for each block state will be baked.
	 * If the model is expensive to bake, and many block states share the same model, this can be quite wasteful (as the model will get baked multiple times).
	 * In that case, one can use {@link DelegatingUnbakedModel} to share an underlying model between multiple block states.
	 */
	void resolveBlockStates(Context context);

	/**
	 * The context for block state resolution.
	 */
	@ApiStatus.NonExtendable
	interface Context {
		/**
		 * The block for which the block state models are being resolved.
		 */
		Block block();

		/**
		 * Sets the model for a block state.
		 *
		 * @param state the block state for which this model should be used
		 * @param model the unbaked model for this block state
		 */
		void setModel(BlockState state, UnbakedModel model);

		/**
		 * Loads a model using an {@link Identifier} or {@link ModelIdentifier}, or gets it if it was already loaded.
		 *
		 * <p>Please note that the game engine keeps track of circular model loading calls on its own.
		 *
		 * @param id the model identifier
		 * @return the unbaked model, or a missing model if it is not present
		 */
		UnbakedModel getOrLoadModel(Identifier id);

		/**
		 * The current model loader instance (changes when resource packs reload).
		 *
		 * <p>Do <b>not</b> call {@link ModelLoader#getOrLoadModel} as it doesn't supported nested model resolution,
		 * use {@link #getOrLoadModel} from the context instead.
		 */
		ModelLoader loader();
	}
}
