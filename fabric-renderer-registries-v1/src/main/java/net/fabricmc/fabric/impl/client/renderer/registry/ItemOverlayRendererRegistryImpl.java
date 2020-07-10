/*
 * Copyright (c) 2016, 2017, 2018, 2019, 2020 FabricMC
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

package net.fabricmc.fabric.impl.client.renderer.registry;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.ItemOverlayRenderer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.ItemOverlayRendererRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;

public class ItemOverlayRendererRegistryImpl implements ItemOverlayRendererRegistry {
	private static final Object2ObjectLinkedOpenHashMap<Item, ItemOverlayRenderer> OVERLAY_RENDERERS =
	    new Object2ObjectLinkedOpenHashMap<>();

	@Override
	public ItemOverlayRenderer get(ItemConvertible item) {
		return OVERLAY_RENDERERS.get(item.asItem());
	}

	@Override
	public void add(ItemConvertible item, ItemOverlayRenderer overlayRenderer) {
		OVERLAY_RENDERERS.put(item.asItem(), overlayRenderer);
	}

	@Override
	public void remove(ItemConvertible item, ItemOverlayRenderer overlayRenderer) {
		OVERLAY_RENDERERS.remove(item.asItem());
	}
}
