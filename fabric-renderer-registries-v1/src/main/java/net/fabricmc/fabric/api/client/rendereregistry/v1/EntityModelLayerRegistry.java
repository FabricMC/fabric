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

package net.fabricmc.fabric.api.client.rendereregistry.v1;

import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A helpers for registering entity model layers and providers for the layer's textured model data.
 *
 * @deprecated This module has been moved into fabric-rendering-v1. Use {@link net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry} instead.
 */
@Deprecated
@Environment(EnvType.CLIENT)
public final class EntityModelLayerRegistry {
	/**
	 * Registers an entity model layer and registers a provider for a {@linkplain TexturedModelData}.
	 *
	 * @param modelLayer the entity model layer
	 * @param provider the provider for the textured model data
	 */
	public static void registerModelLayer(EntityModelLayer modelLayer, TexturedModelDataProvider provider) {
		net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry.registerModelLayer(modelLayer, provider::createModelData);
	}

	private EntityModelLayerRegistry() {
	}

	@FunctionalInterface
	@Deprecated
	@Environment(EnvType.CLIENT)
	public interface TexturedModelDataProvider {
		/**
		 * Creates the textured model data for use in a {@link EntityModelLayer}.
		 *
		 * @return the textured model data for the entity model layer.
		 */
		TexturedModelData createModelData();
	}
}
