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
import net.minecraft.item.ItemConvertible;

import net.fabricmc.fabric.impl.client.renderer.registry.item.ItemOverlayExtensions;

public final class ItemOverlayRendererRegistry {
	private ItemOverlayRendererRegistry() { }

	public static CountLabelProperties getCountLabelProperties(Item item) {
		return ((ItemOverlayExtensions) item).fabric_getCountLabelProperties();
	}

	public static void setCountLabelProperties(Item item, CountLabelProperties properties) {
		((ItemOverlayExtensions) item).fabric_setCountLabelProperties(properties);
	}

	public static DurabilityBarProperties getDurabilityBarProperties(Item item) {
		return ((ItemOverlayExtensions) item).fabric_getDurabilityBarProperties();
	}

	public static void setDurabilityBarProperties(Item item, DurabilityBarProperties properties) {
		((ItemOverlayExtensions) item).fabric_setDurabilityBarProperties(properties);
	}

	public static CooldownOverlayProperties getCooldownOverlayProperties(Item item) {
		return ((ItemOverlayExtensions) item).fabric_getCooldownOverlayProperties();
	}

	public static void setCooldownOverlayProperties(Item item, CooldownOverlayProperties properties) {
		((ItemOverlayExtensions) item).fabric_setCooldownOverlayProperties(properties);
	}

	public static PreItemOverlayRenderer getPreRenderer(Item item) {
		return ((ItemOverlayExtensions) item).fabric_getPreItemOverlayRenderer();
	}

	public static void setPreRenderer(Item item, PreItemOverlayRenderer renderer) {
		((ItemOverlayExtensions) item).fabric_setPreOverlayRenderer(renderer);
	}

	public static PostItemOverlayRenderer getPostRenderer(Item item) {
		return ((ItemOverlayExtensions) item).fabric_getPostItemOverlayRenderer();
	}

	public static void setPostRenderer(Item item, PostItemOverlayRenderer renderer) {
		((ItemOverlayExtensions) item).fabric_setPostOverlayRenderer(renderer);
	}

	public static CountLabelProperties getCountLabelProperties(ItemConvertible itemConvertible) {
		return getCountLabelProperties(itemConvertible.asItem());
	}

	public static void setCountLabelProperties(ItemConvertible itemConvertible, CountLabelProperties properties) {
		setCountLabelProperties(itemConvertible.asItem(), properties);
	}

	public static DurabilityBarProperties getDurabilityBarProperties(ItemConvertible itemConvertible) {
		return getDurabilityBarProperties(itemConvertible.asItem());
	}

	public static void setDurabilityBarProperties(ItemConvertible itemConvertible, DurabilityBarProperties properties) {
		setDurabilityBarProperties(itemConvertible.asItem(), properties);
	}

	public static CooldownOverlayProperties getCooldownOverlayProperties(ItemConvertible itemConvertible) {
		return getCooldownOverlayProperties(itemConvertible.asItem());
	}

	public static void setCooldownOverlayProperties(ItemConvertible itemConvertible, CooldownOverlayProperties properties) {
		setCooldownOverlayProperties(itemConvertible.asItem(), properties);
	}

	public static PreItemOverlayRenderer getPreRenderer(ItemConvertible itemConvertible) {
		return getPreRenderer(itemConvertible.asItem());
	}

	public static void setPreRenderer(ItemConvertible itemConvertible, PreItemOverlayRenderer renderer) {
		setPreRenderer(itemConvertible.asItem(), renderer);
	}

	public static PostItemOverlayRenderer getPostRenderer(ItemConvertible itemConvertible) {
		return getPostRenderer(itemConvertible.asItem());
	}

	public static void setPostRenderer(ItemConvertible itemConvertible, PostItemOverlayRenderer renderer) {
		setPostRenderer(itemConvertible.asItem(), renderer);
	}
}
