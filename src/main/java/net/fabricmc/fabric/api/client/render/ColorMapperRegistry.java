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

package net.fabricmc.fabric.api.client.render;

import net.fabricmc.fabric.impl.client.render.ColorMapperRegistryImpl;
import net.minecraft.block.Block;
import net.minecraft.client.render.block.BlockColorMapper;
import net.minecraft.client.render.item.ItemColorMapper;
import net.minecraft.item.ItemContainer;

public interface ColorMapperRegistry<T, Mapper> {
	ColorMapperRegistry<ItemContainer, ItemColorMapper> ITEMS = ColorMapperRegistryImpl.ITEMS;

	ColorMapperRegistry<Block, BlockColorMapper> BLOCKS = ColorMapperRegistryImpl.BLOCKS;

	/**
	 * Register a color mapper for one or more objects
	 *
	 * @param mapper  The color mapper to register.
	 * @param objects The objects which should be coloured using this mapper.
	 */
	void register(Mapper mapper, T... objects);

	/**
	 * Get a color mapper for the provided object.
	 *
	 * Please note that the underlying registry may not be fully populated or stable until the game has started,
	 * as other mods may overwrite the registry.
	 *
	 * @param object The object to acquire the mapper for.
	 * @return The registered mapper for this mapper, or {@code null} if none is registered or available.
	 */
	Mapper get(T object);
}
