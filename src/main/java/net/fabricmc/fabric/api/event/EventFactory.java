package net.fabricmc.fabric.api.event;

import net.fabricmc.fabric.impl.event.EventFactoryImpl;

import java.util.function.Function;

public final class EventFactory {
	private EventFactory() {

	}

	public static <T> Event<T> arrayBacked(Class<T> type, Function<T[], T> joiner) {
		return EventFactoryImpl.arrayBacked(type, joiner);
	}

	public static <T> Event<T> arrayBacked(Class<T> type, T emptyInvoker, Function<T[], T> joiner) {
		return EventFactoryImpl.arrayBacked(type, emptyInvoker, joiner);
	}
}
