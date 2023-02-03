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

package net.fabricmc.fabric.api.biome.v1;

import org.jetbrains.annotations.Nullable;

import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

/**
 * Fabric-provided extensions for {@link StructureProcessor} subclasses.
 *
 * <p>Note: This interface is automatically implemented on all structure processors via Mixin and interface injection.
 */
public interface FabricStructureProcessor {
	/**
	 * Process the {@link StructureTemplate.StructureEntityInfo} before spawning the entity in a structure.
	 * This method is similar to {@link StructureProcessor#process(WorldView, BlockPos, BlockPos, StructureTemplate.StructureBlockInfo, StructureTemplate.StructureBlockInfo, StructurePlacementData)}
	 *
	 * @param entityInfo The {@link StructureTemplate.StructureEntityInfo} used to spawn the entity. The {@code pos} and {@code blockPos} field values are relative to the structure and not the target world.
	 * @param world The {@link WorldView} instance
	 * @param pos The {@link BlockPos} of the structure
	 * @param pivot The pivot {@link BlockPos} of the structure
	 * @param data The {@link StructurePlacementData} instance
	 * @return A {@link StructureTemplate.StructureEntityInfo} or null to prevent the entity from being spawned.
	 */
	@Nullable
	default StructureTemplate.StructureEntityInfo process(
			StructureTemplate.StructureEntityInfo entityInfo,
			WorldView world,
			BlockPos pos,
			BlockPos pivot,
			StructurePlacementData data) {
		return entityInfo;
	}
}
