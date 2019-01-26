/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

import net.fabricmc.fabric.api.util.Block2ObjectMap;
import net.fabricmc.fabric.impl.registry.PunchExtinguishableBlockRegistryImpl;

/**
 * Registry of blocks that can be punched to extinguish / destroy, like fire.
 * Punch-extinguishable blocks can have an empty outline shape.
 */
public interface PunchExtinguishableBlockRegistry extends Block2ObjectMap<Boolean> {
	final PunchExtinguishableBlockRegistry INSTANCE = PunchExtinguishableBlockRegistryImpl.INSTANCE;
}
