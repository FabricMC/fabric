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

package net.fabricmc.fabric.api.registry;

import net.fabricmc.fabric.api.util.ItemList;
import net.fabricmc.fabric.impl.content.registry.VillagerCompostableRegistryImpl;

/**
 * Registry of items that farmer villagers can collect and
 * use in composters.
 *
 * @see VillagerCollectableRegistry to allow villagers to collect the item
 */
public interface VillagerCompostableRegistry extends ItemList {
	VillagerCompostableRegistry INSTANCE = new VillagerCompostableRegistryImpl();
}
