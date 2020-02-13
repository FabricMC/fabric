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

package net.fabricmc.fabric.api.event.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface FallDeathSuffixCallback {
	Event<FallDeathSuffixCallback> event = EventFactory.createArrayBacked(FallDeathSuffixCallback.class,
		fallDeathCallbacks -> (entity, state) -> {
			String finalSuffix = null;

			for (FallDeathSuffixCallback fallDeathCallback : fallDeathCallbacks) {
				String suffix = fallDeathCallback.getFallDeathSuffix(entity, state);
				if (suffix != null) finalSuffix = suffix;
			}

			return finalSuffix;
		});

	/**
	 * Used for providing a non-vanilla fall-death message.
	 *
	 * <p>In order to use this, your translation file should have the translation key
	 * "death.fell.accident.suffix", where "suffix" is the string returned by this method.
	 * It is possible to return null if you do not wish to handle a specific situation. </p>
	 *
	 * @param entity The entity that is being tracked.
	 * @param state the block state that is being climbed.
	 *
	 * @return The fall death suffix or null.
	 *
	 */
	String getFallDeathSuffix(LivingEntity entity, BlockState state);
}
