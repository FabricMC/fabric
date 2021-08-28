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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;

class ArrayBackedEvent<T> extends Event<T> {
	private static final Logger LOGGER = LogManager.getLogger("fabric-api-base");

	private final Function<T[], T> invokerFactory;
	private final Object lock = new Object();
	private T[] handlers;
	/**
	 * Registered event phases.
	 */
	private final Map<Identifier, EventPhaseData<T>> phases = new LinkedHashMap<>();
	/**
	 * Phases sorted in the correct dependency order.
	 */
	private final List<EventPhaseData<T>> sortedPhases = new ArrayList<>();
	/**
	 * Set of visited phases, used by the DFS when rebuilding the phase list.
	 */
	private final Set<EventPhaseData<T>> visitedPhases = Collections.newSetFromMap(new IdentityHashMap<>());

	@SuppressWarnings("unchecked")
	ArrayBackedEvent(Class<? super T> type, Function<T[], T> invokerFactory) {
		this.invokerFactory = invokerFactory;
		this.handlers = (T[]) Array.newInstance(type, 0);
		update();
	}

	void update() {
		this.invoker = invokerFactory.apply(handlers);
	}

	@Override
	public void register(T listener) {
		register(DEFAULT_PHASE, listener);
	}

	@Override
	public void register(Identifier phaseIdentifier, T listener) {
		Objects.requireNonNull(phaseIdentifier, "Tried to register a listener for a null phase!");
		Objects.requireNonNull(listener, "Tried to register a null listener!");

		synchronized (lock) {
			EventPhaseData<T> phase = phases.get(phaseIdentifier);

			if (phase == null) {
				// Create phase if it doesn't exist yet.
				phase = new EventPhaseData<>(phaseIdentifier);
				phases.put(phaseIdentifier, phase);
				// This phase can't have dependencies yet, so we'll just put it at the end for now.
				sortedPhases.add(phase);
			}

			phase.listeners.add(listener);

			// Rebuild handlers.
			@SuppressWarnings("unchecked")
			T[] newHandlers = (T[]) Array.newInstance(handlers.getClass().getComponentType(), handlers.length+1);
			int newHandlersIndex = 0;

			for (EventPhaseData<T> existingPhase : sortedPhases) {
				for (T handler : existingPhase.listeners) {
					newHandlers[newHandlersIndex++] = handler;
				}
			}

			handlers = newHandlers;

			// Rebuild invoker.
			update();
		}
	}

	@Override
	public void addPhaseOrdering(Identifier firstPhase, Identifier secondPhase) {
		Objects.requireNonNull(firstPhase, "Tried to add an ordering for a null phase.");
		Objects.requireNonNull(secondPhase, "Tried to add an ordering for a null phase.");
		if (firstPhase.equals(secondPhase)) throw new IllegalArgumentException("Tried to add a phase that depends on itself.");

		synchronized (lock) {
			EventPhaseData<T> first = phases.computeIfAbsent(firstPhase, EventPhaseData::new);
			EventPhaseData<T> second = phases.computeIfAbsent(secondPhase, EventPhaseData::new);
			first.subsequentPhases.add(second);
			sortPhases();
		}
	}

	private void sortPhases() {
		sortedPhases.clear();
		visitedPhases.clear();

		for (EventPhaseData<T> phase : phases.values()) {
			visitPhase(phase, null);
		}

		// Reset visit status for the next visit.
		for (EventPhaseData<T> phase : sortedPhases) {
			phase.visitStatus = 0;
		}

		Collections.reverse(sortedPhases);
	}

	private void visitPhase(EventPhaseData<T> phase, EventPhaseData<T> parent) {
		if (phase.visitStatus == 0) {
			// Not yet visited.
			phase.visitStatus = 1;

			for (EventPhaseData<T> data : phase.subsequentPhases) {
				visitPhase(data, phase);
			}

			sortedPhases.add(phase);
			phase.visitStatus = 2;
		} else if (phase.visitStatus == 1) {
			// Already visiting, so we have found a cycle.
			LOGGER.warn(String.format(
					"Event phase ordering conflict detected.%nEvent phase %s is ordered both before and after event phase %s.",
					phase.id,
					parent.id
			));
		}
	}

	private static class EventPhaseData<T> {
		final Identifier id;
		final List<T> listeners = new ArrayList<>();
		final List<EventPhaseData<T>> subsequentPhases = new ArrayList<>();
		int visitStatus = 0; // 0: not visited, 1: visiting, 2: visited

		private EventPhaseData(Identifier id) {
			this.id = id;
		}
	}
}
