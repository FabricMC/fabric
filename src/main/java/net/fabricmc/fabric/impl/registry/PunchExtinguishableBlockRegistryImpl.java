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

package net.fabricmc.fabric.impl.registry;

import net.fabricmc.fabric.api.registry.PunchExtinguishableBlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;

import java.util.HashMap;
import java.util.Map;

public final class PunchExtinguishableBlockRegistryImpl implements PunchExtinguishableBlockRegistry {
	public static final PunchExtinguishableBlockRegistryImpl INSTANCE = new PunchExtinguishableBlockRegistryImpl();

	private final Map<Block, Boolean> map = new HashMap<>();

	private PunchExtinguishableBlockRegistryImpl() {}

	@Override
	public Boolean get(Block block) {
		return map.getOrDefault(block, false);
	}

	@Override
	public void add(Block block, Boolean value) {
		map.put(block, value);
	}

	@Override
	public void add(Tag<Block> tag, Boolean value) {
		throw new UnsupportedOperationException("Tags currently not supported!");
	}

	@Override
	public void remove(Block block) {
		map.remove(block);
	}

	@Override
	public void remove(Tag<Block> tag) {
		throw new UnsupportedOperationException("Tags currently not supported!");
	}
}
