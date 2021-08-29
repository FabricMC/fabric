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
import net.fabricmc.fabric.api.event.EventPhase;

class ArrayBackedEvent<T> extends Event<T> {
	private static final Logger LOGGER = LogManager.getLogger("fabric-api-base");

	private final Function<T[], T> invokerFactory;
	private final Object lock = new Object();
	private T[] handlers;
	/**
	 * Registered event phases.
	 */
	private final Map<Identifier, EventPhaseData> phases = new LinkedHashMap<>();
	/**
	 * Phases sorted in the correct dependency order.
	 */
	private final List<EventPhaseData> sortedPhases = new ArrayList<>();

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
	public EventPhase<T> phase(Identifier phaseIdentifier) {
		Objects.requireNonNull(phaseIdentifier, "Tried to retrieve a null phase.");

		synchronized (lock) {
			return getOrCreatePhase(phaseIdentifier);
		}
	}

	private EventPhaseData getOrCreatePhase(Identifier id) {
		EventPhaseData phase = phases.get(id);

		if (phase == null) {
			phase = new EventPhaseData(id, handlers.getClass().getComponentType());
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

			for (EventPhaseData existingPhase : sortedPhases) {
				for (T handler : existingPhase.listeners) {
					newHandlers[newHandlersIndex++] = handler;
				}
			}

			handlers = newHandlers;
		}

		// Rebuild invoker.
		update();
	}

	/**
	 * Uses a modified Kosaraju SCC to sort the phases.
	 */
	private void sortPhases() {
		sortedPhases.clear();

		// FIRST VISIT
		List<EventPhaseData> toposort = new ArrayList<>(phases.size());

		for (EventPhaseData phase : phases.values()) {
			forwardVisit(phase, null, toposort);
		}

		clearStatus(toposort);
		Collections.reverse(toposort);

		// SECOND VISIT
		for (EventPhaseData phase : toposort) {
			backwardVisit(phase);
		}

		clearStatus(toposort);
	}

	private void forwardVisit(EventPhaseData phase, EventPhaseData parent, List<EventPhaseData> toposort) {
		if (phase.visitStatus == 0) {
			// Not yet visited.
			phase.visitStatus = 1;

			for (EventPhaseData data : phase.subsequentPhases) {
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

	private void clearStatus(List<EventPhaseData> phases) {
		for (EventPhaseData phase : phases) {
			phase.visitStatus = 0;
		}
	}

	private void backwardVisit(EventPhaseData phase) {
		if (phase.visitStatus == 0) {
			phase.visitStatus = 1;
			sortedPhases.add(phase);

			for (EventPhaseData data : phase.previousPhases) {
				backwardVisit(data);
			}
		}
	}

	private class EventPhaseData implements EventPhase<T> {
		final Identifier id;
		T[] listeners;
		final List<EventPhaseData> subsequentPhases = new ArrayList<>();
		final List<EventPhaseData> previousPhases = new ArrayList<>();
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

		@Override
		public void register(T listener) {
			Objects.requireNonNull(listener, "Tried to register a null listener!");

			synchronized (lock) {
				addListener(listener);
				rebuildInvoker(handlers.length + 1);
			}
		}

		@Override
		public void runBefore(Identifier... subsequentPhases) {
			EventFactoryImpl.ensureNoDuplicatesNoNull(subsequentPhases);
			if (subsequentPhases.length == 0) throw new IllegalArgumentException("Must register at least one subsequent phase.");
			if (EventFactoryImpl.contains(subsequentPhases, id)) throw new IllegalArgumentException("Event phase may not depend on itself.");

			synchronized (lock) {
				for (Identifier other : subsequentPhases) {
					EventPhaseData second = getOrCreatePhase(other);
					this.subsequentPhases.add(second);
					second.previousPhases.add(this);
				}

				sortPhases();
				rebuildInvoker(handlers.length);
			}
		}

		@Override
		public void runAfter(Identifier... previousPhases) {
			EventFactoryImpl.ensureNoDuplicatesNoNull(previousPhases);
			if (previousPhases.length == 0) throw new IllegalArgumentException("Must register at least one previous phase.");
			if (EventFactoryImpl.contains(previousPhases, id)) throw new IllegalArgumentException("Event phase may not depend on itself.");

			synchronized (lock) {
				for (Identifier other : previousPhases) {
					EventPhaseData second = getOrCreatePhase(other);
					this.previousPhases.add(second);
					second.subsequentPhases.add(this);
				}

				sortPhases();
				rebuildInvoker(handlers.length);
			}
		}
	}
}
