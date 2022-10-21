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

package net.fabricmc.fabric.impl.datagen;

import java.nio.file.Path;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.data.DataOutput;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/**
 * Extends {@link DataOutput} to keep track of the {@link FabricDataGenerator} that it originated from.
 */
@ApiStatus.Internal
public class FabricDataOutput extends DataOutput {
	private final FabricDataGenerator generator;

	public FabricDataOutput(FabricDataGenerator generator, Path path) {
		super(path);
		this.generator = generator;
	}

	public FabricDataGenerator getGenerator() {
		return generator;
	}
}
