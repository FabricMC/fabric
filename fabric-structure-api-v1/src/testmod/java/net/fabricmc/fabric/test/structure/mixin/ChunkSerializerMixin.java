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

package net.fabricmc.fabric.test.structure.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.world.ChunkSerializer;

@Mixin(ChunkSerializer.class)
abstract class ChunkSerializerMixin {
	/**
	 * @reason Changes the logging message for the `unknown structure start` to describe which chunk the missing structure is located in for debugging purposes.
	 */
	@ModifyConstant(method = "readStructureStarts", constant = @Constant(stringValue = "Unknown structure start: {}"))
	private static String modifyErrorMessage(String original, StructureManager structureManager, CompoundTag tag, long worldSeed) {
		// Use coordinates in tag to determine the position of the chunk
		final int xPos = tag.getInt("xPos");
		final int zPos = tag.getInt("zPos");

		return String.format("Unknown structure start: {} at chunk [%s, %s]", xPos, zPos);
	}
}
