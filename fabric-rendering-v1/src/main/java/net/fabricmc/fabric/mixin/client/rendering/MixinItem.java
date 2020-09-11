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

package net.fabricmc.fabric.mixin.client.rendering;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.item.Item;

import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderingRegistry;
import net.fabricmc.fabric.impl.client.rendering.ArmorProviderExtensions;

@Mixin(Item.class)
public class MixinItem implements ArmorProviderExtensions {
	@Unique
	private ArmorRenderingRegistry.ModelProvider armorModelProvider;
	@Unique
	private ArmorRenderingRegistry.TextureProvider armorTextureProvider;

	@Override
	public ArmorRenderingRegistry.ModelProvider fabric_getArmorModelProvider() {
		return armorModelProvider;
	}

	@Override
	public ArmorRenderingRegistry.TextureProvider fabric_getArmorTextureProvider() {
		return armorTextureProvider;
	}

	@Override
	public void fabric_setArmorModelProvider(ArmorRenderingRegistry.ModelProvider provider) {
		armorModelProvider = provider;
	}

	@Override
	public void fabric_setArmorTextureProvider(ArmorRenderingRegistry.TextureProvider provider) {
		armorTextureProvider = provider;
	}
}
