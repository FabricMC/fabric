/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Pair;

public interface FallDeathSuffixCallback {

	Event<FallDeathSuffixCallback> event = EventFactory.createArrayBacked(FallDeathSuffixCallback.class,
		(fallDeathCallbacks -> (entity, state) -> {
			String result = "generic";
			int currentPriority = 0;

			for (FallDeathSuffixCallback fallDeathCallback : fallDeathCallbacks) {
				Pair<String, Integer> pair = fallDeathCallback.getFallDeathSuffix(entity, state);

				if (pair.getRight() >= currentPriority) {
					result = pair.getLeft();
					currentPriority = pair.getRight();
				}
			}

			return new Pair<>(result, 0);
		}));

	/**
	 * Provides a custom fall death suffix for non-vanilla situation.
	 *
	 * @param entity The entity that is being tracked.
	 * @param state the block state that is being climbed.
	 *
	 * @return a pair containing the suffix for the fall death and the priority number.
	 *
	 * In order to use this, your translation file should have the translation key
	 * "death.fell.accident.suffix", where "suffix" is the string returned by this method.
	 * It is possible to return a null string; if all callbacks do this, the vanilla suffix
	 * for that situation will be used instead.
	 *
	 * The priority number allows for the suffix to take priority over over other suffixes.
	 * i.e. if you return a priority of 2 and everything after it returns a priority of 1,
	 * your suffix will be used. In the event that multiple callbacks return the same priority
	 * number, the suffix of the last callback will be used. This number should be greater than
	 * 0 or it will be ignored.
	 */
	Pair<String, Integer> getFallDeathSuffix(LivingEntity entity, BlockState state);
}
