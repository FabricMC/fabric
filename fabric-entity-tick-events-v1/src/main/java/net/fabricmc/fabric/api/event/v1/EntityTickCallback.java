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

package net.fabricmc.fabric.api.event.v1;

import net.minecraft.entity.Entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.impl.event.entity.TickEventInternals;

/**
 * The callback interface for receiving entity ticking events.
 *
 * <p>The element that is interested in reacting
 * to entity ticks implements this interface, and is
 * registered with an entity class' event, using {@link Event#register(Object)}.
 * When an entity of an applicable type gets ticked, the callback's
 * {@code tick} method is invoked.
 *
 * <p>Entity tick callbacks are registered per entity class and apply to
 * instances of that class and of every subclass. More formally, if a callback
 * is registered for a class {@code E}, its {@code tick} method will
 * be invoked for any entity {@code e} verifying {@code e instanceof E}.
 *
 * @param <E> the type of entity targeted by this callback
 * @see #event(Class)
 */
@FunctionalInterface
public interface EntityTickCallback<E extends Entity> {
	/**
	 * Returns the {@code Event} used to register tick callbacks for
	 * entities of the given type.
	 *
	 * <p>Callers of this method should always use the most specific entity
	 * type for their use. For example, a callback which goal is to add some behaviour
	 * to players should pass {@code PlayerEntity.class} as a parameter,
	 * not one of its superclasses. This limits the need for entity-dependant
	 * checks, as well as the amount of redundant callback invocations.
	 * For these reasons, when registering callbacks for various entity types,
	 * it is often better to register a separate specialized callback for each type
	 * than a single generic callback with additional checks.
	 *
	 * @param type the class object representing the desired type of entity
	 * @param <E>  the type of entities targeted by the event
	 * @return the {@code Event} used to register tick callbacks for entities
	 * of the given type.
	 */
	static <E extends Entity> Event<EntityTickCallback<E>> event(Class<E> type) {
		return TickEventInternals.getOrCreateEntityEvent(type);
	}

	/**
	 * Called at the end of {@link Entity#tick()} for every entity that
	 * {@link Class#isInstance(Object) is an instance} of {@code E}.
	 *
	 * @param entity the entity that is being ticked
	 * @implNote because this method is called every tick for every entity of an appropriate type,
	 * implementations should limit the amount of operations run every call, and avoid expensive computations.
	 */
	void tick(E entity);
}
