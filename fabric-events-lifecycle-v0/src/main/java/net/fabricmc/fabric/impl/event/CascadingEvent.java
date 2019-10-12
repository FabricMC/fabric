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
	private final List<Event<E>> descendants = new ArrayList<>();
	private final List<E> callbacks = new ArrayList<>();

	/**
	 * Creates a {@code CascadingEvent} that uses another event's {@link Event#invoker invoker}.
	 *
	 * @param delegate the event to decorate
	 */
	public CascadingEvent(Event<E> delegate) {
		this.delegate = delegate;
		this.invoker = this.delegate.invoker();
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p> The listener will also be registered to this event's delegate as well as any cascading
	 * event registered at a past or future time.
	 *
	 * @see #registerDescendant(Event)
	 */
	@Override
	public void register(E listener) {
		this.delegate.register(listener);
		this.invoker = this.delegate.invoker();
		this.callbacks.add(listener);
		for (Event<E> cascade : this.descendants) {
			cascade.register(listener);
		}
	}

	/**
	 * Registers an event as descendant of this {@code CascadingEvent}.
	 *
	 * <p> The {@code descendant} is added to this event's inheritance tree with this event as the direct ancestor.
	 * It will then receive any {@link #register(Object) callback registration} made to its ancestors.
	 * This method also immediately registers any callback that has been previously registered to this event to
	 * the {@code descendant}, allowing descendants to be registered after callback registrations have occurred.
	 *
	 * @param descendant an event to register as descendant of this event
	 */
	public void registerDescendant(Event<E> descendant) {
		this.descendants.add(descendant);
		for (E callback : this.callbacks) {
			descendant.register(callback);
		}
	}
}
