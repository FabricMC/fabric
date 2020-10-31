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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.generatortype.v1.FabricGeneratorConfig;

final class TestGeneratorConfig extends FabricGeneratorConfig {
	public static final Codec<TestGeneratorConfig> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			Registry.BLOCK.fieldOf("block").forGetter(TestGeneratorConfig::getWorldBlock),
			Codec.INT.fieldOf("height").forGetter(TestGeneratorConfig::getWorldHeight))
			.apply(instance, instance.stable(TestGeneratorConfig::new)));

	private final Block worldBlock;
	private final int worldHeight;

	TestGeneratorConfig(Block worldBlock, int worldHeight) {
		this.worldBlock = worldBlock;
		this.worldHeight = worldHeight;
	}

	@Override
	public Codec<? extends FabricGeneratorConfig> getCodec() {
		return CODEC;
	}

	public Block getWorldBlock() {
		return this.worldBlock;
	}

	public int getWorldHeight() {
		return this.worldHeight;
	}

	@Override
	public String toString() {
		return "worldBlock: " + Registry.BLOCK.getId(this.worldBlock) + ", worldHeight: " + this.worldHeight;
	}
}
