package net.fabricmc.fabric.api.attachment.v1;

import java.util.function.Supplier;

import org.jetbrains.annotations.ApiStatus;

/**
 * An {@link AttachmentType} that will automatically initialize attachments with a default value when queried for the first time.
 *
 * @param <A>
 * @see AttachmentTarget#getAttached(DefaultedAttachmentType)
 */
@ApiStatus.NonExtendable
public interface DefaultedAttachmentType<A> extends AttachmentType<A> {
	/**
	 * If an object has no value associated to an attachment,
	 * this initializer is used to create a non-{@code null} starting value.
	 *
	 * <p>It is <i>encouraged</i> for {@link A} to be an immutable data type, such as a primitive type
	 * or an immutable record.</p>
	 *
	 * <p>Otherwise, one must be very careful, as attachments <i>must not share any mutable state</i>.
	 * As an example, for a (mutable) list/array attachment type,
	 * the initializer should create a new independent instance each time it is called.</p>
	 *
	 * @return the initializer for this attachment
	 */
	Supplier<A> initializer();
}
