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

package net.fabricmc.fabric.api.event;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.Identifier;

/**
 * Base class for Fabric's event implementations.
 *
 * @param <T> The listener type.
 * @see EventFactory
 */
@ApiStatus.NonExtendable // Should only be extended by fabric API.
public abstract class Event<T> {
	/**
	 * The invoker field. This should be updated by the implementation to
	 * always refer to an instance containing all code that should be
	 * executed upon event emission.
	 */
	protected volatile T invoker;

	/**
	 * Returns the invoker instance.
	 *
	 * <p>An "invoker" is an object which hides multiple registered
	 * listeners of type T under one instance of type T, executing
	 * them and leaving early as necessary.
	 *
	 * @return The invoker instance.
	 */
	public final T invoker() {
		return invoker;
	}

	/**
	 * Register a listener to the event, in the default phase.
	 * Have a look at {@link #addPhaseOrdering} for an explanation of event phases.
	 *
	 * @param listener The desired listener.
	 */
	public void register(T listener) {
		register(DEFAULT_PHASE, listener);
	}

	/**
	 * The identifier of the default phase.
	 * Have a look at {@link #addPhaseOrdering} for an explanation of event phases.
	 */
	public static final Identifier DEFAULT_PHASE = new Identifier("fabric:default");

	/**
	 * Register a listener to the event for the specified phase.
	 * Have a look at {@link #addPhaseOrdering} for an explanation of event phases.
	 *
	 * @param phase Identifier of the phase this listener should be registered for. It will be created if it didn't exist yet.
	 * @param listener The desired listener.
	 */
	public void register(Identifier phase, T listener) {
		throw new UnsupportedOperationException("Extending Event is not supported.");
	}

	/**
	 * Request that listeners registered for one phase be executed before listeners registered for another phase.
	 *
	 * <p>An event phase is a named group of listeners, which may be ordered before or after other groups of listeners.
	 * This allows some listeners to take priority over other listeners.
	 *
	 * <p>Separate events should be preferred to multiple event phases.
	 * Event phases should only be used when separate events are not possible or not practical.
	 *
	 * <p>Incompatible ordering constraints such as cycles will lead to inconsistent behavior:
	 * some constraints will be respected and some will be ignored. If this happens, a warning will be logged.
	 *
	 * @param firstPhase The identifier of the phase that should run before the other. It will be created if it didn't exist yet.
	 * @param secondPhase The identifier of the phase that should run after the other. It will be created if it didn't exist yet.
	 */
	public void addPhaseOrdering(Identifier firstPhase, Identifier secondPhase) {
		throw new UnsupportedOperationException("Extending Event is not supported.");
	}
}
