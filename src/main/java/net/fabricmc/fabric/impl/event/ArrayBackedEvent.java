/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.impl.event;

import net.fabricmc.fabric.api.event.Event;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Function;

class ArrayBackedEvent<T> extends Event<T> {
	private final Class<T> type;
	private final Function<T[], T> joiner;
	private final T dummyInvoker;
	private T[] handlers;

	ArrayBackedEvent(Class<T> type, T dummyInvoker, Function<T[], T> joiner) {
		this.type = type;
		this.dummyInvoker = dummyInvoker;
		this.joiner = joiner;
		update();
	}

	void update() {
		if (handlers == null) {
			if (dummyInvoker != null) {
				invoker = dummyInvoker;
			} else {
				//noinspection unchecked
				invoker = joiner.apply((T[]) Array.newInstance(type, 0));
			}
		} else if (handlers.length == 1) {
			invoker = handlers[0];
		} else {
			invoker = joiner.apply(handlers);
		}
	}

	@Override
	public void register(T listener) {
		if (listener == null) {
			throw new NullPointerException("Tried to register a null listener!");
		}

		if (handlers == null) {
			//noinspection unchecked
			handlers = (T[]) Array.newInstance(type, 1);
			handlers[0] = listener;
		} else {
			handlers = Arrays.copyOf(handlers, handlers.length + 1);
			handlers[handlers.length - 1] = listener;
		}

		update();
	}
}
