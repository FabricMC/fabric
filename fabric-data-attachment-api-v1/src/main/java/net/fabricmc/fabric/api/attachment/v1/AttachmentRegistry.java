/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.attachment.v1;

import java.util.Objects;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.attachment.AttachmentRegistryImpl;

/**
 * Class used to create and register {@link AttachmentType}s. To quickly create {@link AttachmentType}s, use one of the various
 * {@code createXXX} methods:
 * <ul>
 *     <li>{@link #create(Identifier)}: attachments will be neither persistent nor auto-initialized.</li>
 *     <li>{@link #createDefaulted(Identifier, Supplier)}: attachments will be auto-initialized, but not persistent.</li>
 *     <li>{@link #createPersistent(Identifier, Codec)}: attachments will be persistent, but not auto-initialized.</li>
 * </ul>
 *
 * <p>For finer control over the attachment type and its properties, use {@link AttachmentRegistry#builder()} to
 * get a {@link Builder} instance.</p>
 */
@ApiStatus.Experimental
public final class AttachmentRegistry {
	private AttachmentRegistry() {
	}

	/**
	 * Creates <i>and registers</i> an attachment. The data will not be persisted.
	 *
	 * @param id  the identifier of this attachment
	 * @param <A> the type of attached data
	 * @return the registered {@link AttachmentType} instance
	 */
	public static <A> AttachmentType<A> create(Identifier id) {
		Objects.requireNonNull(id, "identifier cannot be null");

		return AttachmentRegistry.<A>builder().buildAndRegister(id);
	}

	/**
	 * Creates <i>and registers</i> an attachment, that will be automatically initialized with a default value
	 * when an attachment does not exist on a given target, using {@link AttachmentTarget#getAttachedOrCreate(AttachmentType)}.
	 *
	 * @param id          the identifier of this attachment
	 * @param initializer the initializer used to provide a default value
	 * @param <A>         the type of attached data
	 * @return the registered {@link AttachmentType} instance
	 */
	public static <A> AttachmentType<A> createDefaulted(Identifier id, Supplier<A> initializer) {
		Objects.requireNonNull(id, "identifier cannot be null");
		Objects.requireNonNull(initializer, "initializer cannot be null");

		return AttachmentRegistry.<A>builder()
				.initializer(initializer)
				.buildAndRegister(id);
	}

	/**
	 * Creates <i>and registers</i> an attachment, that will persist across server restarts.
	 *
	 * @param id    the identifier of this attachment
	 * @param codec the codec used for (de)serialization
	 * @param <A>   the type of attached data
	 * @return the registered {@link AttachmentType} instance
	 */
	public static <A> AttachmentType<A> createPersistent(Identifier id, Codec<A> codec) {
		Objects.requireNonNull(id, "identifier cannot be null");
		Objects.requireNonNull(codec, "codec cannot be null");

		return AttachmentRegistry.<A>builder().persistent(codec).buildAndRegister(id);
	}

	/**
	 * Creates a {@link Builder}, that gives finer control over the attachment's properties.
	 *
	 * @param <A> the type of the attached data
	 * @return a {@link Builder} instance
	 */
	public static <A> Builder<A> builder() {
		return AttachmentRegistryImpl.builder();
	}

	/**
	 * A builder for creating {@link AttachmentType}s with finer control over their properties.
	 *
	 * <p>Note on entity attachments: sometimes, the game needs to copy data between two different entity instances.
	 * This happens for example on player respawn, when a mob converts to another type, or when the player returns from the End.
	 * Since one entity instance is discarded, it is imperative that the attached data doesn't hold a reference to the old instance.</p>
	 * <ul>
	 *     <li>If a {@link Codec codec} is provided using {@link #codec(Codec)}, it will automatically be used for copying data.</li>
	 *     <li>If finer control is desired, a {@link AttachmentType.EntityCopyHandler custom copy handler} can be specified using {@link #entityCopyHandler(AttachmentType.EntityCopyHandler)}.</li>
	 *     <li>If neither are provided, <i>no attempt at copy will be made</i>, and attached data <b>will be lost</b>
	 *     in the situations outlined above. This can sometimes be useful for even finer control over copying, but
	 *     is generally undesirable for attachment types used on entities.</li>
	 * </ul>
	 *
	 * @param <A> the type of the attached data
	 * @see #copyOnDeath()
	 * @see #copyOnDeath(Codec)
	 * @see #entityCopyHandler(AttachmentType.EntityCopyHandler)
	 */
	public interface Builder<A> {
		/**
		 * Declares that attachments corresponding to this type should persist between server restarts,
		 * using the provided {@link Codec} for (de)serialization.
		 *
		 * <p>A shorthand for {@code persistent().codec(codec)}, cannot be used in conjunction with {@link #copyOnDeath(Codec)},
		 * as {@link #codec(Codec)} can only be called once.</p>
		 *
		 * @param codec the codec used for (de)serialization
		 * @return the builder
		 * @see #codec(Codec)
		 */
		default Builder<A> persistent(Codec<A> codec) {
			return persistent().codec(codec);
		}

		/**
		 * Declares that attachments corresponding to this type should persist between server restarts. A codec must be
		 * declared using {@link #codec(Codec)} at some point, or {@link #buildAndRegister(Identifier)} will fail.
		 *
		 * @return the builder
		 */
		Builder<A> persistent();

		/**
		 * Declares that when an entity dies and respawns in some way, the attachments corresponding to this type
		 * should be copied, using the provided {@link Codec} to copy data
		 * between entity instances. This is used either when a player dies and respawns, or when a mob converts to another
		 * (for example, zombie → drowned, or zombie villager → villager).
		 *
		 * <p>A shorthand for {@code copyEntityAttachments().codec(codec)}, and cannot be used in conjunction with {@link #persistent(Codec)},
		 * as {@link #codec(Codec)} can only be called once.</p>
		 *
		 * @param codec a codec
		 * @return the builder
		 * @see #codec(Codec)
		 */
		default Builder<A> copyOnDeath(Codec<A> codec) {
			return copyOnDeath().codec(codec);
		}

		/**
		 * Declares that when a player dies and respawns, the attachments corresponding to this type should remain.
		 * When using this method, some method for attachment copying must be specified as explained in the description
		 * of {@link Builder}, otherwise {@link #buildAndRegister(Identifier)} will fail.
		 *
		 * @return the builder
		 * @see Builder
		 */
		Builder<A> copyOnDeath();

		/**
		 * Sets the {@link AttachmentType.EntityCopyHandler} for this attachment type, used when copying attachments between
		 * entity instances.
		 *
		 * @param copyHandler the copy handler
		 * @return the builder
		 */
		Builder<A> entityCopyHandler(AttachmentType.EntityCopyHandler<A> copyHandler);

		/**
		 * Sets the codec used for (de)serialization of this attachment type. Must only be called once during the
		 * builder's existence.
		 *
		 * @param codec the codec used for (de)serialization
		 * @return the builder
		 */
		Builder<A> codec(Codec<A> codec);

		/**
		 * Sets the default initializer for this attachment type. The initializer will be called by
		 * {@link AttachmentTarget#getAttachedOrCreate(AttachmentType)} to automatically initialize attachments that
		 * don't yet exist. It must not return {@code null}.
		 *
		 * <p>It is <i>encouraged</i> for {@link A} to be an immutable data type, such as a primitive type
		 * or an immutable record.</p>
		 *
		 * <p>Otherwise, one must be very careful, as attachments <i>must not share any mutable state</i>.
		 * As an example, for a (mutable) list/array attachment type,
		 * the initializer should create a new independent instance each time it is called.</p>
		 *
		 * @param initializer the initializer
		 * @return the builder
		 */
		Builder<A> initializer(Supplier<A> initializer);

		/**
		 * Builds and registers the {@link AttachmentType}.
		 *
		 * @param id the attachment's identifier
		 * @return the built and registered {@link AttachmentType}
		 */
		AttachmentType<A> buildAndRegister(Identifier id);
	}
}
