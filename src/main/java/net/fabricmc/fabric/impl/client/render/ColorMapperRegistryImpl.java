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

package net.fabricmc.fabric.impl.client.render;

import net.fabricmc.fabric.api.client.render.ColorMapperRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.render.block.BlockColorMap;
import net.minecraft.client.render.block.BlockColorMapper;
import net.minecraft.client.render.item.ItemColorMap;
import net.minecraft.client.render.item.ItemColorMapper;
import net.minecraft.item.ItemContainer;

import java.util.IdentityHashMap;
import java.util.Map;

public abstract class ColorMapperRegistryImpl<T, Mapper, Underlying> implements ColorMapperRegistry<T, Mapper> {
	public static final ColorMapperRegistryImpl<Block, BlockColorMapper, BlockColorMap> BLOCKS = new ColorMapperRegistryImpl<Block, BlockColorMapper, BlockColorMap>() {
		@Override
		void registerUnderlying(BlockColorMap map, BlockColorMapper mapper, Block block) {
			map.register(mapper, block);
		}
	};

	public static final ColorMapperRegistryImpl<ItemContainer, ItemColorMapper, ItemColorMap> ITEMS = new ColorMapperRegistryImpl<ItemContainer, ItemColorMapper, ItemColorMap>() {
		@Override
		void registerUnderlying(ItemColorMap map, ItemColorMapper mapper, ItemContainer block) {
			map.method_1708(mapper, block);
		}
	};

	private Underlying colorMap;
	private Map<T, Mapper> tempMappers = new IdentityHashMap<>();

	abstract void registerUnderlying(Underlying colorMap, Mapper mapper, T objects);

	public void initialize(Underlying colorMap) {
		if (this.colorMap != null) {
			if (this.colorMap != colorMap) throw new IllegalStateException("Cannot set colorMap twice");
			return;
		}

		this.colorMap = colorMap;
		for (Map.Entry<T, Mapper> mappers : tempMappers.entrySet()) {
			registerUnderlying(colorMap, mappers.getValue(), mappers.getKey());
		}
		tempMappers = null;
	}

	@Override
	@SafeVarargs
	public final void register(Mapper mapper, T... objects) {
		if (colorMap != null) {
			for (T object : objects) registerUnderlying(colorMap, mapper, object);
		} else {
			for (T object : objects) tempMappers.put(object, mapper);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Mapper get(T object) {
		return colorMap == null ? null : ((ColorMapperHolder<T, Mapper>) colorMap).get(object);
	}

	public interface ColorMapperHolder<T, Mapper> {
		Mapper get(T item);
	}
}
