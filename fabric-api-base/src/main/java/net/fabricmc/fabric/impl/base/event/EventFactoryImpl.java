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

package net.fabricmc.fabric.impl.base.event;

import java.util.function.Function;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;

public final class EventFactoryImpl {
	private EventFactoryImpl() { }

	public static <T> Event<T> createArrayBacked(Class<? super T> type, Function<T[], T> invokerFactory) {
		return new ArrayBackedEvent<>(type, invokerFactory);
	}

	public static void ensureContainsDefault(Identifier[] defaultPhases) {
		for (Identifier id : defaultPhases) {
			if (id.equals(Event.DEFAULT_PHASE)) {
				return;
			}
		}

		throw new IllegalArgumentException("The event phases must contain Event.DEFAULT_PHASE.");
	}

	public static void ensureNoDuplicates(Identifier[] defaultPhases) {
		for (int i = 0; i < defaultPhases.length; ++i) {
			for (int j = i+1; j < defaultPhases.length; ++j) {
				if (defaultPhases[i].equals(defaultPhases[j])) {
					throw new IllegalArgumentException("Duplicate event phase: " + defaultPhases[i]);
				}
			}
		}
	}
}
