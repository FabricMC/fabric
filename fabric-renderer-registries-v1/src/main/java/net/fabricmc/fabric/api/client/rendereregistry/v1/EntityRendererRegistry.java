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

import net.minecraft.class_5617;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import net.fabricmc.fabric.impl.client.renderer.registry.EntityRendererRegistryImpl;

/**
 * Helper class for registering EntityRenderers.
 */
public interface EntityRendererRegistry {
	EntityRendererRegistry INSTANCE = new EntityRendererRegistryImpl();

	/**
	 * Register a BlockEntityRenderer for a BlockEntityType. Can be called clientside before the world is rendered.
	 *
	 * @param entityType the {@link EntityType} to register a renderer for
	 * @param entityRendererFactory a {@link class_5617} that creates a {@link EntityRenderer}, called
	 *                            when {@link EntityRenderDispatcher} is initialized or immediately if the dispatcher
	 *                            class is already loaded
	 * @param <E> the {@link Entity}
	 */
	<E extends Entity> void register(EntityType<? extends E> entityType, class_5617<E> entityRendererFactory);
}
