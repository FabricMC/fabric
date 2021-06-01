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

package net.fabricmc.fabric.impl.client.renderer.registry;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

/**
 * Helper class for registering EntityRenderers.
 */
public final class EntityRendererRegistryImpl implements EntityRendererRegistry {
	
	public <T extends Entity> void register(EntityType<? extends T> entityType, EntityRendererFactory<T> factory) {
		net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.INSTANCE.register(entityType, factory);
	}
}
