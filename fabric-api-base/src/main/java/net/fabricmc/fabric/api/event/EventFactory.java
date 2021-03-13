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

import java.util.function.Function;

import net.fabricmc.fabric.impl.base.event.EventFactoryImpl;

/**
 * Helper for creating {@link Event} classes.
 */
public final class EventFactory {
	private static boolean profilingEnabled = true;

	private EventFactory() { }

	/**
	 * @return True if events are supposed to be profiled.
	 */
	public static boolean isProfilingEnabled() {
		return profilingEnabled;
	}

	/**
	 * Invalidate and re-create all existing "invoker" instances across
	 * events created by this EventFactory. Use this if, for instance,
	 * the profilingEnabled field changes.
	 */
	// TODO: Turn this into an event?
	public static void invalidate() {
		EventFactoryImpl.invalidate();
	}

	/**
	 * Create an "array-backed" Event instance.
	 * The factory will be used to create the invoker for any number of listeners.
	 *
	 * <p>Consider using {@linkplain #createArrayBacked(Class, Object, Function) a custom empty invoker}
	 * if performance of this event is critical.
	 *
	 * @param type           The listener class type.
	 * @param invokerFactory The invoker factory, combining multiple listeners into one instance.
	 * @param <T>            The listener type.
	 * @return The Event instance.
	 */
	public static <T> Event<T> createArrayBacked(Class<? super T> type, Function<T[], T> invokerFactory) {
		return EventFactoryImpl.createArrayBacked(type, invokerFactory);
	}

	/**
	 * Create an "array-backed" Event instance with a custom empty invoker.
	 * If there is no listener, the custom invoker will be used.
	 * If there is only one listener, that one will be used as the invoker.
	 * The factory will only be used when there at least two invokers.
	 *
	 * <p>Having a custom empty invoker (of type (...) -&gt; {}) increases performance
	 * relative to iterating over an empty array; however, it only really matters
	 * if the event is executed thousands of times a second.
	 *
	 * @param type           The listener class type.
	 * @param emptyInvoker   The custom empty invoker.
	 * @param invokerFactory The invoker factory, combining multiple listeners into one instance.
	 * @param <T>            The listener type.
	 * @return The Event instance.
	 */
	public static <T> Event<T> createArrayBacked(Class<T> type, T emptyInvoker, Function<T[], T> invokerFactory) {
		return createArrayBacked(type, invokers -> {
			if (invokers.length == 0) {
				return emptyInvoker;
			} else if (invokers.length == 1) {
				return invokers[0];
			} else {
				return invokerFactory.apply(invokers);
			}
		});
	}

	/**
	 * Get the listener object name. This can be used in debugging/profiling
	 * scenarios.
	 *
	 * @param handler The listener object.
	 * @return The listener name.
	 */
	public static String getHandlerName(Object handler) {
		return handler.getClass().getName();
	}
}
