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

import java.util.Objects;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.renderer.registry.item.ItemOverlayExtensions;

@Environment(EnvType.CLIENT)
public final class ItemOverlayRendererRegistry {
	private ItemOverlayRendererRegistry() { }

	public static ItemLabelProperties getCountLabelProperties(Item item) {
		Objects.requireNonNull(item);
		return ((ItemOverlayExtensions) item).fabric_getCountLabelProperties();
	}

	public static void setCountLabelProperties(Item item, ItemLabelProperties properties) {
		Objects.requireNonNull(item);
		Objects.requireNonNull(properties);
		((ItemOverlayExtensions) item).fabric_setCountLabelProperties(properties);
	}

	public static ItemDamageBarInfo getDurabilityBarProperties(Item item) {
		Objects.requireNonNull(item);
		return ((ItemOverlayExtensions) item).fabric_getDurabilityBarProperties();
	}

	public static void setDurabilityBarProperties(Item item, ItemDamageBarInfo properties) {
		Objects.requireNonNull(item);
		Objects.requireNonNull(properties);
		((ItemOverlayExtensions) item).fabric_setDurabilityBarProperties(properties);
	}

	public static ItemCooldownInfo getCooldownOverlayProperties(Item item) {
		return ((ItemOverlayExtensions) item).fabric_getCooldownOverlayProperties();
	}

	public static void setCooldownOverlayProperties(Item item, ItemCooldownInfo properties) {
		Objects.requireNonNull(item);
		Objects.requireNonNull(properties);
		((ItemOverlayExtensions) item).fabric_setCooldownOverlayProperties(properties);
	}

	public static ItemOverlayRenderer.Pre getPreRenderer(Item item) {
		Objects.requireNonNull(item);
		return ((ItemOverlayExtensions) item).fabric_getPreItemOverlayRenderer();
	}

	public static void setPreRenderer(Item item, ItemOverlayRenderer.Pre renderer) {
		Objects.requireNonNull(item);
		Objects.requireNonNull(renderer);
		((ItemOverlayExtensions) item).fabric_setPreOverlayRenderer(renderer);
	}

	public static ItemOverlayRenderer.Post getPostRenderer(Item item) {
		Objects.requireNonNull(item);
		return ((ItemOverlayExtensions) item).fabric_getPostItemOverlayRenderer();
	}

	public static void setPostRenderer(Item item, ItemOverlayRenderer.Post renderer) {
		Objects.requireNonNull(item);
		Objects.requireNonNull(renderer);
		((ItemOverlayExtensions) item).fabric_setPostOverlayRenderer(renderer);
	}

	public static ItemLabelProperties getCountLabelProperties(ItemConvertible itemConvertible) {
		Objects.requireNonNull(itemConvertible);
		return getCountLabelProperties(itemConvertible.asItem());
	}

	public static void setCountLabelProperties(ItemConvertible itemConvertible, ItemLabelProperties properties) {
		Objects.requireNonNull(itemConvertible);
		setCountLabelProperties(itemConvertible.asItem(), properties);
	}

	public static ItemDamageBarInfo getDurabilityBarProperties(ItemConvertible itemConvertible) {
		Objects.requireNonNull(itemConvertible);
		return getDurabilityBarProperties(itemConvertible.asItem());
	}

	public static void setDurabilityBarProperties(ItemConvertible itemConvertible, ItemDamageBarInfo properties) {
		Objects.requireNonNull(itemConvertible);
		setDurabilityBarProperties(itemConvertible.asItem(), properties);
	}

	public static ItemCooldownInfo getCooldownOverlayProperties(ItemConvertible itemConvertible) {
		Objects.requireNonNull(itemConvertible);
		return getCooldownOverlayProperties(itemConvertible.asItem());
	}

	public static void setCooldownOverlayProperties(ItemConvertible itemConvertible, ItemCooldownInfo properties) {
		Objects.requireNonNull(itemConvertible);
		setCooldownOverlayProperties(itemConvertible.asItem(), properties);
	}

	public static ItemOverlayRenderer.Pre getPreRenderer(ItemConvertible itemConvertible) {
		Objects.requireNonNull(itemConvertible);
		return getPreRenderer(itemConvertible.asItem());
	}

	public static void setPreRenderer(ItemConvertible itemConvertible, ItemOverlayRenderer.Pre renderer) {
		Objects.requireNonNull(itemConvertible);
		setPreRenderer(itemConvertible.asItem(), renderer);
	}

	public static ItemOverlayRenderer.Post getPostRenderer(ItemConvertible itemConvertible) {
		Objects.requireNonNull(itemConvertible);
		return getPostRenderer(itemConvertible.asItem());
	}

	public static void setPostRenderer(ItemConvertible itemConvertible, ItemOverlayRenderer.Post renderer) {
		Objects.requireNonNull(itemConvertible);
		setPostRenderer(itemConvertible.asItem(), renderer);
	}
}
