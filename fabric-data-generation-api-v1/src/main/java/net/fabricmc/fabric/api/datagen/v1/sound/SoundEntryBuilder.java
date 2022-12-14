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

package net.fabricmc.fabric.api.datagen.v1.sound;

import com.google.common.base.Preconditions;

import net.minecraft.util.Identifier;

/**
 * Utility class for building a {@link SoundEntry} with a given set of properties, without necessarily passing them all
 * as parameters.
 */
public class SoundEntryBuilder {
	private final Identifier name;
	private final boolean event;

	private float volume = 1;
	private float pitch = 1;
	private int weight = 1;
	private boolean stream = false;
	private int attenuationDistance = 16;
	private boolean preload = false;

	private SoundEntryBuilder(Identifier name, boolean event) {
		this.name = name;
		this.event = event;
	}

	/**
	 * Build an entry corresponding to a sound file.
	 *
	 * @param name The name of the sound as a namespaced ID with relative folder path.
	 */
	public static SoundEntryBuilder sound(Identifier name) {
		return new SoundEntryBuilder(name, false);
	}

	/**
	 * Build an entry corresponding to an existing {@link net.minecraft.sound.SoundEvent}.
	 *
	 * @param name The ID of the sound event.
	 */
	public static SoundEntryBuilder event(Identifier name) {
		return new SoundEntryBuilder(name, true);
	}

	/**
	 * Sets the volume that the sound should play at as a number between <code>0.0</code> and <code>1.0</code>. Defaults
	 * to <code>1.0</code>.
	 */
	public SoundEntryBuilder setVolume(float volume) {
		Preconditions.checkArgument(volume >= 0 && volume <= 1);
		this.volume = volume;
		return this;
	}

	/**
	 * Sets the pitch that the sound should play at. Note that this is internally clamped in-game between 0.5 and 2.0.
	 */
	public SoundEntryBuilder setPitch(float pitch) {
		Preconditions.checkArgument(pitch > 0);
		this.pitch = pitch;
		return this;
	}

	/**
	 * Sets how much likelier it should be for this sound to play. For example, setting this to 2 will mean that this
	 * sound is twice as likely to play for this event.
	 */
	public SoundEntryBuilder setWeight(int weight) {
		Preconditions.checkArgument(weight >= 0);
		this.weight = weight;
		return this;
	}

	/**
	 * Dictates that this sound should be streamed from its file. Recommended for sounds with a much longer play time
	 * than a couple of seconds, such as music tracks, in order to minimise lag. If set, only 4 instances of this sound
	 * can play in-game at once.
	 */
	public SoundEntryBuilder stream() {
		this.stream = true;
		return this;
	}

	/**
	 * Sets the reduction rate of this sound depending on distance from the source. Defaults to 16.
	 */
	public SoundEntryBuilder setAttenuationDistance(int attenuationDistance) {
		Preconditions.checkArgument(attenuationDistance >= 0);
		this.attenuationDistance = attenuationDistance;
		return this;
	}

	/**
	 * Dictates that this sound should be loaded in advance when loading the resource pack containing it rather than
	 * when the sound itself plays.
	 */
	public SoundEntryBuilder preload() {
		this.preload = true;
		return this;
	}

	/**
	 * Return a finalised {@link SoundEntry} instance to generate data from.
	 */
	public SoundEntry build() {
		return new SoundEntry(name, volume, pitch, weight, stream, attenuationDistance, preload, event);
	}
}
