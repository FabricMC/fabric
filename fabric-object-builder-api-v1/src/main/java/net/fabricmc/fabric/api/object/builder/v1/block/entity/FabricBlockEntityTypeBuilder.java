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
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

/**
 * Fabric's version of BlockEntityType.Builder.
 * Removes the need to use an access widener on the BlockEntityFactory
 *
 */
public final class FabricBlockEntityTypeBuilder<T extends BlockEntity> {
	private final Factory<? extends T> factory;
	private final Block[] blocks;

	private FabricBlockEntityTypeBuilder(Factory<? extends T> factory, Block[] blocks) {
		this.factory = factory;
		this.blocks = blocks;
	}

	public static <T extends BlockEntity> FabricBlockEntityTypeBuilder<T> create(Factory<? extends T> factory, Block... blocks) {
		return new FabricBlockEntityTypeBuilder<>(factory, blocks);
	}

	public BlockEntityType<T> build() {
		return build(null);
	}

	public BlockEntityType<T> build(Type<?> type) {
		return BlockEntityType.Builder.<T>create(factory::create, blocks)
				.build(type);
	}

	@FunctionalInterface
	public interface Factory<T extends BlockEntity> {
		T create(BlockPos blockPos, BlockState blockState);
	}
}
