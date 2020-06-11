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

package net.fabricmc.fabric.impl.content.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

import net.fabricmc.fabric.api.content.registry.v1.util.ContentRegistry;
import net.fabricmc.fabric.mixin.content.registry.ShovelItemAccessor;

public class FlattenableBlockRegistryImpl extends ContentRegistryImpl<Block, BlockState> {
	public static final ContentRegistry<Block, BlockState> INSTANCE = new FlattenableBlockRegistryImpl();

	private FlattenableBlockRegistryImpl() {
		super("flattenable_block_registry",
				ShovelItemAccessor.getPathBlockstates()::put, // Putter
				ShovelItemAccessor.getPathBlockstates()::remove, // Remover
				ShovelItemAccessor.getPathBlockstates()::get); // Getter
	}
}
