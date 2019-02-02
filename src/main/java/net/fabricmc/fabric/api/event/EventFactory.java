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

package net.fabricmc.fabric.api.event;

import net.fabricmc.fabric.impl.event.EventFactoryImpl;
import net.fabricmc.loader.launch.common.FabricLauncherBase;

import java.util.function.Function;

public final class EventFactory {
	private static boolean profilingEnabled = true;

	private EventFactory() {

	}

	public static boolean isProfilingEnabled() {
		return profilingEnabled;
	}

	public static void invalidate() {
		EventFactoryImpl.invalidate();
	}

	public static <T> Event<T> arrayBacked(Class<T> type, Function<T[], T> joiner) {
		return EventFactoryImpl.arrayBacked(type, joiner);
	}

	public static <T> Event<T> arrayBacked(Class<T> type, T emptyInvoker, Function<T[], T> joiner) {
		return EventFactoryImpl.arrayBacked(type, emptyInvoker, joiner);
	}
}
