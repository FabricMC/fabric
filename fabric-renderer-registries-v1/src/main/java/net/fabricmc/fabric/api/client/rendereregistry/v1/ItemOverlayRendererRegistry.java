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

package net.fabricmc.fabric.api.client.rendereregistry.v1;

import net.fabricmc.fabric.impl.client.renderer.registry.ItemOverlayRendererRegistryImpl;
import net.minecraft.item.ItemConvertible;

public interface ItemOverlayRendererRegistry {
	ItemOverlayRendererRegistry INSTANCE = new ItemOverlayRendererRegistryImpl();

	ItemOverlayRenderer get(ItemConvertible item);
	void add(ItemConvertible item, ItemOverlayRenderer overlayRenderer);
	void remove(ItemConvertible item, ItemOverlayRenderer overlayRenderer);
}
