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

package net.fabricmc.fabric.api.datagen.v1.provider;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.sound.SoundBuilder;

/**
 * Extend this class and implement {@link FabricSoundProvider#generateSounds(SoundGenerator)}.
 *
 * <p>Register an instance of the class with {@link FabricDataGenerator.Pack#addProvider} in a {@link net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint}.
 */
public abstract class FabricSoundProvider implements DataProvider {
	protected final FabricDataOutput dataOutput;

	protected FabricSoundProvider(FabricDataOutput dataOutput) {
		this.dataOutput = dataOutput;
	}

	/**
	 * Implement this method to register sounds.
	 *
	 * <p>Call {@link SoundGenerator#add(SoundEvent, SoundBuilder...)} to add a list of sound entries
	 * for a given {@link SoundEvent}.
	 */
	public abstract void generateSounds(SoundGenerator soundGenerator);

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		HashMap<String, JsonObject> soundEvents = new HashMap<>();

		generateSounds(((sound, replace, subtitle, entries) -> {
			Objects.requireNonNull(sound);
			Objects.requireNonNull(entries);

			List<Identifier> keys = Arrays.stream(entries).map(SoundBuilder::getName).toList();

			if (!keys.stream().filter(i -> Collections.frequency(keys, i) > 1).toList().isEmpty()) {
				throw new RuntimeException("Entries for sound event " + sound.getId() + " contain duplicate sound names. Event will be omitted.");
			}

			JsonObject soundEventData = new JsonObject();
			JsonArray soundEntries = new JsonArray();

			Arrays.asList(entries).forEach(s -> soundEntries.add(s.build()));
			soundEventData.add("sounds", soundEntries);

			if (replace) {
				soundEventData.addProperty("replace", true);
			}

			if (subtitle != null) {
				soundEventData.addProperty("subtitle", subtitle);
			}

			soundEvents.put(sound.getId().toString(), soundEventData);
		}));

		JsonObject soundsJson = new JsonObject();

		for (Map.Entry<String, JsonObject> entry : soundEvents.entrySet()) {
			soundsJson.add(entry.getKey(), entry.getValue());
		}

		Path soundsPath = dataOutput
				.getResolver(DataOutput.OutputType.RESOURCE_PACK, ".")
				.resolveJson(new Identifier(dataOutput.getModId(), "sounds"));
		return DataProvider.writeToPath(writer, soundsJson, soundsPath.normalize());
	}

	@Override
	public String getName() {
		return "Sounds";
	}

	@ApiStatus.NonExtendable
	@FunctionalInterface
	public interface SoundGenerator {
		/**
		 * Adds an individual {@link SoundEvent} and its respective sounds to your mod's <code>sounds.json</code> file.
		 *
		 * @param sound The {@link SoundEvent} to add an entry for.
		 * @param replace Set this to <code>true</code> if this entry corresponds to a sound event from vanilla
		 *                Minecraft or some other mod's namespace, in order to replace the default sounds from the
		 *                original namespace's sounds file via your own namespace's resource pack.
		 * @param subtitle An optional subtitle to use for the event, given as a translation key for the subtitle.
		 * @param sounds A list of {@link SoundBuilder} instances from which to generate individual sound entry data for
		 *                this event.
		 */
		void add(SoundEvent sound, boolean replace, @Nullable String subtitle, SoundBuilder... sounds);

		/**
		 * Adds an individual {@link SoundEvent} and its respective sounds to your mod's <code>sounds.json</code> file.
		 *
		 * @param sound The {@link SoundEvent} to add an entry for.
		 * @param replace Set this to <code>true</code> if this entry corresponds to a sound event from vanilla
		 *                Minecraft or some other mod's namespace, in order to replace the default sounds from the
		 *                original namespace's sounds file via your own namespace's resource pack.
		 * @param sounds A list of {@link SoundBuilder} instances from which to generate individual sound entry data for
		 *                this event.
		 */
		default void add(SoundEvent sound, boolean replace, SoundBuilder... sounds) {
			add(sound, replace, null, sounds);
		}

		/**
		 * Adds an individual {@link SoundEvent} and its respective sounds to your mod's <code>sounds.json</code> file.
		 *
		 * @param sound The {@link SoundEvent} to add an entry for.
		 * @param subtitle An optional subtitle to use for the event, given as a translation key for the subtitle.
		 * @param sounds A list of {@link SoundBuilder} instances from which to generate individual sound entry data for
		 *                this event.
		 */
		default void add(SoundEvent sound, @Nullable String subtitle, SoundBuilder... sounds) {
			add(sound, false, subtitle, sounds);
		}

		/**
		 * Adds an individual {@link SoundEvent} and its respective sounds to your mod's <code>sounds.json</code> file.
		 *
		 * @param sound The {@link SoundEvent} to add an entry for.
		 * @param sounds A list of {@link SoundBuilder} instances from which to generate individual sound entry data for
		 *                this event.
		 */
		default void add(SoundEvent sound, SoundBuilder... sounds) {
			add(sound, false, null, sounds);
		}
	}
}
