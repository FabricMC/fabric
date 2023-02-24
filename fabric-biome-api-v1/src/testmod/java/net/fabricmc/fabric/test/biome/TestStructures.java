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

package net.fabricmc.fabric.test.biome;

import java.util.Collections;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.item.Items;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureTerrainAdaptation;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.Structure;

import net.fabricmc.fabric.test.structure.TestStructureProcessor;

public class TestStructures {
	public static final RegistryKey<StructureProcessorList> TEST_LIST = RegistryKey.of(RegistryKeys.PROCESSOR_LIST, new Identifier(FabricBiomeTest.MOD_ID, "test_list"));
	public static final RegistryKey<StructurePool> TEST_POOL = RegistryKey.of(RegistryKeys.TEMPLATE_POOL, new Identifier(FabricBiomeTest.MOD_ID, "test_pool"));
	public static final RegistryKey<Structure> TEST_STRUCTURE = RegistryKey.of(RegistryKeys.STRUCTURE, new Identifier(FabricBiomeTest.MOD_ID, "test_structure"));
	public static final RegistryKey<StructureSet> TEST_SET = RegistryKey.of(RegistryKeys.STRUCTURE_SET, new Identifier(FabricBiomeTest.MOD_ID, "test_structure_set"));

	public static void bootstrapStructureProcessorList(Registerable<StructureProcessorList> registerable) {
		registerable.register(TEST_LIST, new StructureProcessorList(List.of(
			new TestStructureProcessor(Registries.ITEM.getEntry(Items.DIAMOND)))
		));
	}

	public static void bootstrapStructurePool(Registerable<StructurePool> registerable) {
		final RegistryEntryLookup<StructurePool> structurePoolLookup = registerable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
		final RegistryEntryLookup<StructureProcessorList> structureProcessorListLookup = registerable.getRegistryLookup(RegistryKeys.PROCESSOR_LIST);

		registerable.register(TEST_POOL, new StructurePool(
				structurePoolLookup.getOrThrow(StructurePools.EMPTY),
				List.of(
					Pair.of(
						StructurePoolElement.ofProcessedSingle(
							new Identifier(FabricBiomeTest.MOD_ID, "test_structure").toString(),
							structureProcessorListLookup.getOrThrow(TEST_LIST)
						).apply(StructurePool.Projection.RIGID),
					1)
				)
		));
	}

	public static void bootstrapStructure(Registerable<Structure> registerable) {
		final RegistryEntryLookup<StructurePool> structurePoolLookup = registerable.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);
		final RegistryEntryLookup<Biome> biomeLookup = registerable.getRegistryLookup(RegistryKeys.BIOME);

		registerable.register(
				TEST_STRUCTURE,
				new JigsawStructure(
					new Structure.Config(
						RegistryEntryList.of(
							biomeLookup.getOrThrow(BiomeKeys.PLAINS)
						),
						Collections.emptyMap(),
						GenerationStep.Feature.SURFACE_STRUCTURES,
						StructureTerrainAdaptation.BEARD_THIN
					),
					structurePoolLookup.getOrThrow(TEST_POOL),
					1,
					ConstantHeightProvider.ZERO,
					false,
					Heightmap.Type.WORLD_SURFACE_WG
				)
		);
	}

	public static void bootstrapStructureSet(Registerable<StructureSet> registerable) {
		final RegistryEntryLookup<Structure> structureLookup = registerable.getRegistryLookup(RegistryKeys.STRUCTURE);

		registerable.register(TEST_SET, new StructureSet(
				structureLookup.getOrThrow(TEST_STRUCTURE),
				new RandomSpreadStructurePlacement(2, 1, SpreadType.LINEAR, 14357617))
		);
	}
}
