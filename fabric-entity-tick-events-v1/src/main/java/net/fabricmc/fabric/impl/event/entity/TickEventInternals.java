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

package net.fabricmc.fabric.impl.event.entity;

import java.util.IdentityHashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import net.minecraft.entity.Entity;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.v1.EntityTickCallback;

public class TickEventInternals {
	/** Cache of entity class to {@code Event}. */
	private static final Map<Class<?>, CascadingEvent<?>> ENTITY_TICK_EVENTS = new IdentityHashMap<>();

	/**
	 * Retrieves an entity tick event for a given class. If none exists, this method creates one and registers it
	 * as a cascading child of the superclass' event.
	 *
	 * <p>The returned {@code Event} will have new callbacks registered automatically whenever one is registered
	 * to a parent event.
	 *
	 * @return a tick {@code Event} for entities of the given class
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Entity> CascadingEvent<EntityTickCallback<E>> getOrCreateEntityEvent(Class<E> entityClass) {
		Preconditions.checkArgument(Entity.class.isAssignableFrom(entityClass), "Cannot register entity tick callback to non-entity class. Found: " + entityClass.getName());
		CascadingEvent<EntityTickCallback<E>> event = (CascadingEvent<EntityTickCallback<E>>) ENTITY_TICK_EVENTS.get(entityClass);

		if (event == null) {
			event = new CascadingEvent<>(createEntityEvent());    // decorator around the base Event

			if (entityClass != Entity.class) {    // entities cannot inherit callbacks from anything
				Class<E> superclass = (Class<E>) entityClass.getSuperclass();
				getOrCreateEntityEvent(superclass).registerDescendant(event);    // recursive call to init every parent
			}

			ENTITY_TICK_EVENTS.put(entityClass, event);
		}

		return event;
	}

	/**
	 * Creates an {@code Event} for entity tick callbacks.
	 *
	 * <p>This method has no side effects.
	 *
	 * @return a new Event to which {@code EntityTickCallback} can be registered
	 */
	private static <E extends Entity> Event<EntityTickCallback<E>> createEntityEvent() {
		return EventFactory.createArrayBacked(EntityTickCallback.class, listeners -> {
				if (EventFactory.isProfilingEnabled()) {
					return entity -> {
						Profiler profiler = entity.getEntityWorld().getProfiler();
						profiler.push("fabricEntityTick");

						for (EntityTickCallback<E> event : listeners) {
							profiler.push(EventFactory.getHandlerName(event));
							event.tick(entity);
							profiler.pop();
						}

						profiler.pop();
					};
				} else {
					return entity -> {
						for (EntityTickCallback<E> event : listeners) {
							event.tick(entity);
						}
					};
				}
			});
	}
}
