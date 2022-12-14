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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

/**
 * Extend this class and implement {@link FabricSoundProvider#generateSounds(SoundBuilder)}.
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
	 * <p>Call {@link FabricSoundProvider.SoundBuilder#add(SoundEvent, SoundEntry...)} to add a list of sound entries
	 * for a given {@link SoundEvent}. An optional subtitle to use for the event can be provided in the form of an
	 * existing translation key for this subtitle, along with the option to <code>replace</code> the sound entries for
	 * this event with your own via resource pack, if specifying an existing event from some other namespace.
	 */
	public abstract void generateSounds(SoundBuilder soundBuilder);

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		HashMap<String, JsonObject> soundEvents = new HashMap<>();

		generateSounds(((sound, replace, subtitle, entries) -> {
			Objects.requireNonNull(sound);
			Objects.requireNonNull(entries);

			List<Identifier> keys = Arrays.stream(entries).map(SoundEntry::name).toList();

			if (!keys.stream().filter(i -> Collections.frequency(keys, i) > 1).toList().isEmpty()) {
				throw new RuntimeException("Entries for sound event " + sound.getId() + " contain duplicate sound names. Event will be omitted.");
			}

			JsonObject soundEventData = new JsonObject();
			JsonArray soundEntries = new JsonArray();

			Arrays.asList(entries).forEach(e -> soundEntries.add(e.build()));
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
		return DataProvider.writeToPath(writer, soundsJson, soundsPath);
	}

	@Override
	public String getName() {
		return "Sounds";
	}

	@ApiStatus.NonExtendable
	@FunctionalInterface
	public interface SoundBuilder {
		void add(SoundEvent sound, boolean replace, @Nullable String subtitle, SoundEntry... entries);

		default void add(SoundEvent sound, boolean replace, SoundEntry... entries) {
			add(sound, replace, null, entries);
		}

		default void add(SoundEvent sound, @Nullable String subtitle, SoundEntry... entries) {
			add(sound, false, subtitle, entries);
		}

		default void add(SoundEvent sound, SoundEntry... entries) {
			add(sound, false, null, entries);
		}
	}

	public record SoundEntry(Identifier name, float volume, float pitch, int weight, boolean stream, int attenuationDistance, boolean preload, @Nullable Type type) {
		// TODO: add more constructors for different param combinations
		public SoundEntry(Identifier name, Type type) {
			this(name, 1, 1, 1, false, 16, false, type);
		}

		public SoundEntry(Identifier name) {
			this(name, 1, 1, 1, false, 16, false, null);
		}

		private boolean allDefaults() {
			return volume == 1 && pitch == 1 && weight == 1 && !stream && attenuationDistance == 16 && !preload && type != null;
		}

		private JsonElement build() {
			if (allDefaults()) {
				return new JsonPrimitive(name.toString());
			} else {
				JsonObject soundEntry = new JsonObject();
				soundEntry.addProperty("name", name.toString());

				if (volume != 1) {
					soundEntry.addProperty("volume", volume);
				}

				if (pitch != 1) {
					soundEntry.addProperty("pitch", pitch);
				}

				if (weight != 1) {
					soundEntry.addProperty("weight", weight);
				}

				if (stream) {
					soundEntry.addProperty("stream", true);
				}

				if (attenuationDistance != 16) {
					soundEntry.addProperty("attenuation_distance", attenuationDistance);
				}

				if (preload) {
					soundEntry.addProperty("preload", true);
				}

				if (type != null) {
					soundEntry.addProperty("type", type.name().toLowerCase());
				}

				return soundEntry;
			}
		}
	}

	public enum Type {
		SOUND,
		EVENT
	}
}
