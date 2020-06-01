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

package net.fabricmc.fabric.api.level.generator.v1;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.level.generator.FabricLevelGeneratorTypeImpl;

public abstract class FabricLevelGeneratorType extends LevelGeneratorType {
	/**
	 * @param name identifier of the {@link LevelGeneratorType}
	 */
	protected FabricLevelGeneratorType(Identifier name) {
		super(FabricLevelGeneratorTypeImpl.getFreeId(), name.toString(), name.toString(), 0);
	}

	/**
	 * @param name       identifier of the {@link LevelGeneratorType}
	 * @param storedName identifier of the {@link LevelGeneratorType} used to store in the level properties
	 * @param version    version of {@link LevelGeneratorType} used to store in the level properties and shown in crash reports
	 */
	protected FabricLevelGeneratorType(Identifier name, Identifier storedName, int version) {
		super(FabricLevelGeneratorTypeImpl.getFreeId(), name.toString(), storedName.toString(), version);
	}

	/**
	 * @return {@link ChunkGenerator} that is used in {@link OverworldDimension#createChunkGenerator()}
	 */
	public abstract ChunkGenerator<? extends ChunkGeneratorConfig> createChunkGenerator(World world);

	/**
	 * {@link LevelGeneratorType#isCustomizable()} must be true to show {@link ButtonWidget} in {@link CreateWorldScreen}.
	 *
	 * @return {@link Screen} that is opened when customize {@link ButtonWidget} is clicked
	 */
	@Environment(EnvType.CLIENT)
	public Screen getCustomizationScreen(CreateWorldScreen parent) {
		throw new UnsupportedOperationException("Not implemented");
	}
}
