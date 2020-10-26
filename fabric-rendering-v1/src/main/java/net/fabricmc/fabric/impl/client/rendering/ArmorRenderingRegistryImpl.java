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

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderingRegistry;

public final class ArmorRenderingRegistryImpl {
	private ArmorRenderingRegistryImpl() {
	}

	public static void registerModel(ArmorRenderingRegistry.ModelProvider provider, Iterable<Item> items) {
		Objects.requireNonNull(items);

		for (Item item : items) {
			Objects.requireNonNull(item);

			((ArmorProviderExtensions) item).fabric_setArmorModelProvider(provider);
		}
	}

	public static void registerTexture(ArmorRenderingRegistry.TextureProvider provider, Iterable<Item> items) {
		Objects.requireNonNull(items);

		for (Item item : items) {
			Objects.requireNonNull(item);

			((ArmorProviderExtensions) item).fabric_setArmorTextureProvider(provider);
		}
	}

	public static BipedEntityModel<LivingEntity> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, BipedEntityModel<LivingEntity> defaultModel) {
		if (!stack.isEmpty()) {
			ArmorRenderingRegistry.ModelProvider provider = ((ArmorProviderExtensions) stack.getItem()).fabric_getArmorModelProvider();

			if (provider != null) {
				return provider.getArmorModel(entity, stack, slot, defaultModel);
			}
		}

		return defaultModel;
	}

	public static Identifier getArmorTexture(LivingEntity entity, ItemStack stack, EquipmentSlot slot, boolean secondLayer, @Nullable String suffix, Identifier defaultTexture) {
		if (!stack.isEmpty()) {
			ArmorRenderingRegistry.TextureProvider provider = ((ArmorProviderExtensions) stack.getItem()).fabric_getArmorTextureProvider();

			if (provider != null) {
				return provider.getArmorTexture(entity, stack, slot, secondLayer, suffix, defaultTexture);
			}
		}

		return defaultTexture;
	}
}
