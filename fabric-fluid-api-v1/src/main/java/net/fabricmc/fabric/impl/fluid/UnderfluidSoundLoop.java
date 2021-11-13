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

package net.fabricmc.fabric.impl.fluid;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.SoundParameters;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.sound.SoundCategory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Implements a sound loop player used when the player is submerged by a fluid.
 */
@Environment(EnvType.CLIENT)
public class UnderfluidSoundLoop extends MovingSoundInstance {
	private final ClientPlayerEntity player;
	private boolean playing;
	private int transitionTimer;

	/**
	 * @param player Player that will listen the sound.
	 * @param sound Sound to play.
	 * @return New UnderfluidSoundLoop instance.
	 */
	@Contract("_, _ -> new")
	public static @NotNull UnderfluidSoundLoop of(@NotNull ClientPlayerEntity player, @NotNull SoundParameters sound) {
		if (!sound.hasSound()) throw new IllegalArgumentException("There is no sound specified in the sound parameter.");
		return new UnderfluidSoundLoop(player, sound);
	}

	/**
	 * Initializes a new UnderfluidSoundLoop instance.
	 * @param player Player that will listen the sound.
	 * @param sound Sound to play.
	 */
	private UnderfluidSoundLoop(@NotNull ClientPlayerEntity player, @NotNull SoundParameters sound) {
		super(sound.getSoundEvent(), SoundCategory.AMBIENT);
		this.player = player;
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = sound.getVolume();
		this.pitch = sound.getPitch();
		this.transitionTimer = 0;
		start();
	}

	/**
	 * Start the sound loop.
	 */
	public void start() {
		playing = true;
	}

	/**
	 * Stop the sound loop.
	 */
	public void stop() {
		playing = false;
	}

	/**
	 * Executed every tick.
	 */
	@Override
	public void tick() {
		if (!this.player.isRemoved() && this.transitionTimer >= 0) {
			if (playing) ++this.transitionTimer;
			else this.transitionTimer -= 2;

			this.transitionTimer = Math.min(this.transitionTimer, 40);
			this.volume = Math.max(0.0F, Math.min((float)this.transitionTimer / 40.0F, 1.0F));
		} else {
			this.setDone();
		}
	}
}
