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

package net.fabricmc.fabric.impl.client.rendering;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

@Environment(EnvType.CLIENT)
public final class BuiltinItemRendererRegistryImpl implements BuiltinItemRendererRegistry {
	public static final BuiltinItemRendererRegistryImpl INSTANCE = new BuiltinItemRendererRegistryImpl();

	private static final Map<Item, BuiltinItemRenderer> RENDERERS = new HashMap<>();

	private BuiltinItemRendererRegistryImpl() {
	}

	@Override
	public void register(Item item, BuiltinItemRenderer renderer) {
		Objects.requireNonNull(item, "item is null");
		Objects.requireNonNull(renderer, "renderer is null");

		if (RENDERERS.containsKey(item)) {
			throw new IllegalArgumentException("Item " + Registry.ITEM.getId(item) + " already has a builtin renderer!");
		}

		RENDERERS.put(item, renderer);
	}

	/* @Nullable */
	public static BuiltinItemRenderer getRenderer(Item item) {
		return RENDERERS.get(item);
	}
}
