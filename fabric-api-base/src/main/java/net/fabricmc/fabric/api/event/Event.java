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

/**
 * Base class for Event implementations.
 *
 * @param <T> The listener type.
 * @see EventFactory
 */
public abstract class Event<T> {
	/**
	 * The invoker field. This should be updated by the implementation to
	 * always refer to an instance containing all code that should be
	 * executed upon event emission.
	 */
	protected T invoker;

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
	 * Some events can be optimized away if nobody is listening. If an Event
	 * implementation can quickly check for this condition, it may override this
	 * method to return false in that case.
	 *
	 * @return True if the event might have any listeners.
	 */
	public boolean hasListeners() {
		return true;
	}

	/**
	 * Register a listener to the event.
	 *
	 * @param listener The desired listener.
	 */
	public abstract void register(T listener);
}
