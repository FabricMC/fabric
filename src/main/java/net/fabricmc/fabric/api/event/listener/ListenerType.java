package net.fabricmc.fabric.api.event.listener;

public abstract class ListenerType<T> {
	protected T reference;

	public final T get() {
		return reference;
	}

	public abstract void register(T listener);
}
