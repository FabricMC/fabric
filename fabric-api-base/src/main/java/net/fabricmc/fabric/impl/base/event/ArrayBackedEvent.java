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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
			getOrCreatePhase(phaseIdentifier).addListener(listener);
			rebuildInvoker(handlers.length + 1);
		}
	}

	private EventPhaseData<T> getOrCreatePhase(Identifier id) {
		EventPhaseData<T> phase = phases.get(id);

		if (phase == null) {
			phase = new EventPhaseData<>(id, handlers.getClass().getComponentType());
			phases.put(id, phase);
			sortedPhases.add(phase);
		}

		return phase;
	}

	private void rebuildInvoker(int newLength) {
		// Rebuild handlers.
		if (sortedPhases.size() == 1) {
			// Special case with a single phase: use the array of the phase directly.
			handlers = sortedPhases.get(0).listeners;
		} else {
			@SuppressWarnings("unchecked")
			T[] newHandlers = (T[]) Array.newInstance(handlers.getClass().getComponentType(), newLength);
			int newHandlersIndex = 0;

			for (EventPhaseData<T> existingPhase : sortedPhases) {
				for (T handler : existingPhase.listeners) {
					newHandlers[newHandlersIndex++] = handler;
				}
			}

			handlers = newHandlers;
		}

		// Rebuild invoker.
		update();
	}

	@Override
	public void addPhaseOrdering(Identifier firstPhase, Identifier secondPhase) {
		Objects.requireNonNull(firstPhase, "Tried to add an ordering for a null phase.");
		Objects.requireNonNull(secondPhase, "Tried to add an ordering for a null phase.");
		if (firstPhase.equals(secondPhase)) throw new IllegalArgumentException("Tried to add a phase that depends on itself.");

		synchronized (lock) {
			EventPhaseData<T> first = getOrCreatePhase(firstPhase);
			EventPhaseData<T> second = getOrCreatePhase(secondPhase);
			first.subsequentPhases.add(second);
			second.previousPhases.add(first);
			sortPhases();
			rebuildInvoker(handlers.length);
		}
	}

	/**
	 * Uses a modified Kosaraju SCC to sort the phases.
	 */
	private void sortPhases() {
		sortedPhases.clear();

		// FIRST VISIT
		List<EventPhaseData<T>> toposort = new ArrayList<>(phases.size());

		for (EventPhaseData<T> phase : phases.values()) {
			forwardVisit(phase, null, toposort);
		}

		clearStatus(toposort);
		Collections.reverse(toposort);

		// SECOND VISIT
		for (EventPhaseData<T> phase : toposort) {
			backwardVisit(phase);
		}

		clearStatus(toposort);
	}

	private void forwardVisit(EventPhaseData<T> phase, EventPhaseData<T> parent, List<EventPhaseData<T>> toposort) {
		if (phase.visitStatus == 0) {
			// Not yet visited.
			phase.visitStatus = 1;

			for (EventPhaseData<T> data : phase.subsequentPhases) {
				forwardVisit(data, phase, toposort);
			}

			toposort.add(phase);
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

	private void clearStatus(List<EventPhaseData<T>> phases) {
		for (EventPhaseData<T> phase : phases) {
			phase.visitStatus = 0;
		}
	}

	private void backwardVisit(EventPhaseData<T> phase) {
		if (phase.visitStatus == 0) {
			phase.visitStatus = 1;
			sortedPhases.add(phase);

			for (EventPhaseData<T> data : phase.previousPhases) {
				backwardVisit(data);
			}
		}
	}

	private static class EventPhaseData<T> {
		final Identifier id;
		T[] listeners;
		final List<EventPhaseData<T>> subsequentPhases = new ArrayList<>();
		final List<EventPhaseData<T>> previousPhases = new ArrayList<>();
		int visitStatus = 0; // 0: not visited, 1: visiting, 2: visited

		@SuppressWarnings("unchecked")
		private EventPhaseData(Identifier id, Class<?> listenerClass) {
			this.id = id;
			this.listeners = (T[]) Array.newInstance(listenerClass, 0);
		}

		private void addListener(T listener) {
			int oldLength = listeners.length;
			listeners = Arrays.copyOf(listeners, oldLength + 1);
			listeners[oldLength] = listener;
		}
	}
}
