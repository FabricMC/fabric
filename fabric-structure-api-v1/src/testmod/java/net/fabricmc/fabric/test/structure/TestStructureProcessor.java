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

package net.fabricmc.fabric.test.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class TestStructureProcessor extends StructureProcessor {
	public static final Codec<TestStructureProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			RegistryFixedCodec.of(RegistryKeys.ITEM).fieldOf("item").forGetter(TestStructureProcessor::getItem)
	).apply(instance, TestStructureProcessor::new));

	private final RegistryEntry<Item> item;

	public TestStructureProcessor(RegistryEntry<Item> item) {
		this.item = item;
	}

	public RegistryEntry<Item> getItem() {
		return item;
	}

	@Nullable
	@Override
	public StructureTemplate.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlacementData data) {
		return currentBlockInfo;
	}

	@Override
	public @Nullable StructureTemplate.StructureEntityInfo process(StructureTemplate.StructureEntityInfo entityInfo, WorldView world, BlockPos pos, BlockPos pivot, StructurePlacementData data) {
		final String entityId = entityInfo.nbt.getString("id");

		if (entityId.equals("minecraft:item_frame")) {
			final NbtCompound nbt = entityInfo.nbt.copy();
			nbt.getCompound("Item").putString("id", item.getKey().get().getValue().toString());
			return new StructureTemplate.StructureEntityInfo(entityInfo.pos, entityInfo.blockPos, nbt);
		} else if (entityId.equals("minecraft:armor_stand")) {
			// Don't spawn armor stands
			return null;
		}

		return entityInfo;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return FabricStructureTest.TEST_STRUCTURE_PROCESSOR_TYPE;
	}
}
