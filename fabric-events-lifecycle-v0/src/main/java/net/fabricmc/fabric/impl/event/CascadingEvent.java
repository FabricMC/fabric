package net.fabricmc.fabric.impl.event;

import net.fabricmc.fabric.api.event.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@code Event} that cascades registrations to other events
 *
 * @param <E> the type of callback registered to this {@code Event}
 */
public final class CascadingEvent<E> extends Event<E> {
	private final Event<E> delegate;
	private final List<Event<E>> cascades = new ArrayList<>();
	private final List<E> callbacks = new ArrayList<>();

	public CascadingEvent(Event<E> delegate) {
		this.delegate = delegate;
		this.invoker = this.delegate.invoker();
	}

	@Override
	public void register(E listener) {
		this.delegate.register(listener);
		this.invoker = this.delegate.invoker();
		this.callbacks.add(listener);
		for (Event<E> cascade : this.cascades) {
			cascade.register(listener);
		}
	}

	public void registerCascading(Event<E> cascading) {
		this.cascades.add(cascading);
		for (E callback : this.callbacks) {
			cascading.register(callback);
		}
	}
}
