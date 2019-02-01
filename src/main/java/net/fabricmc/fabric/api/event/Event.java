package net.fabricmc.fabric.api.event;

public abstract class Event<T> {
	protected T invoker;

	public final T invoker() {
		return invoker;
	}

	public abstract void register(T listener);
}
