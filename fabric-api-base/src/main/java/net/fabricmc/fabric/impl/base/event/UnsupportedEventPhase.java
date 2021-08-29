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

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventPhase;

/**
 * Base phase implementation for foreign events that don't support phases.
 */
public class UnsupportedEventPhase<T> implements EventPhase<T> {
	private final Event<T> event;

	public UnsupportedEventPhase(Event<T> event) {
		this.event = event;
	}

	@Override
	public void register(T listener) {
		event.register(listener);
	}

	@Override
	public void runBefore(Identifier... subsequentPhases) {
	}

	@Override
	public void runAfter(Identifier... previousPhases) {
	}
}
