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

package net.fabricmc.fabric.api.util;

import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Incapsulates some sound parameters.
 */
@SuppressWarnings("unused")
public class SoundParameters {
	private final SoundEvent soundEvent;
	private final float volume;
	private final float pitch;

	/**
	 * SoundParameters instance with no sound.
	 */
	private static final SoundParameters EMPTY = of(null);

	/**
	 * @return SoundParameters instance with no sound.
	 */
	public static @NotNull SoundParameters empty() {
		return EMPTY;
	}

	/**
	 * @param soundEvent Sound to play.
	 * @return New SoundParameters instance with volume and pitch both equals to 1.
	 */
	@Contract(value = "_ -> new", pure = true)
	public static @NotNull SoundParameters of(SoundEvent soundEvent) {
		return new SoundParameters(soundEvent, 1f, 1f);
	}

	/**
	 * @param soundEvent Sound to play.
	 * @param volume Sound volume.
	 * @param pitch Sound pitch.
	 * @return New SoundParameters instance.
	 */
	@Contract(value = "_, _, _ -> new", pure = true)
	public static @NotNull SoundParameters of(SoundEvent soundEvent, float volume, float pitch) {
		return new SoundParameters(soundEvent, volume, pitch);
	}

	/**
	 * Initializes a new SoundParameters instance.
	 * @param soundEvent Sound to play.
	 * @param volume Sound volume.
	 * @param pitch Sound pitch.
	 */
	private SoundParameters(SoundEvent soundEvent, float volume, float pitch) {
		this.soundEvent = soundEvent;
		this.volume = volume;
		this.pitch = pitch;
	}

	/**
	 * @return SoundEvent.
	 */
	public SoundEvent getSoundEvent() {
		return soundEvent;
	}

	/**
	 * @return Sound volume.
	 */
	public float getVolume() {
		return volume;
	}

	/**
	 * @return Sound pitch.
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * @return True if the SoundEvent is not null.
	 */
	public boolean hasSound() {
		return soundEvent != null;
	}

	/**
	 * If the SoundEvent is not null, performs the given action with the value, otherwise does nothing.
	 * @param action The action to be performed, if the SoundEvent is not null.
	 * @throws NullPointerException if the SoundEvent is not null and the given action is {@code null}.
	 */
	public void ifHasSound(Consumer<SoundParameters> action) {
		if (hasSound()) action.accept(this);
	}

	/**
	 * If the SoundEvent is not null, performs the given action with the value,
	 * otherwise performs the given empty-based action.
	 * @param action The action to be performed, if the SoundEvent is not null.
	 * @param emptyAction The empty-based action to be performed, if the SoundEvent is null.
	 * @throws NullPointerException if the SoundEvent is not null and the given action is {@code null},
	 * or the SoundEvent is null and the given empty-based action is {@code null}.
	 */
	public void ifHasSoundOrElse(Consumer<SoundParameters> action, Runnable emptyAction) {
		if (hasSound()) action.accept(this);
		else emptyAction.run();
	}
}
