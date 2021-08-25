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
 * Base class for Event implementations.
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
	 * Register a listener to the event.
	 *
	 * @param listener The desired listener.
	 */
	public void register(T listener) {
		register(DEFAULT_PHASE, listener);
	}

	public abstract void register(Identifier phase, T listener);

	public static final Identifier DEFAULT_PHASE = new Identifier("fabric:default");

	public abstract void registerPhase(Identifier phaseIdentifier, PhaseDependency... dependencies);

	public static final class PhaseDependency {
		public static PhaseDependency before(Identifier otherPhase) {
			return new PhaseDependency(otherPhase, true);
		}

		public static PhaseDependency after(Identifier otherPhase) {
			return new PhaseDependency(otherPhase, false);
		}

		public final Identifier otherPhase;
		public final boolean before;

		private PhaseDependency(Identifier otherPhase, boolean before) {
			this.otherPhase = otherPhase;
			this.before = before;
		}

		@Override
		public String toString() {
			return "PhaseDependency{" + "otherPhase=" + otherPhase + ", before=" + before + '}';
		}
	}
}
