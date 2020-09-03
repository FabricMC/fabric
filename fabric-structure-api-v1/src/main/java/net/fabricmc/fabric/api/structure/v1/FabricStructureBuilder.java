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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

import net.fabricmc.fabric.impl.structure.FabricStructureUtil;
import net.fabricmc.fabric.impl.structure.StructuresConfigHooks;
import net.fabricmc.fabric.mixin.structure.BiomeAccessor;
import net.fabricmc.fabric.mixin.structure.FlatChunkGeneratorConfigAccessor;
import net.fabricmc.fabric.mixin.structure.StructureFeatureAccessor;
import net.fabricmc.fabric.mixin.structure.StructuresConfigAccessor;

/**
 * A builder for registering custom structures.
 *
 * <p>Example usage:
 * <pre>{@code
 * StructureFeature structure = new MyStructure(DefaultFeatureConfig.CODEC);
 * ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> configuredStructure
 *     = structure.configure(new DefaultFeatureConfig());
 * FabricStructureBuilder.create(new Identifier("mymod:mystructure"), structure)
 *     .step(GenerationStep.Feature.SURFACE_STRUCTURES) // required
 *     .defaultConfig(32, 8, 12345) // required
 *     .superflatFeature(configuredStructure)
 *     .register();}
 * </pre></p>
 *
 * <p>This class does <i>not</i> add structures to biomes for you, you have to do that yourself. You may also need to
 * register custom structure pieces yourself.</p>
 */
public final class FabricStructureBuilder<FC extends FeatureConfig, S extends StructureFeature<FC>> {
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

	/**
	 * Creates a new {@code FabricStructureBuilder} for registering a structure.
	 *
	 * @param id The structure ID.
	 * @param structure The {@linkplain StructureFeature} you want to register.
	 */
	public static <FC extends FeatureConfig, S extends StructureFeature<FC>> FabricStructureBuilder<FC, S> create(Identifier id, S structure) {
		Objects.requireNonNull(id, "id must not be null");
		Objects.requireNonNull(structure, "structure must not be null");
		return new FabricStructureBuilder<>(id, structure);
	}

	/**
	 * Sets the generation step of this structure. The generation step specifies when the structure is generated, to
	 * ensure they are generated in the correct order to reduce the amount of floating blocks.
	 *
	 * <p>The most commonly used values for structures are {@linkplain GenerationStep.Feature#SURFACE_STRUCTURES} and
	 * {@linkplain GenerationStep.Feature#UNDERGROUND_STRUCTURES}, however technically any value in the
	 * {@linkplain GenerationStep.Feature} enum may be used.</p>
	 *
	 * <p>This is a required option.</p>
	 */
	public FabricStructureBuilder<FC, S> step(GenerationStep.Feature step) {
		Objects.requireNonNull(step, "step must not be null");
		this.step = step;
		return this;
	}

	/**
	 * Sets the default {@linkplain StructureConfig} for this structure. See the alternative
	 * {@linkplain #defaultConfig(int, int, int)} for details.
	 *
	 * <p>This is a required option.</p>
	 */
	public FabricStructureBuilder<FC, S> defaultConfig(StructureConfig config) {
		Objects.requireNonNull(config, "config must not be null");
		this.defaultConfig = config;
		return this;
	}

	/**
	 * Sets the default {@linkplain StructureConfig} for this structure. This sets the default configuration of where in
	 * the world to place structures.
	 *
	 * <p>Note: the {@code spacing} and {@code separation} options are subject to other checks for whether the structure
	 * can spawn, such as biome. If these checks always pass and the structure can spawn in every biome, then the
	 * description of these values below would be exactly correct.</p>
	 *
	 * <p>This is a required option. Vanilla needs it to function.</p>
	 *
	 * @param spacing The average distance between 2 structures of this type along the X and Z axes.
	 * @param separation The minimum distance between 2 structures of this type.
	 * @param salt The random salt of the structure. This does not affect how common the structure is, but every
	 *                structure must have an unique {@code salt} in order to spawn in different places.
	 *
	 * @see #defaultConfig(StructureConfig)
	 */
	public FabricStructureBuilder<FC, S> defaultConfig(int spacing, int separation, int salt) {
		return defaultConfig(new StructureConfig(spacing, separation, salt));
	}

	/**
	 * Sets the structure configuration which spawns in superflat worlds. If unset, this structure will not spawn in
	 * superflat worlds.
	 *
	 * @see #superflatFeature(FeatureConfig)
	 */
	public FabricStructureBuilder<FC, S> superflatFeature(ConfiguredStructureFeature<FC, ? extends StructureFeature<FC>> superflatFeature) {
		Objects.requireNonNull(superflatFeature, "superflatFeature must not be null");
		this.superflatFeature = superflatFeature;
		return this;
	}

	/**
	 * Sets the structure configuration which spawns in superflat worlds. If unset, this structure will not spawn in
	 * superflat worlds.
	 *
	 * @see #superflatFeature(ConfiguredStructureFeature)
	 */
	public FabricStructureBuilder<FC, S> superflatFeature(FC config) {
		return superflatFeature(structure.configure(config));
	}

	/**
	 * Causes structure pieces of this structure to adjust the surface of the world to fit them, so that they don't
	 * stick out of or into the ground.
	 */
	public FabricStructureBuilder<FC, S> adjustsSurface() {
		this.adjustsSurface = true;
		return this;
	}

	/**
	 * Registers this structure and applies the other changes from the {@linkplain FabricStructureBuilder}.
	 */
	public S register() {
		Objects.requireNonNull(step, "Structure \"" + id + "\" is missing a generation step");
		Objects.requireNonNull(defaultConfig, "Structure \"" + id + "\" is missing a default config");

		// Ensure StructuresConfig class is initialized, so the assertion in its static {} block doesn't fail
		StructuresConfig.DEFAULT_STRUCTURES.size();

		StructureFeatureAccessor.callRegister(id.toString(), structure, step);

		if (!id.toString().equals(structure.getName())) {
			// mods should not be overriding getName, but if they do and it's incorrect, this gives an error
			throw new IllegalStateException(String.format("Structure \"%s\" has mismatching name \"%s\". Structures should not override \"getName\".", id, structure.getName()));
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
					.addAll(StructureFeature.JIGSAW_STRUCTURES)
					.add(structure)
					.build());
		}

		// update existing structures configs
		for (StructuresConfig structuresConfig : FabricStructureUtil.DEFAULT_STRUCTURES_CONFIGS) {
			((StructuresConfigHooks) structuresConfig).fabric_updateDefaultEntries();
		}

		// update builtin biomes, just to be safe
		for (Biome biome : BuiltinRegistries.BIOME) {
			BiomeAccessor biomeAccessor = (BiomeAccessor) (Object) biome;
			Map<Integer, List<StructureFeature<?>>> structureLists = biomeAccessor.getStructureLists();

			if (!(structureLists instanceof HashMap)) {
				// not guaranteed by the standard to be a mutable map
				((BiomeAccessor) (Object) biome).setStructureLists(structureLists = new HashMap<>(structureLists));
			}

			// not guaranteed by the standard to be mutable lists
			structureLists.compute(step.ordinal(), (k, v) -> makeMutable(v)).add(structure);
		}

		return structure;
	}

	private static List<StructureFeature<?>> makeMutable(List<StructureFeature<?>> mapValue) {
		if (mapValue == null) return new ArrayList<>();
		if (!(mapValue instanceof ArrayList)) return new ArrayList<>(mapValue);
		return mapValue;
	}
}
