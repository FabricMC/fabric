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

package net.fabricmc.fabric.mixin.client.renderer.registry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.item.Item;

import net.fabricmc.fabric.api.client.rendereregistry.v1.item.ItemCooldownInfo;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.ItemLabelProperties;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.ItemOverlayRenderer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.ItemDamageBarInfo;
import net.fabricmc.fabric.impl.client.renderer.registry.item.ItemOverlayExtensions;

@Mixin(Item.class)
public abstract class MixinItem implements ItemOverlayExtensions {
	@Unique private ItemLabelProperties itemLabelProperties;
	@Unique private ItemDamageBarInfo itemDamageBarInfo;
	@Unique private ItemCooldownInfo itemCooldownInfo;
	@Unique private ItemOverlayRenderer.Pre preItemOverlayRenderer;
	@Unique private ItemOverlayRenderer.Post postItemOverlayRenderer;

	@Override
	public ItemLabelProperties fabric_getCountLabelProperties() {
		return itemLabelProperties;
	}

	@Override
	public void fabric_setCountLabelProperties(ItemLabelProperties properties) {
		this.itemLabelProperties = properties;
	}

	@Override
	public ItemDamageBarInfo fabric_getDurabilityBarProperties() {
		return itemDamageBarInfo;
	}

	@Override
	public void fabric_setDurabilityBarProperties(ItemDamageBarInfo properties) {
		this.itemDamageBarInfo = properties;
	}

	@Override
	public ItemCooldownInfo fabric_getCooldownOverlayProperties() {
		return itemCooldownInfo;
	}

	@Override
	public void fabric_setCooldownOverlayProperties(ItemCooldownInfo properties) {
		this.itemCooldownInfo = properties;
	}

	@Override
	public ItemOverlayRenderer.Pre fabric_getPreItemOverlayRenderer() {
		return preItemOverlayRenderer;
	}

	@Override
	public void fabric_setPreOverlayRenderer(ItemOverlayRenderer.Pre renderer) {
		this.preItemOverlayRenderer = renderer;
	}

	@Override
	public ItemOverlayRenderer.Post fabric_getPostItemOverlayRenderer() {
		return postItemOverlayRenderer;
	}

	@Override
	public void fabric_setPostOverlayRenderer(ItemOverlayRenderer.Post renderer) {
		this.postItemOverlayRenderer = renderer;
	}
}
