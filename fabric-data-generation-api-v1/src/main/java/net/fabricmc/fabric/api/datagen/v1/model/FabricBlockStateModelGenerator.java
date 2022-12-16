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

package net.fabricmc.fabric.api.datagen.v1.model;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.model.builder.ModelBuilder;

/**
 * Fabric-provided extensions for {@link net.minecraft.data.client.BlockStateModelGenerator}.
 *
 * <p>Note: This interface is automatically implemented on all generators via Mixin and interface injection.
 */
public interface FabricBlockStateModelGenerator {
	/**
	 * Create a singleton block state and empty model file for this block. Useful in special cases where a block may
	 * be rendered/modelled entirely through code rather than a standard model.
	 *
	 * @param block The block to register a block state and model for.
	 */
	default void registerEmptyModel(Block block) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	/**
	 * Create a singleton block state and empty model file with the given ID for this block. Useful in special cases
	 * where a block may be rendered/modelled entirely through code rather than a standard model.
	 *
	 * @param block The block to register a block state and model for.
	 * @param id   The ID/path for the generated "model".
	 */
	default void registerEmptyModel(Block block, Identifier id) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	default void buildWithSingletonState(Block block, ModelBuilder<?> builder) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}
}
