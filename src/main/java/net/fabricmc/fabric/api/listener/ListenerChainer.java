package net.fabricmc.fabric.api.listener;

@FunctionalInterface
public interface ListenerChainer<T> {
	T chain(T[] listeners);
}
