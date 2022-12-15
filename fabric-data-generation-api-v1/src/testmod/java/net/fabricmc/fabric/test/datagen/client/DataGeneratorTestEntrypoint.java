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

package net.fabricmc.fabric.test.datagen.client;

import static net.fabricmc.fabric.test.datagen.DataGeneratorTestContent.MOD_ID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.client.FabricSoundProvider;
import net.fabricmc.fabric.api.datagen.v1.sound.SoundBuilder;

@Environment(EnvType.CLIENT)
public class DataGeneratorTestEntrypoint implements DataGeneratorEntrypoint {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataGeneratorTestEntrypoint.class);

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
		final FabricDataGenerator.Pack pack = dataGenerator.createPack();
		pack.addProvider(TestSoundProvider::new);
	}

	private static class TestSoundProvider extends FabricSoundProvider {
		private TestSoundProvider(FabricDataOutput dataOutput) {
			super(dataOutput);
		}

		@Override
		public void generateSounds(SoundGenerator soundGenerator) {
			soundGenerator.add(SoundEvents.BLOCK_METAL_BREAK, true,
					SoundBuilder.sound(new Identifier(MOD_ID, "replacement_sound_1")).build(),
					SoundBuilder.sound(new Identifier(MOD_ID, "replacement_sound_2")).setVolume(0.5f).setPitch(0.5f).build(),
					SoundBuilder.event(new Identifier(MOD_ID, "replacement_event")).setWeight(2).build());
			soundGenerator.add(SoundEvents.BLOCK_DEEPSLATE_BREAK, true,
					SoundBuilder.event(new Identifier(MOD_ID, "replacement_event")).build());
		}
	}
}
