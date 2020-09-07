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
import net.fabricmc.fabric.impl.client.renderer.registry.item.ItemOverlayMaps;

@Environment(EnvType.CLIENT)
public final class ItemOverlayRendererRegistry {
	private ItemOverlayRendererRegistry() { }

	public static void setLabelInfo(Item item, ItemLabelInfo info) {
		Objects.requireNonNull(item);
		Objects.requireNonNull(info);
		ItemOverlayMaps.LABEL_INFO_MAP.put(item, info);
	}

	public static void setDamageBarInfo(Item item, ItemDamageBarInfo info) {
		Objects.requireNonNull(item);
		Objects.requireNonNull(info);
		ItemOverlayMaps.DAMAGE_BAR_INFO_MAP.put(item, info);
	}

	public static void setCooldownOverlayInfo(Item item, ItemCooldownOverlayInfo info) {
		Objects.requireNonNull(item);
		Objects.requireNonNull(info);
		ItemOverlayMaps.COOLDOWN_OVERLAY_INFO_MAP.put(item, info);
	}

	public static void setPreRenderer(Item item, ItemOverlayRenderer.Pre renderer) {
		Objects.requireNonNull(item);
		Objects.requireNonNull(renderer);
		ItemOverlayMaps.PRE_RENDERER_MAP.put(item, renderer);
	}

	public static void setPostRenderer(Item item, ItemOverlayRenderer.Post renderer) {
		Objects.requireNonNull(item);
		Objects.requireNonNull(renderer);
		ItemOverlayMaps.POST_RENDERER_MAP.put(item, renderer);
	}

	public static void setLabelInfo(ItemConvertible itemConvertible, ItemLabelInfo info) {
		Objects.requireNonNull(itemConvertible);
		setLabelInfo(itemConvertible.asItem(), info);
	}

	public static void setDamageBarInfo(ItemConvertible itemConvertible, ItemDamageBarInfo info) {
		Objects.requireNonNull(itemConvertible);
		setDamageBarInfo(itemConvertible.asItem(), info);
	}

	public static void setCooldownOverlayInfo(ItemConvertible itemConvertible, ItemCooldownOverlayInfo info) {
		Objects.requireNonNull(itemConvertible);
		setCooldownOverlayInfo(itemConvertible.asItem(), info);
	}

	public static void setPreRenderer(ItemConvertible itemConvertible, ItemOverlayRenderer.Pre renderer) {
		Objects.requireNonNull(itemConvertible);
		setPreRenderer(itemConvertible.asItem(), renderer);
	}

	public static void setPostRenderer(ItemConvertible itemConvertible, ItemOverlayRenderer.Post renderer) {
		Objects.requireNonNull(itemConvertible);
		setPostRenderer(itemConvertible.asItem(), renderer);
	}

	public static void setDefaultLabelInfo(ItemLabelInfo info) {
		Objects.requireNonNull(info);
		ItemOverlayMaps.LABEL_INFO_MAP.defaultReturnValue(info);
	}

	public static void setDefaultDamageBarInfo(ItemDamageBarInfo info) {
		Objects.requireNonNull(info);
		ItemOverlayMaps.DAMAGE_BAR_INFO_MAP.defaultReturnValue(info);
	}

	public static void setDefaultCooldownOverlayInfo(ItemCooldownOverlayInfo info) {
		Objects.requireNonNull(info);
		ItemOverlayMaps.COOLDOWN_OVERLAY_INFO_MAP.defaultReturnValue(info);
	}

	public static void setDefaultPreRenderer(ItemOverlayRenderer.Pre renderer) {
		Objects.requireNonNull(renderer);
		ItemOverlayMaps.PRE_RENDERER_MAP.defaultReturnValue(renderer);
	}

	public static void setDefaultPostRenderer(ItemOverlayRenderer.Post renderer) {
		Objects.requireNonNull(renderer);
		ItemOverlayMaps.POST_RENDERER_MAP.defaultReturnValue(renderer);
	}
}
