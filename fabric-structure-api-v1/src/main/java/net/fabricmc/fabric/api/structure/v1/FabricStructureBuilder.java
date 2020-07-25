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

package net.fabricmc.fabric.api.structure.v1;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

import net.fabricmc.fabric.impl.structure.FabricStructuresImpl;
import net.fabricmc.fabric.impl.structure.StructuresConfigHooks;
import net.fabricmc.fabric.mixin.structure.FlatChunkGeneratorConfigAccessor;
import net.fabricmc.fabric.mixin.structure.StructureFeatureAccessor;
import net.fabricmc.fabric.mixin.structure.StructuresConfigAccessor;

public class FabricStructureBuilder<FC extends FeatureConfig, S extends StructureFeature<FC>> {
	private final Identifier id;
	private final S structure;
	private GenerationStep.Feature step;
	private StructureConfig defaultConfig;
	private ConfiguredStructureFeature<FC, ? extends StructureFeature<FC>> superflatFeature;
	private boolean adjustsSurface = false;

	private FabricStructureBuilder(Identifier id, S structure) {
		this.id = id;
		this.structure = structure;
	}

	public static <FC extends FeatureConfig, S extends StructureFeature<FC>> FabricStructureBuilder<FC, S> create(Identifier id, S structure) {
		return new FabricStructureBuilder<>(id, structure);
	}

	public FabricStructureBuilder<FC, S> step(GenerationStep.Feature step) {
		this.step = step;
		return this;
	}

	public FabricStructureBuilder<FC, S> defaultConfig(StructureConfig config) {
		this.defaultConfig = config;
		return this;
	}

	public FabricStructureBuilder<FC, S> defaultConfig(int spacing, int separation, int salt) {
		return defaultConfig(new StructureConfig(spacing, separation, salt));
	}

	public FabricStructureBuilder<FC, S> superflatFeature(ConfiguredStructureFeature<FC, ? extends StructureFeature<FC>> superflatFeature) {
		this.superflatFeature = superflatFeature;
		return this;
	}

	public FabricStructureBuilder<FC, S> superflatFeature(FC config) {
		return superflatFeature(structure.configure(config));
	}

	public FabricStructureBuilder<FC, S> adjustsSurface() {
		this.adjustsSurface = true;
		return this;
	}

	public S register() {
		if (step == null) {
			throw new IllegalStateException("Structure \"" + id + "\" is missing a generation step");
		}

		if (defaultConfig == null) {
			throw new IllegalStateException("Structure \"" + id + "\" is missing a default config");
		}

		// Ensure StructuresConfig class is initialized, so the assertion in its static {} block doesn't fail
		StructuresConfig.DEFAULT_STRUCTURES.size();

		StructureFeatureAccessor.callRegister(id.toString(), structure, step);

		if (!id.toString().equals(structure.getName())) {
			// mods should not be overriding getName, but if they do and it's incorrect, this gives an error
			throw new IllegalStateException("Structure " + id + " has mismatching name " + structure.getName() + ". Structures should not override getName.");
		}

		StructuresConfigAccessor.setDefaultStructures(ImmutableMap.<StructureFeature<?>, StructureConfig>builder()
				.putAll(StructuresConfig.DEFAULT_STRUCTURES)
				.put(structure, defaultConfig)
				.build());

		if (superflatFeature != null) {
			FlatChunkGeneratorConfigAccessor.getStructureToFeatures().put(structure, superflatFeature);
		}

		if (adjustsSurface) {
			StructureFeatureAccessor.setSurfaceAdjustingStructures(ImmutableList.<StructureFeature<?>>builder()
					.addAll(StructureFeature.field_24861)
					.add(structure)
					.build());
		}

		// update existing structure configs
		for (StructuresConfig structuresConfig : FabricStructuresImpl.defaultStructuresConfigs) {
			((StructuresConfigHooks) structuresConfig).fabric_updateDefaultEntries();
		}

		return structure;
	}
}
