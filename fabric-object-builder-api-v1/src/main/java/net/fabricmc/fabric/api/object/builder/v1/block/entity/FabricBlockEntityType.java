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

package net.fabricmc.fabric.api.object.builder.v1.block.entity;

import com.mojang.datafixers.types.Type;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

/**
 * General-purpose Fabric-provided extensions for {@link BlockEntityType}.
 *
 * <p>Note: This interface is automatically implemented on {@link BlockEntityType} via Mixin and interface injection.
 */
public interface FabricBlockEntityType {
	/**
	 * Adds a block to the list of blocks that this block entity type can be used with.
	 *
	 * @param block the {@link Block} to add
	 */
	default void addSupportedBlock(Block block) {
		throw new AssertionError("Implemented in Mixin");
	}

	/**
	 * General-purpose Fabric-provided extensions for {@link BlockEntityType.Builder}.
	 *
	 * <p>Note: This interface is automatically implemented on {@link BlockEntityType.Builder} via Mixin and interface injection.
	 */
	interface Builder<T extends BlockEntity> {
		/**
		 * Builds the {@link BlockEntityType}, see {@link BlockEntityType.Builder#build(Type)}.
		 *
		 * @return the built {@link BlockEntityType}
		 */
		default BlockEntityType<T> build() {
			throw new AssertionError("Implemented in Mixin");
		}
	}
}
