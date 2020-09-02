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

package net.fabricmc.fabric.impl.client.renderer.registry.item;

import net.fabricmc.fabric.api.client.rendereregistry.v1.item.ItemCooldownInfo;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.ItemLabelInfo;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.ItemOverlayRenderer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.ItemDamageBarInfo;

public interface ItemOverlayExtensions {
	ItemLabelInfo fabric_getCountLabelProperties();
	void fabric_setCountLabelProperties(ItemLabelInfo properties);

	ItemDamageBarInfo fabric_getDurabilityBarProperties();
	void fabric_setDurabilityBarProperties(ItemDamageBarInfo properties);

	ItemCooldownInfo fabric_getCooldownOverlayProperties();
	void fabric_setCooldownOverlayProperties(ItemCooldownInfo properties);

	ItemOverlayRenderer.Pre fabric_getPreItemOverlayRenderer();
	void fabric_setPreOverlayRenderer(ItemOverlayRenderer.Pre renderer);

	ItemOverlayRenderer.Post fabric_getPostItemOverlayRenderer();
	void fabric_setPostOverlayRenderer(ItemOverlayRenderer.Post renderer);
}
