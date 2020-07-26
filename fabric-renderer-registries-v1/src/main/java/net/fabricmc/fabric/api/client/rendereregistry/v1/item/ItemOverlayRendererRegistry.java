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

package net.fabricmc.fabric.api.client.rendereregistry.v1.item;

import net.minecraft.item.Item;

import net.fabricmc.fabric.impl.client.renderer.registry.item.ItemOverlayExtensions;

public final class ItemOverlayRendererRegistry {
	private ItemOverlayRendererRegistry() { }

	public static CountLabelProperties getCountLabelProperties(Item item) {
		return ((ItemOverlayExtensions) item).fabric_getCountLabelProperties();
	}

	public static void setCountLabelProperties(Item item, CountLabelProperties clp) {
		((ItemOverlayExtensions) item).fabric_setCountLabelProperties(clp);
	}

	public static DurabilityBarProperties getDurabilityBarProperties(Item item) {
		return ((ItemOverlayExtensions) item).fabric_getDurabilityBarProperties();
	}

	public static void setDurabilityBarProperties(Item item, DurabilityBarProperties dbp) {
		((ItemOverlayExtensions) item).fabric_setDurabilityBarProperties(dbp);
	}

	public static CooldownOverlayProperties getCooldownOverlayProperties(Item item) {
		return ((ItemOverlayExtensions) item).fabric_getCooldownOverlayProperties();
	}

	public static void setCooldownOverlayProperties(Item item, CooldownOverlayProperties cop) {
		((ItemOverlayExtensions) item).fabric_setCooldownOverlayProperties(cop);
	}

	public static PreItemOverlayRenderer getPreRenderer(Item item) {
		return ((ItemOverlayExtensions) item).fabric_getPreItemOverlayRenderer();
	}

	public static void setPreRenderer(Item item, PreItemOverlayRenderer pior) {
		((ItemOverlayExtensions) item).fabric_setPreOverlayRenderer(pior);
	}

	public static PostItemOverlayRenderer getPostRenderer(Item item) {
		return ((ItemOverlayExtensions) item).fabric_getPostItemOveralyRenderer();
	}

	public static void setPostRenderer(Item item, PostItemOverlayRenderer pior) {
		((ItemOverlayExtensions) item).fabric_setPostOverlayRenderer(pior);
	}
}
