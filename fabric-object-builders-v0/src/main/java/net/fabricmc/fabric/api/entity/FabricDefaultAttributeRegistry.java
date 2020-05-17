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

package net.fabricmc.fabric.api.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;

/**
 * @deprecated Please use {@link net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry} instead.
 */
@Deprecated
public final class FabricDefaultAttributeRegistry {
	/**
	 * @deprecated Please {@link net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry#register(EntityType, DefaultAttributeContainer.Builder)} instead.
	 */
	@Deprecated
	public static void register(EntityType<? extends LivingEntity> type, DefaultAttributeContainer.Builder builder) {
		net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(type, builder);
	}
}
