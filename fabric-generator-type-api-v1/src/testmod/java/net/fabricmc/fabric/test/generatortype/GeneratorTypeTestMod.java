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

package net.fabricmc.fabric.test.generatortype;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;

public final class GeneratorTypeTestMod implements ModInitializer {
	private static final Identifier FABRIC_TEST = new Identifier("fabric", "fabric_test");

	@Override
	public void onInitialize() {
		Registry.register(Registry.CHUNK_GENERATOR, FABRIC_TEST, TestChunkGenerator.CODEC);
		Registry.register(Registry.BIOME_SOURCE, FABRIC_TEST, TestBiomeSource.CODEC);
		new TestGeneratorType(FABRIC_TEST);
	}
}
