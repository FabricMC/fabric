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

	private void update() {
		if (handlers == null) {
			invoker = dummyInvoker;
		} else if (handlers.length == 1) {
			invoker = handlers[0];
		} else {
			invoker = joiner.apply(handlers);
		}
	}

	@Override
	public void register(T listener) {
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
