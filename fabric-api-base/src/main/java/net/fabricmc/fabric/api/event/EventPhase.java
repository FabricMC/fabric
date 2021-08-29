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

import net.minecraft.util.Identifier;

/**
 * An event phase is a named group of event listeners, which may be ordered before or after other groups of listeners.
 * This allows some listeners to take priority over other listeners.
 * Adding separate events should be considered before making use of multiple event phases.
 *
 * <p>Phases may be freely added to any event, however using the default phases passed to {@link EventFactory#createWithPhases}
 * is preferred to manually adding them. If more phases are necessary, discussion with the author of the Event is encouraged.
 */
public interface EventPhase<T> {
	/**
	 * The identifier of the default phase.
	 */
	Identifier DEFAULT = new Identifier("fabric", "default");

	/**
	 * Register a listener to this event phase.
	 *
	 * @param listener The desired listener.
	 */
	void register(T listener);

	/**
	 * Request that listeners registered to this phase be ran before listeners registered to some other phases.
	 * Relying on the default phases supplied to {@link EventFactory#createWithPhases} should be preferred over manually
	 * registering phase ordering dependencies.
	 *
	 * <p>Incompatible ordering constraints such as cycles will lead to inconsistent ordering of listeners inside the cycle.
	 * If this happens, a warning will be logged.
	 *
	 * @param subsequentPhases Identifiers of the phases that should run AFTER this one.
	 */
	void runBefore(Identifier... subsequentPhases);

	/**
	 * Request that listeners registered to this phase be ran after listeners registered to some other phases.
	 *
	 * @param previousPhases Identifiers of the phases that should run BEFORE this one.
	 * @see #runBefore
	 */
	void runAfter(Identifier... previousPhases);
}
