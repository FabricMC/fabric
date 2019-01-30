package net.fabricmc.fabric.api.event.listener;

@FunctionalInterface
public interface ListenerChainer<T> {
	T chain(T[] listeners);
}
