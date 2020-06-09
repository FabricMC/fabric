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

import net.fabricmc.fabric.api.content.registry.v1.util.ContentRegistry;
import net.fabricmc.fabric.mixin.content.registry.AxeItemAccessor;
import net.minecraft.block.Block;

public class StrippableBlockRegistryImpl extends ContentRegistryImpl<Block, Block> {
	public static final ContentRegistry<Block, Block> INSTANCE = new StrippableBlockRegistryImpl();

	public StrippableBlockRegistryImpl() {
		super("strippable_block_registry",
				AxeItemAccessor.getStrippedBlocks()::put, // Putter
				AxeItemAccessor.getStrippedBlocks()::remove, // Remover
				AxeItemAccessor.getStrippedBlocks()::get); // Getter
	}
}
