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

package net.fabricmc.fabric.impl.conditionalresource;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.conditionalresource.v1.ResourceConditions;

public class DefaultResourceConditions implements ModInitializer {
	@Override
	public void onInitialize() {
		Registry.register(ResourceConditions.RESOURCE_CONDITION_REGISTRY, new Identifier("fabric:impossible"), ResourceConditions.IMPOSSIBLE);
		Registry.register(ResourceConditions.RESOURCE_CONDITION_REGISTRY, new Identifier("fabric:always"), ResourceConditions.ALWAYS);
		Registry.register(ResourceConditions.RESOURCE_CONDITION_REGISTRY, new Identifier("fabric:boolean"), ResourceConditions.BOOLEAN);
		Registry.register(ResourceConditions.RESOURCE_CONDITION_REGISTRY, new Identifier("fabric:or"), ResourceConditions.OR);
		Registry.register(ResourceConditions.RESOURCE_CONDITION_REGISTRY, new Identifier("fabric:and"), ResourceConditions.AND);
		Registry.register(ResourceConditions.RESOURCE_CONDITION_REGISTRY, new Identifier("fabric:mod_loaded"), ResourceConditions.MOD_LOADED);
	}
}
