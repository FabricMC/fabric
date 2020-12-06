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

package net.fabricmc.fabric.impl.structure;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.world.gen.chunk.StructuresConfig;

public final class FabricStructureUtil {
	private FabricStructureUtil() { }

	// This tracks all StructuresConfig objects that have been created with the default set of structures
	// in order to add mod-created structures that are registered later
	public static final Set<StructuresConfig> DEFAULT_STRUCTURES_CONFIGS = Collections.newSetFromMap(new WeakHashMap<>());
}
