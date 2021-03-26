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

package net.fabricmc.fabric.mixin.biome;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.biome.layer.AddHillsLayer;

@Mixin(AddHillsLayer.class)
public interface AddHillsLayerAccessor {
	/**
	 * This field contains a raw-id to raw-id map for establishing parent/child relationships that
	 * model derived biomes.
	 *
	 * <p>For example, it contains a mapping for 1 -> 129 where 1 is the raw id of plains, while 129 is the raw id
	 * of the sunflower plains, which is derived from plains.
	 */
	@Accessor("field_26727")
	static Int2IntMap getBaseToVariantMap() {
		throw new AssertionError("mixin");
	}
}
