package net.fabricmc.fabric.impl.event;

import net.fabricmc.fabric.api.event.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@code Event} that cascades registrations to other events
 *
 * @param <T> the type of handler registered to this {@code Event}
 */
public final class CascadingEvent<T> extends Event<T> {
	private final Event<T> delegate;
	private final List<Event<T>> descendants = new ArrayList<>();
	private final List<T> handlers = new ArrayList<>();

	/**
	 * Creates a {@code CascadingEvent} that uses another event's {@link Event#invoker invoker}.
	 *
	 * @param delegate the event to decorate
	 */
	public CascadingEvent(Event<T> delegate) {
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
	public void register(T listener) {
		this.delegate.register(listener);
		this.invoker = this.delegate.invoker();
		this.handlers.add(listener);
		for (Event<T> cascade : this.descendants) {
			cascade.register(listener);
		}
	}

	/**
	 * Registers an event as descendant of this {@code CascadingEvent}.
	 *
	 * <p> The {@code descendant} is added to this event's inheritance tree with this event as the direct ancestor.
	 * It will then receive any {@link #register(Object) handler registration} made to its ancestors.
	 * This method also immediately registers any handler that has been previously registered to this event to
	 * the {@code descendant}, allowing descendants to be registered after handler registrations have occurred.
	 *
	 * @param descendant an event to register as descendant of this event
	 */
	public void registerDescendant(Event<T> descendant) {
		this.descendants.add(descendant);
		for (T handler : this.handlers) {
			descendant.register(handler);
		}
	}
}
