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
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;

class ArrayBackedEvent<T> extends Event<T> {
	private final Function<T[], T> invokerFactory;
	private final Object lock = new Object();
	private T[] handlers;
	/**
	 * Registered event phases.
	 */
	private final Map<Identifier, EventPhaseData<T>> phases = new HashMap<>();
	/**
	 * Subsequent dependencies for phases that have not yet been registered.
	 */
	private final Map<Identifier, List<Identifier>> unresolvedDependencies = new HashMap<>();
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

		registerPhase(DEFAULT_PHASE);

		update();
	}

	void update() {
		this.invoker = invokerFactory.apply(handlers);
	}

	@Override
	public void register(Identifier phaseIdentifier, T listener) {
		Objects.requireNonNull(phaseIdentifier, "Tried to register a listener for a null phase!");
		Objects.requireNonNull(listener, "Tried to register a null listener!");

		synchronized (lock) {
			EventPhaseData<T> phase = phases.get(phaseIdentifier);

			if (phase == null) {
				throw new IllegalArgumentException("Tried to register a listener for a non-existing phase: " + phaseIdentifier);
			} else {
				phase.listeners.add(listener);
			}

			@SuppressWarnings("unchecked")
			T[] newHandlers = (T[]) Array.newInstance(handlers.getClass().getComponentType(), handlers.length+1);
			int newHandlersIndex = 0;

			for (EventPhaseData<T> existingPhase : sortedPhases) {
				for (T handler : existingPhase.listeners) {
					newHandlers[newHandlersIndex++] = handler;
				}
			}

			handlers = newHandlers;
			update();
		}
	}

	@Override
	public void registerPhase(Identifier phaseIdentifier, PhaseDependency... dependencies) {
		Objects.requireNonNull(phaseIdentifier, "Tried to register a phase with a null id!");

		synchronized (lock) {
			EventPhaseData<T> existingPhase = phases.get(phaseIdentifier);

			if (existingPhase != null) {
				checkPhasesAreEqual(existingPhase, phaseIdentifier, dependencies);
			} else {
				addNewPhase(phaseIdentifier, dependencies);
			}
		}
	}

	private void checkPhasesAreEqual(EventPhaseData<T> existingPhase, Identifier newPhaseIdentifier, PhaseDependency... newDependencies) {
		for (PhaseDependency existingDep : existingPhase.dependencies) {
			if (!arrayContains(newDependencies, existingDep)) {
				throw new IllegalArgumentException(
						String.format(
								"Tried to register duplicate event phase %s, but it is missing the following dependency: %s.",
								newPhaseIdentifier,
								existingDep
						)
				);
			}
		}

		for (PhaseDependency newDep : newDependencies) {
			if (!arrayContains(existingPhase.dependencies, newDep)) {
				throw new IllegalArgumentException(
						String.format(
								"Tried to register duplicate event phase %s, but it has an additional dependency: %s.",
								newPhaseIdentifier,
								newDep
						)
				);
			}
		}
	}

	private static <T> boolean arrayContains(T[] array, T object) {
		for (T t : array) {
			if (t == object) {
				return true;
			}
		}

		return false;
	}

	private void addNewPhase(Identifier phaseIdentifier, PhaseDependency... dependencies) {
		for (PhaseDependency dependency : dependencies) {
			if (dependency.otherPhase.equals(phaseIdentifier)) {
				throw new IllegalArgumentException(
						String.format(
								"Event phase %s may not depend on itself.",
								phaseIdentifier
						)
				);
			}
		}

		EventPhaseData<T> newPhase = new EventPhaseData<>(dependencies);

		for (PhaseDependency dependency : dependencies) {
			if (dependency.before) {
				// Add to this phase directly.
				newPhase.subsequentPhases.add(dependency.otherPhase);
			} else {
				EventPhaseData<T> previousPhase = phases.get(dependency.otherPhase);

				if (previousPhase != null) {
					// Add to the other phase directly.
					previousPhase.subsequentPhases.add(phaseIdentifier);
				} else {
					// Add to the unresolved dependencies so it can be resolved when the phase is added.
					unresolvedDependencies.computeIfAbsent(dependency.otherPhase, id -> new ArrayList<>()).add(phaseIdentifier);
				}
			}
		}

		List<Identifier> unresolvedSubsequentPhases = unresolvedDependencies.remove(phaseIdentifier);

		if (unresolvedSubsequentPhases != null) {
			newPhase.subsequentPhases.addAll(unresolvedSubsequentPhases);
		}

		phases.put(phaseIdentifier, newPhase);

		sortPhases();
	}

	private void sortPhases() {
		sortedPhases.clear();
		visitedPhases.clear();

		for (EventPhaseData<T> phase : phases.values()) {
			visitPhase(phase);
		}

		Collections.reverse(sortedPhases);
	}

	private void visitPhase(@Nullable EventPhaseData<T> phase) {
		if (phase != null && visitedPhases.add(phase)) {
			for (Identifier subsequentId : phase.subsequentPhases) {
				visitPhase(phases.get(subsequentId));
			}

			sortedPhases.add(phase);
		}
	}
}
