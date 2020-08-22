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

package net.fabricmc.fabric.test.dynamicregistry;

import java.util.List;

import net.fabricmc.fabric.api.dynamicregistry.v1.CustomDynamicRegistry;
import net.fabricmc.fabric.api.dynamicregistry.v1.DynamicRegistryProvider;
import net.fabricmc.fabric.test.dynamicregistry.tater.Tater;

public class TestDynamicRegistryProvider implements DynamicRegistryProvider {
	public void getDynamicRegistries(List<CustomDynamicRegistry<?>> entries) {
		entries.add(new CustomDynamicRegistry<>(DynamicRegistriesTestMod.TATER_REGISTRY, () -> DynamicRegistriesTestMod.DEFAULT_TATER, Tater.CODEC));
	}
}
